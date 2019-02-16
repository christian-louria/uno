package GameParts;

import java.util.Random;

public class Deck {

    Random r;
    Deck(){

        this.r = new Random();
    }

    public Card draw() {

        return new Card(this.r);
    }
}
