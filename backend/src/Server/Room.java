package Server;

import java.net.Socket;
import java.util.ArrayList;

public class Room {

    private ArrayList<Socket> players;
    private Integer maximumPlayers = 6;
    private String id;

    Room() {

        this.players = new ArrayList<>();
    }

    public void addPlayer(Socket socket) throws RoomFullException{

        // Make sure this room has enough players
        if(this.players.size() < maximumPlayers - 1){
            this.players.add(socket);
        } else {

            throw new RoomFullException();
        }
    }
}
