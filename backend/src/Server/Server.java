package Server;

import GameParts.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    ArrayList<Room> currentRooms;

    Server(){
        currentRooms = new ArrayList<Room>();
    }

    public void handleConnection(Player player){

        DataInputStream inStream;
        DataOutputStream outStream;

        /*try {
            inStream = new DataInputStream(player.getInputStream());
            outStream = new DataOutputStream(player.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }*/

        while(true){

            int id = 3;
            if(id == 1){

                // Create room
                String name = "Name";
                Room room = new Room(name);
                room.setHost(player);
                this.currentRooms.add(room);
                player.setRoom(room);

            } else if(id == 2){

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

            } else if(id == 3){

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

            } else if(id == 4) {

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

