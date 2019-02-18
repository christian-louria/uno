package Server;

import GameParts.Player;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ArrayList<Room> currentRooms;

    /**
     * Server constructor
     */
    Server(){ currentRooms = new ArrayList<Room>(); }


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
                    sendJSONString("{\"status\":\"bad\"}", socket);
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
     * Handles each client
     * @param player The client's player object
     * @param socket The socket that the server and client will
     *               communicate over
     */
    public void handleConnection(Player player, Socket socket){


        while(true){

            JSONObject jo = getJSON(socket);
            String action = jo.getString("action");

            if(action.equals("create")){

                // Create room
                JSONObject payload = new JSONObject(jo.get("payload"));
                Room room = new Room(payload.getString("roomId"));
                room.setHost(player);
                this.currentRooms.add(room);
                player.setRoom(room);

            } else if(action.equals("join")){

                // Join room
                String roomName = "Name";
                for(Room room : this.currentRooms){
                    if(room.getId().equals(roomName)){

                        try {
                            room.addPlayer(player);
                        } catch (RoomFullException e) {

                            // alert player that the room was full
                        }

                    }
                }

            } else if(action == "3"){

                // Start game
                try {
                    player.getRoom().startGame(player);
                } catch (NotEnoughPlayersException e) {

                    // alert that there are not enough
                } catch (GameAlreadyStartedException e) {

                    // alert that the game has already began
                } catch (InsufficientPrivilegesException e) {

                    // alert this player is not the host
                }

            } else if(action == "4") {

                // Play card
                int card = 0;
                //player.getRoom().playCard(player.getHand().get(card));
            } else {

                // Check if game is over
                if(player.getRoom().isGameOver()){

                    for(Player p : player.getRoom().getPlayers()){

                        // Alert other players the game is over
                        continue;
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

