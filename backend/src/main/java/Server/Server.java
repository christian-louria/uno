package Server;

import GameParts.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private HashMap<String, Room> currentRooms;

    /**
     * Server constructor
     */
    Server(){ currentRooms = new HashMap<String, Room>(); }


    /**
     * Gets JSON from the client which will tell server what to do
     * @param socket Socket to communicate over
     * @return JSON string
     */
    private JSONObject getJSON(Socket socket) {

        try {
            JSONObject jo = null;
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Until the client sends good json keep asking after alerting of bad JSON
            while(jo == null) {

                try {

                    // read the line and try to parse the json
                    String jsonString = bf.readLine();
                    jo = new JSONObject(jsonString);

                } catch (JSONException e) {

                    // JSON error occurred, tell client it was a bad request.
                    sendBadResponse("jsonParsingError", socket);
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
     * @param json JSON string to send
     * @param socket Socket for the client
     */
    private void sendJSONString(String json, Socket socket) {

        try {
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
    private void sendBadResponse(String message, Socket socket) {

        // payload not supplied
        JSONObject badResp = new JSONObject();
        badResp.put("status", "bad");
        badResp.accumulate("payload", new HashMap<>(){{
            put("errorMsg", message);
        }});

        // send response
        sendJSONString(badResp.toString(), socket);
    }


    /**
     * Handles each client
     * @param player The client's player object
     * @param socket The socket that the server and client will
     *               communicate over
     */
    public void handleConnection(Player player, Socket socket){

        while(true){

            // Get JSON from the client
            JSONObject jo;
            try {
                jo = getJSON(socket);
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

                sendBadResponse("actionNotFound", socket);
                continue;
            }

            // Get the action object
            if(action.equals("create")){

                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload", socket);
                    continue;
                }

                // Check if this room already exists
                if(this.currentRooms.containsKey(roomId)) {

                    sendBadResponse("roomAlreadyExists", socket);
                    continue;
                } else {

                    // Create the room
                    Room room = new Room(jo.getJSONObject("payload").getString("roomId"));

                    // This player is the host
                    room.setHost(player);
                    room.getPlayers().add(player);

                    // Add the room to the hash map
                    this.currentRooms.put(room.getId(), room);

                    // Assign the room to the player
                    player.setRoom(room);
                }

            } else if(action.equals("join")){

                // Join room
                // Try to parse JSON for the roomId
                String roomId;
                try {
                    roomId = jo.getJSONObject("payload").getString("roomId");
                } catch (JSONException e) {

                    sendBadResponse("badPayload", socket);
                    continue;
                }

                // Check if the room exists
                if(!this.currentRooms.containsKey(roomId)){

                    sendBadResponse("roomDoesNotExist", socket);
                    continue;
                }

                // Get the room
                Room room = this.currentRooms.get(roomId);
                try {

                    // Check to see if the player is already in the room
                    if(room.getPlayers().contains(player)) {

                        sendBadResponse("playerAlreadyInRequestedRoom", socket);
                        continue;
                    } else {

                        room.addPlayer(player);
                    }

                } catch (RoomFullException e) {

                    sendBadResponse("requestedRoomFull", socket);
                    continue;
                }


            } else if(action.equals("start")){

                // Start game
                try {
                    player.getRoom().startGame(player);
                } catch (NotEnoughPlayersException e) {

                    sendBadResponse("notEnoughPlayers", socket);
                    continue;
                } catch (GameAlreadyStartedException e) {

                    sendBadResponse("gameStartedAlready", socket);
                    continue;
                } catch (InsufficientPrivilegesException e) {

                    sendBadResponse("insufficientPrivileges", socket);
                    continue;
                }

            } else if(action.equals("play")) {

                int cardIndex;
                try {

                    // Get the index of the card
                    cardIndex = jo.getJSONObject("payload").getInt("cardIndex");
                } catch (JSONException e) {

                    // Card index not supplied or is not an integer
                    sendBadResponse("invalidCardIndex", socket);
                    continue;
                }

                // Check if the game has been started
                if(!player.getRoom().isGameStarted()) {

                    sendBadResponse("gameNotStarted", socket);
                    continue;
                }

                // Make sure card index is within the correct range
                if(cardIndex < 0 || cardIndex >= player.getHand().size()) {

                    sendBadResponse("invalidCardIndex", socket);
                    continue;
                }

                // get the card
                Card c = player.getHand().get(cardIndex);

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
                                sendBadResponse("illegalColorOption", socket);
                                continue;
                            }
                        } catch (IllegalArgumentException e) {

                            // Color offered was not in the enum
                            sendBadResponse("illegalColorOption", socket);
                            continue;
                        }

                        try {

                            // try to play the card
                            player.getRoom().playCard(player, cardIndex, newColor);
                        } catch (IllegalCardException e) {

                            // Card that was played could not be played for any
                            // number of reasons
                            sendBadResponse("illegalCardPlayed", socket);
                            continue;
                        } catch (IllegalPlayException e) {

                            // not this player's turn to play
                            sendBadResponse("illegalPlayException", socket);
                            continue;
                        }

                    } catch (JSONException e) {

                        sendBadResponse("wildcardColorMissing", socket);
                        continue;
                    }

                } else {

                    try {

                        // try to play the card
                        player.getRoom().playCard(player, cardIndex, null);
                    } catch (IllegalCardException e) {

                        // Card that was played could not be played for any
                        // number of reasons
                        sendBadResponse("illegalCardPlayed", socket);
                        continue;
                    } catch (IllegalPlayException e) {

                        // not this player's turn to play
                        sendBadResponse("illegalPlayException", socket);
                        continue;
                    }
                }


            } else {

                sendBadResponse("unknownRequest", socket);
                continue;
            }

            // Check if the player has a room before checking if
            // game is over. Otherwise just continue
            if(player.getRoom() != null){

                // If the game is not started yet ignore
                if(!player.getRoom().isGameStarted()){
                    continue;
                }

                // Check if game is over
                if(player.getRoom().isGameOver()){

                    for(Player p : player.getRoom().getPlayers()){

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
                        sendJSONString(j.toString(), socket);
                    }

                    break;
                }
            }
        }

        // Close the connection for this player
        /*try {
            player.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}

