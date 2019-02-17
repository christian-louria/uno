package GameParts;

import Server.Room;

import java.net.Socket;
import java.util.ArrayList;

public class Player extends Socket {
//public class Player {

    private ArrayList<Card> hand;
    private String name;
    private Room room;
    private boolean calledUno;

    /**
     * Player constructor
     * @param name Name of this player
     */
    public Player( String name ) {

        hand = new ArrayList<Card>();
        this.name = name;
    }

    /**
     * CalledUno getter
     * @return Whether or not this player called uno
     */
    public boolean didCallUno(){
        return calledUno;
    }


    /**
     * CalledUno setter
     * @param calledUno whether or not this player called uno
     */
    public void setCalledUno(boolean calledUno) {
        this.calledUno = calledUno;
    }

    /**
     * Room setter for this player
     * @param room Room the player is joining
     */
    public void setRoom( Room room ){
        this.room = room;
    }


    /**
     * Room getter
     * @return Room this player is in
     */
    public Room getRoom(){ return this.room; }

    /**
     * Hand getter
     * @return This player's hand
     */
    public ArrayList<Card> getHand() { return hand; }


    /**
     * Hand setter
     * @param hand Hand of cards to give to this player
     */
    public void setHand(ArrayList<Card> hand) { this.hand = hand; }


    /**
     * Player toString
     * @return String representing the Player object
     */
    @Override
    public String toString() {

        String ret = this.name + ": ";

        for(Card c : this.hand) {

            ret += c.toString();
            ret += "  ";
        }

        return ret;
    }
}
