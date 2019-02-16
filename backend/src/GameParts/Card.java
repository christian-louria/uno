package GameParts;


import java.util.Random;

/**
 * Represents the different types of cards possible in the game
 */
enum CardType {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
    REVERSE, SKIP, ADDTWO, ADDFOUR, WILDCARD
}

/**
 * Represents the different colors for cards in the game
 */
enum Color {
    BLUE, RED, GREEN, YELLOW, WILDCARD
}

/**
 * Represents the Cards used in the game
 */
public class Card {

    private CardType type;
    private Color color;

    /**
     * Card constructor
     * @param r Random object to generate random cards
     */
    Card(Random r) {

        int colorPick = r.nextInt(5);

        // Generate the color
        if(colorPick == 0) {
            this.color = Color.RED;
        } else if(colorPick == 1) {
            this.color = Color.YELLOW;
        } else if(colorPick == 2) {
            this.color = Color.GREEN;
        } else if(colorPick == 3) {
            this.color = Color.BLUE;
        } else {
            this.color = Color.WILDCARD;
        }

        if(this.color == Color.WILDCARD){

            // Wildcards only have two choices
            int cardType = r.nextInt(2);

            if(cardType == 0){
                this.type = CardType.WILDCARD;
            } else {
                this.type = CardType.ADDFOUR;
            }

        } else {

            // Randomly choose card type
            int cardtype = r.nextInt(13);
            if(cardtype == 0) {
                this.type = CardType.ZERO;
            } else if(cardtype == 1) {
                this.type = CardType.ONE;
            } else if(cardtype == 2) {
                this.type = CardType.TWO;
            } else if(cardtype == 3) {
                this.type = CardType.THREE;
            } else if(cardtype == 4) {
                this.type = CardType.FOUR;
            } else if(cardtype == 5) {
                this.type = CardType.FIVE;
            } else if(cardtype == 6) {
                this.type = CardType.SIX;
            } else if(cardtype == 7) {
                this.type = CardType.SEVEN;
            } else if(cardtype == 8) {
                this.type = CardType.EIGHT;
            } else if(cardtype == 9) {
                this.type = CardType.NINE;
            } else if(cardtype == 10) {
                this.type = CardType.REVERSE;
            } else if(cardtype == 11) {
                this.type = CardType.SKIP;
            } else {
                this.type = CardType.ADDTWO;
            }
        }
    }


    /**
     * Type getter
     * @return Type of card this is
     */
    public CardType getType(){ return this.type; }

    /**
     * Color getter
     * @return color of this card
     */
    public Color getColor(){ return this.color; }


    /**
     * To String method
     * @return String representation of this card
     */
    @Override
    public String toString() {
        return String.format("%s:%s", this.color, this.type);
    }
}
