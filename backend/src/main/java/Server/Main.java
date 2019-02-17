package Server;

import GameParts.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.peade.websocket.net.WebSocketServerSocket;

public class Main {

    public static void main(String[] args) {

        ServerSocket sock;
        Server server = new Server();

        try {
            sock = new ServerSocket(8080, 5, InetAddress.getByName("0.0.0.0"));

            WebSocketServerSocket webSocketServerSocket = new WebSocketServerSocket();

            while(true){
                try {
                    Socket client = sock.accept();
                    System.out.println("A client connected");



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e){

            e.printStackTrace();
            return;
        }
    }
}
