package Server;

import GameParts.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

public class ClientHandler extends Thread {

    private RoomHashMap roomHashMap;
    private Socket socket;
    private Player player;

    /**
     * Server constructor
     * @param socket Socket that communication will be conducted over
     * @param roomHashMap Hash map that holds all the rooms
     */
    ClientHandler(Socket socket, RoomHashMap roomHashMap) {
        this.roomHashMap = roomHashMap;
        this.socket = socket;
    }


    /**
     * Sends back a good response when successful
     * @param key Secret key to send
     */
    private void sendGoodResponse(String key){

        // payload not supplied
        JSONObject goodResp = new JSONObject();
        goodResp.put("status", "good");

        // send response
        sendJSONString(goodResp.toString(), key);
    }


    /**
     * Gets JSON from the client which will tell server what to do
     * @return JSON string
     */
    private JSONObject getJSON() {

        try {
            JSONObject jo = null;
            String key = null;

            while(jo == null) {

                BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                // Read until we hit 2 CRLFs signaling end of headers
                String header;
                try {
                    while(!(header = bf.readLine()).equals("")) {
                        // Get the web socket secret key
                        if(header.contains("Sec-WebSocket-Key:"))
                            key = header.substring(header.indexOf(":") + 2);
                    }
                } catch (NullPointerException e) {
                    continue;
                }

                // Until the client sends good json keep asking after alerting of bad JSON
                try {

                    // read the line and try to parse the json
                    String jsonString = bf.readLine();
                    jo = new JSONObject(jsonString);

                } catch (JSONException | NullPointerException e) {

                    // JSON error occurred, tell client it was a bad request.
                    sendBadResponse("jsonParsingError", key);
                    jo = null;
                }
            }

            if(key != null)
                jo.put("key", key);

            return jo;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Generates the necessary headers for the responses
     * @param key Secret key shared at beginning of previos request
     * @return String containing the beginning responses
     */
    private String getWebSocketResponseHeaders(String key) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1 101 Ok\r\n");
        buffer.append("Upgrade: websocket\r\n");
        buffer.append("Connection: upgrade\r\n");

        try {
            String digestStr = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(digestStr.getBytes());
            key = Base64.getEncoder().encodeToString(messageDigest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        buffer.append("Sec-WebSocket-Accept: " + key + "\r\n\r\n");

        return buffer.toString();
    }


    /**
     * Sends JSON back to the client
     * @param json JSON string to sent
     * @param key Secret key
     */
    private void sendJSONString(String json, String key) {

        try {
            // Create the new writing object and send the message on its way
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bf.write(getWebSocketResponseHeaders(key) + json + "\r\n");
            bf.flush();
        } catch (IOException e) {
            System.err.println("Error sending response");
        }
    }


    /**
     * Builds a response to a bad JSON request
     * @param message Message to be sent
     * @param key Secret key
     */
    private void sendBadResponse(String message, String key) {

        // payload not supplied
        JSONObject badResp = new JSONObject();
        badResp.put("status", "bad");
        badResp.accumulate("payload", new HashMap<>(){{
            put("errorMsg", message);
        }});

        // send response
        sendJSONString(badResp.toString(), key);
    }


    /**
     * Handles each client
     */
    @Override
    public void run(){

        JSONObject json = getJSON();

        while(true){

            // Get JSON from the client
            JSONObject jo;
            try {
                jo = getJSON();
            } catch (NullPointerException e) {

                // This will only be encountered if client disconnects
                // so get out of function
                return;
            }

            String action;
            try {

                // Try to get the action object in the JSON string
                action = jo.getString("action");
            } catch (NullPointerException | JSONException e){

                // Action was not included in the message
                sendBadResponse("actionNotFound", jo.getString("key"));
                continue;
            }

            // Get the action object
            if(action.equals("create")){

                // If this player is not created yet they must send a name
                if(this.player == null) {
                    sendBadResponse("mustLogIn", jo.getString("key"));
                    continue;
                }

                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload", jo.getString("key"));
                    continue;
                }

                // Check if this room already exists
                if(this.roomHashMap.containsRoom(roomId)) {

                    sendBadResponse("roomAlreadyExists", jo.getString("key"));
                    continue;
                } else {

                    // Create the room
                    Room room = new Room(jo.getJSONObject("payload").getString("roomId"));

                    // This player is the host
                    room.setHost(this.player);
                    room.getPlayers().add(this.player);

                    // Add the room to the hash map
                    this.roomHashMap.addRoom(room);

                    // Assign the room to the player
                    this.player.setRoom(room);
                }

            } else if(action.equals("join")){

                // If this player is not created yet they must send a name
                if(this.player == null) {
                    sendBadResponse("mustLogIn", jo.getString("key"));
                    continue;
                }

                // Join room
                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload", jo.getString("key"));
                    continue;
                }

                // Check if the room exists
                if(!this.roomHashMap.containsRoom(roomId)){

                    sendBadResponse("roomDoesNotExist", jo.getString("key"));
                    continue;
                }

                // Get the room
                Room room = this.roomHashMap.getRoom(roomId);
                try {

                    // Check to see if the player is already in the room
                    if(room.getPlayers().contains(this.player)) {

                        sendBadResponse("playerAlreadyInRequestedRoom", jo.getString("key"));
                        continue;
                    } else {

                        room.addPlayer(this.player);
                    }

                } catch (RoomFullException e) {

                    sendBadResponse("requestedRoomFull", jo.getString("key"));
                    continue;
                }

            } else if(action.equals("start")){

                // If this player is not created yet they must send a name
                if(this.player == null) {
                    sendBadResponse("mustLogIn", jo.getString("key"));
                    continue;
                }

                // Start game
                try {
                    this.player.getRoom().startGame(this.player);
                } catch (NotEnoughPlayersException e) {

                    // The room does not have at least 2 players
                    sendBadResponse("notEnoughPlayers", jo.getString("key"));
                    continue;
                } catch (GameAlreadyStartedException e) {

                    // The game is already started and cannot be started again
                    sendBadResponse("gameStartedAlready", jo.getString("key"));
                    continue;
                } catch (InsufficientPrivilegesException e) {

                    // The person who tried to start the program is not
                    // the host of the room
                    sendBadResponse("insufficientPrivileges", jo.getString("key"));
                    continue;
                }

            } else if(action.equals("play")) {

                // If this player is not created yet they must send a name
                if(this.player == null) {
                    sendBadResponse("mustLogIn", jo.getString("key"));
                    continue;
                }

                int cardIndex;
                try {

                    // Get the index of the card
                    cardIndex = jo.getJSONObject("payload").getInt("cardIndex");
                } catch (JSONException e) {

                    // Card index not supplied or is not an integer
                    sendBadResponse("invalidCardIndex", jo.getString("key"));
                    continue;
                }

                // Check if the game has been started
                if(!player.getRoom().isGameStarted()) {

                    sendBadResponse("gameNotStarted", jo.getString("key"));
                    continue;
                }

                // Make sure card index is within the correct range
                if(cardIndex < 0 || cardIndex >= player.getHand().size()) {

                    sendBadResponse("invalidCardIndex", jo.getString("key"));
                    continue;
                }

                // get the card
                Card c = this.player.getHand().get(cardIndex);

                // If the card is a wilcard we need to tell playCard the
                // new color of the pile
                if(c.getType() == CardType.WILDCARD) {
                    try {

                        // Get the colorStr from the JSON
                        String colorStr = jo.getJSONObject("payload").getString("wilcardColor");

                        Color newColor;
                        try {
                            // Turn that color into the correct enum
                            newColor = Color.valueOf(colorStr);

                            if(newColor == Color.WILDCARD) {

                                // new color cannot be wildcard
                                sendBadResponse("illegalColorOption", jo.getString("key"));
                                continue;
                            }
                        } catch (IllegalArgumentException e) {

                            // Color offered was not in the enum
                            sendBadResponse("illegalColorOption", jo.getString("key"));
                            continue;
                        }

                        try {

                            // try to play the card
                            this.player.getRoom().playCard(this.player, cardIndex, newColor);
                        } catch (IllegalCardException e) {

                            // Card that was played could not be played for any
                            // number of reasons
                            sendBadResponse("illegalCardPlayed", jo.getString("key"));
                            continue;
                        } catch (IllegalPlayException e) {

                            // not this player's turn to play
                            sendBadResponse("illegalPlayException", jo.getString("key"));
                            continue;
                        }

                    } catch (JSONException e) {

                        sendBadResponse("wildcardColorMissing", jo.getString("key"));
                        continue;
                    }

                } else {

                    try {

                        // try to play the card
                        this.player.getRoom().playCard(this.player, cardIndex, null);
                    } catch (IllegalCardException e) {

                        // Card that was played could not be played for any
                        // number of reasons
                        sendBadResponse("illegalCardPlayed", jo.getString("key"));
                        continue;
                    } catch (IllegalPlayException e) {

                        // not this player's turn to play
                        sendBadResponse("illegalPlayException", jo.getString("key"));
                        continue;
                    }
                }

            } else if (action.equals("name")) {

                // If this player is already created fail
                if(this.player != null) {
                    sendBadResponse("alreadyLoggedIn", jo.getString("key"));
                    continue;
                }

                try {
                    // Create the player object
                    this.player = new Player(jo.getJSONObject("payload").getString("name"));
                    sendGoodResponse(jo.getString("key"));
                } catch (JSONException e) {

                    sendBadResponse("badPayload", jo.getString("key"));
                    continue;
                }

            }  else {

                sendBadResponse("unknownRequest", jo.getString("key"));
                continue;
            }

            // Check if the player has a room before checking if
            // game is over. Otherwise just continue
            if(this.player.getRoom() != null){

                // If the game is not started yet ignore
                if(!this.player.getRoom().isGameStarted()){
                    continue;
                }

                // Check if game is over
                if(this.player.getRoom().isGameOver()){

                    // Tell all other players that the game is over
                    for(Player p : this.player.getRoom().getPlayers()){

                        // Alert other players the game is over
                        JSONObject j = new JSONObject();
                        j.put("status", "good");

                        // If this player is the one that won tell them they won
                        if(p.getHand().size() == 0) {

                            j.put("payload", new HashMap<>(){{
                                put("gameStatus", "over");
                                put("userWon", "true");
                            }});
                        } else {
                            j.put("payload", new HashMap<>(){{
                                put("gameStatus", "complete");
                                put("userWon", "false");
                            }});
                        }

                        // send the response
                        sendJSONString(j.toString(), jo.getString("key"));
                    }

                    // Remove the room from the list of rooms
                    this.roomHashMap.removeRoom(this.player.getRoom().getId());
                    break;
                }
            }

            // If this is reached everything is all good
            sendGoodResponse(jo.getString("key"));
        }

        // Close the connection for this player
        try {
            this.socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket connection");
        }
    }
}

