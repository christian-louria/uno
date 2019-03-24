package GameParts;

import java.util.Random;


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

        int colorPick = r.nextInt(113);

        // Generate the color
        if(colorPick < 26) {
            this.color = Color.RED;
        } else if(colorPick < 52) {
            this.color = Color.YELLOW;
        } else if(colorPick < 78) {
            this.color = Color.GREEN;
        } else if(colorPick < 104) {
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

        String name;

        if(this.type == CardType.WILDCARD)
            return "wild.svg";
        else if(this.type == CardType.ADDFOUR)
            return "draw-4.svg";
        else if(this.type == CardType.ADDTWO) {
            name = "draw-2-";
            name += this.color + ".svg";
            name = name.toLowerCase();
            return name;
        } else if(this.type == CardType.SKIP || this.type == CardType.REVERSE) {

            name = this.type + "-";
            name = name.toLowerCase();
            name += this.color + ".svg";
            name = name.toLowerCase();
            return name;
        }

        // Deal with normal cases
        name = this.type.ordinal() + "-";
        name = name.toLowerCase();
        name += this.color + ".svg";
        name = name.toLowerCase();

        return name;
    }

    public static void main(String[] args) {

        Random r = new Random();
        for(int i = 0; i < 12; i++){
            System.out.println(new Card(r).toString());
        }
    }
}
