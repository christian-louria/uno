package Server;

import GameParts.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    Server(){}

    public void handleConnection(Player player){

        DataInputStream inStream;
        DataOutputStream outStream;

        try {
            inStream = new DataInputStream(player.getInputStream());
            outStream = new DataOutputStream(player.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(true){

            // Create room
            String name = "Name";
            Room room = new Room(name);
            room.setHost(player);
            player.setRoom(room);

            // Join room

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


            // Play card
            int card = 0;
            player.getRoom().playCard(player.getHand().get(card));


            // Check if game is over
            if(player.getRoom().isGameOver()){

                for(Player p : player.getRoom().getPlayers()){

                    // Alert other players the game is over
                    continue;
                }
            }
        }
    }
}

public class Main {

    public static void main(String[] args) {

        if(args.length < 1){
            System.err.println("Usage: java Server.Server <port>");
            System.exit(1);
        }

        ServerSocket serverSock;
        Server gameServer = new Server();

        try {
            serverSock = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {

            System.err.println(String.format("Port %s in use or privileged port.", args[0]));
            System.err.println("Try running as root or using a port above 1023");
            System.exit(1);
        } catch (NumberFormatException e) {

            System.err.println("Port supplied must be an integer");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Port number must be between 1 and 65535");
            System.exit(1);
        }

        //System.out.println(String.format("Socket starting on port %d", serverSock.getLocalPort()));

        while(true){

            Player player = serverSock.accept();
            gameServer.handleConnection(player);
        }
    }
}
