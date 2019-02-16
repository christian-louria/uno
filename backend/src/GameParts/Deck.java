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

        // TODO remove seed from actual game
        this.r = new Random(10);
    }

    /**
     * Draws a random card from the deck
     * @return drawn card
     */
    public Card draw() {

        return new Card(this.r);
    }
}
