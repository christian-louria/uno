package Server;

import java.util.HashMap;

/**
 * Holds the list of room currently on the server
 */
public class RoomHashMap {

    private HashMap<String, Room> roomHashMap;

    /**
     * RoomHashMap constructor
     */
    RoomHashMap() { this.roomHashMap = new HashMap<>(); }

    /**
     * roomHashMap Getter
     * @return the hash map that holds all the rooms
     */
    public HashMap<String, Room> getRoomHashMap() {
        return roomHashMap;
    }
}
