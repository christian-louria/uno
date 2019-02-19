package Server;

import GameParts.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {

        // Create the server socket
        ServerSocket serverSocket = new ServerSocket(8080, 5, InetAddress.getByName("0.0.0.0"));
        RoomHashMap roomHashMap = new RoomHashMap();

        while(true){
            try {

                // Connect new user
                Socket socket = serverSocket.accept();

                try {

                    // Create this user's handler and send them on their way
                    ClientHandler handler = new ClientHandler(socket, roomHashMap);
                    handler.run();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
