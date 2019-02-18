package Server;

import GameParts.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080, 5, InetAddress.getByName("0.0.0.0"));
        Server server = new Server();

        while(true){
            try {

                Socket socket = serverSocket.accept();

                try {
                    BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String name = bf.readLine();
                    System.out.println(name);


                    BufferedWriter bfo = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bfo.write("Your name is: " + name);
                    bfo.flush();


                    Player p = new Player(name);
                    server.handleConnection(p, socket);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
