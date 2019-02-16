package GameParts;

/**
 * Represents the pile of cards that have been played
 */
public class Pile {

    private Card top;

    /**
     * Top card getter
     * @return Card on top of the deck
     */
    public Card topCard() { return this.top; }

    /**
     * Top card setter
     * @param card New card on top of the deck
     */
    public void setTop(Card card) { this.top = card; }
}
