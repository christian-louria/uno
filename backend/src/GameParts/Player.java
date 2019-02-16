package GameParts;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Player extends Socket {

    private ArrayList<Card> hand;
    private String name;

    /**
     * Player constructor
     * @param numCards Number of cards to deal to player
     * @param name Name of this player
     */
    Player( int numCards, String name ) {

        Random r = new Random();
        hand = new ArrayList<>();
        this.name = name;

        while(numCards-- > 0){
            hand.add(new Card(r));
        }
    }

    /**
     * Hand getter
     * @return This player's hand
     */
    public ArrayList<Card> getHand() { return hand; }


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
