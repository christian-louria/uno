package GameParts;

import java.util.Random;

/**
 * Represents the Deck for the game
 */
public class Deck {

    Random r;

    /**
     * Deck constructor
     */
    public Deck(){

        this.r = new Random();
    }

    /**
     * Draws a random card from the deck
     * @return drawn card
     */
    public Card draw() {

        return new Card(this.r);
    }
}
