package GameParts;

import GameParts.Card;

import java.net.Socket;
import java.util.ArrayList;

public class Player extends Socket {

    private ArrayList<Card> hand;

    Player() {


    }

    /**
     * Hand getter
     * @return This player's hand
     */
    public ArrayList<Card> getHand() { return hand; }
}
