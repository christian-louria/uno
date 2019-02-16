package Server;

import GameParts.Player;

import java.io.IOException;
import java.net.ServerSocket;

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

            //Player player = serverSock.accept();
            gameServer.handleConnection(new Player("Grant"));
        }
    }
}
