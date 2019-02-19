package Server;

import GameParts.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private RoomHashMap roomHashMap;
    private Socket socket;
    private Player player;

    /**
     * Server constructor
     * @param player Player that will be using this server
     * @param socket Socket that communication will be conducted over
     * @param roomHashMap Hash map that holds all the rooms
     */
    Server(Player player, Socket socket, RoomHashMap roomHashMap){
        this.roomHashMap = roomHashMap;
        this.player = player;
        this.socket = socket;
    }


    /**
     * Gets JSON from the client which will tell server what to do
     * @return JSON string
     */
    private JSONObject getJSON() {

        try {
            JSONObject jo = null;
            BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Until the client sends good json keep asking after alerting of bad JSON
            while(jo == null) {

                try {

                    // read the line and try to parse the json
                    String jsonString = bf.readLine();
                    jo = new JSONObject(jsonString);

                } catch (JSONException e) {

                    // JSON error occurred, tell client it was a bad request.
                    sendBadResponse("jsonParsingError");
                    jo = null;
                }
            }

            return jo;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Sends JSON back to the client
     * @param json JSON string to sent
     */
    private void sendJSONString(String json) {

        try {
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bf.write(json);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Builds a response to a bad JSON request
     * @param message Message to be sent
     */
    private void sendBadResponse(String message) {

        // payload not supplied
        JSONObject badResp = new JSONObject();
        badResp.put("status", "bad");
        badResp.accumulate("payload", new HashMap<>(){{
            put("errorMsg", message);
        }});

        // send response
        sendJSONString(badResp.toString());
    }


    /**
     * Handles each client
     */
    public void run(){

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

            } catch (NullPointerException e){

                sendBadResponse("actionNotFound");
                continue;
            }

            // Get the action object
            if(action.equals("create")){

                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload");
                    continue;
                }

                // Check if this room already exists
                if(this.roomHashMap.getRoomHashMap().containsKey(roomId)) {

                    sendBadResponse("roomAlreadyExists");
                    continue;
                } else {

                    // Create the room
                    Room room = new Room(jo.getJSONObject("payload").getString("roomId"));

                    // This player is the host
                    room.setHost(this.player);
                    room.getPlayers().add(this.player);

                    // Add the room to the hash map
                    this.roomHashMap.getRoomHashMap().put(room.getId(), room);

                    // Assign the room to the player
                    this.player.setRoom(room);
                }

            } else if(action.equals("join")){

                // Join room
                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload");
                    continue;
                }

                // Check if the room exists
                if(!this.roomHashMap.getRoomHashMap().containsKey(roomId)){

                    sendBadResponse("roomDoesNotExist");
                    continue;
                }

                // Get the room
                Room room = this.roomHashMap.getRoomHashMap().get(roomId);
                try {

                    // Check to see if the player is already in the room
                    if(room.getPlayers().contains(this.player)) {

                        sendBadResponse("playerAlreadyInRequestedRoom");
                        continue;
                    } else {

                        room.addPlayer(this.player);
                    }

                } catch (RoomFullException e) {

                    sendBadResponse("requestedRoomFull");
                    continue;
                }


            } else if(action.equals("start")){

                // Start game
                try {
                    this.player.getRoom().startGame(this.player);
                } catch (NotEnoughPlayersException e) {

                    sendBadResponse("notEnoughPlayers");
                    continue;
                } catch (GameAlreadyStartedException e) {

                    sendBadResponse("gameStartedAlready");
                    continue;
                } catch (InsufficientPrivilegesException e) {

                    sendBadResponse("insufficientPrivileges");
                    continue;
                }

            } else if(action.equals("play")) {

                int cardIndex;
                try {

                    // Get the index of the card
                    cardIndex = jo.getJSONObject("payload").getInt("cardIndex");
                } catch (JSONException e) {

                    // Card index not supplied or is not an integer
                    sendBadResponse("invalidCardIndex");
                    continue;
                }

                // Check if the game has been started
                if(!player.getRoom().isGameStarted()) {

                    sendBadResponse("gameNotStarted");
                    continue;
                }

                // Make sure card index is within the correct range
                if(cardIndex < 0 || cardIndex >= player.getHand().size()) {

                    sendBadResponse("invalidCardIndex");
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
                                sendBadResponse("illegalColorOption");
                                continue;
                            }
                        } catch (IllegalArgumentException e) {

                            // Color offered was not in the enum
                            sendBadResponse("illegalColorOption");
                            continue;
                        }

                        try {

                            // try to play the card
                            this.player.getRoom().playCard(this.player, cardIndex, newColor);
                        } catch (IllegalCardException e) {

                            // Card that was played could not be played for any
                            // number of reasons
                            sendBadResponse("illegalCardPlayed");
                            continue;
                        } catch (IllegalPlayException e) {

                            // not this player's turn to play
                            sendBadResponse("illegalPlayException");
                            continue;
                        }

                    } catch (JSONException e) {

                        sendBadResponse("wildcardColorMissing");
                        continue;
                    }

                } else {

                    try {

                        // try to play the card
                        this.player.getRoom().playCard(this.player, cardIndex, null);
                    } catch (IllegalCardException e) {

                        // Card that was played could not be played for any
                        // number of reasons
                        sendBadResponse("illegalCardPlayed");
                        continue;
                    } catch (IllegalPlayException e) {

                        // not this player's turn to play
                        sendBadResponse("illegalPlayException");
                        continue;
                    }
                }


            } else {

                sendBadResponse("unknownRequest");
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
                        sendJSONString(j.toString());
                    }

                    // Remove the room from the list of rooms
                    this.roomHashMap.getRoomHashMap().remove(this.player.getRoom().getId());
                    break;
                }
            }
        }

        // Close the connection for this player
        try {
            this.socket.close();
        } catch (IOException e) {

            // Shouldn't happen
        }
    }
}

