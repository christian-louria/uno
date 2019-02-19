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
     * Adds a room to the map
     * @param room Room to add to the list
     */
    public synchronized void addRoom(Room room) {

        this.roomHashMap.put(room.getId(), room);
    }


    /**
     * Removes a room from the map
     * @param roomId Room to remove
     */
    public synchronized void removeRoom(String roomId) {

        this.roomHashMap.remove(roomId);
    }


    /**
     * Checks if a room already exists
     * @param roomId id of the room to check
     * @return true if the room exists, false otherwise
     */
    public synchronized boolean containsRoom(String roomId) {

        return this.roomHashMap.containsKey(roomId);
    }


    /**
     * Gets a specified room from the map
     * @param roomId id of the room to get
     * @return the room
     */
    public synchronized Room getRoom(String roomId) {

        return this.roomHashMap.get(roomId);
    }
}
