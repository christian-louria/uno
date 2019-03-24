package GameParts;

/**
 * Represents the pile of cards that have been played
 */
public class Pile {

    private Card top;
    private Color topColor;

    /**
     * Top card getter
     * @return Card on top of the deck
     */
    public Card topCard() { return this.top; }

    /**
     * Represents the color of the top card in cases of Wildcards
     * @param color
     */
    public void setTopColor(Color color) { this.topColor = color; }

    /**
     * This function will be called if the top card is a wild card
     * @return The color of the top card
     */
    public Color getTopColor() { return this.topColor; }

    /**
     * Top card setter
     * @param card New card on top of the deck
     */
    public void setTop(Card card) { this.top = card; }
}
