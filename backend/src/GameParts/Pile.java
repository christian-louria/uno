package GameParts;

import java.util.ArrayList;

public class Pile {

    ArrayList<Card> pile;

    Pile() {
        this.pile = new ArrayList<>();
    }

    public Card topCard() {

        return this.pile.get(0);
    }
}
