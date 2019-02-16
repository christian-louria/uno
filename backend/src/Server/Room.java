package Server;

import GameParts.*;

import java.util.ArrayList;

public class Room {

    private ArrayList<Player> players;
    private Integer maximumPlayers = 4;
    private String id;
    private Deck deck;
    private Pile pile;
    private boolean gameStarted;
    private Player host;

    Room(String id) {
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.deck = new Deck();
        this.pile = new Pile();
        this.id = id;
    }

    /**
     * Adds a new player to the room
     * @param player Player to add to the room
     * @throws RoomFullException Room is already at capacity
     */
    public void addPlayer(Player player) throws RoomFullException {

        // Make sure this room has enough players
        if(this.players.size() < maximumPlayers){
            this.players.add(player);
            player.setRoom(this);
        } else {

            throw new RoomFullException();
        }
    }

    /**
     * Starts the game
     * @param callee Player calling startGame
     * @throws NotEnoughPlayersException Only one person in the room
     * @throws GameAlreadyStartedException Game is already being played
     * @throws InsufficientPrivilegesException The callee is not the host
     */
    public void startGame(Player callee) throws NotEnoughPlayersException,
                                    GameAlreadyStartedException,
                                    InsufficientPrivilegesException {

        // If game is already started cannot call this function
        // again
        if(this.gameStarted){
            throw new GameAlreadyStartedException();
        }

        // If not the host you cannot call this function
        if(callee != this.host){
            throw new InsufficientPrivilegesException();
        }

        // If there are not 2 or more players you cannot call this function
        if(this.players.size() < 2){
            throw new NotEnoughPlayersException();
        }

        for(Player p : this.players){

            ArrayList<Card> hand = new ArrayList<Card>();
            while(hand.size() < 7)
                hand.add(deck.draw());

            p.setHand(hand);
        }

        this.gameStarted = true;

        // Set the top of the pile to the top of the deck
        this.pile.setTop(deck.draw());
    }


    /**
     * Tests to see if the game is over
     * @return True if the game is over, false otherwise
     */
    public boolean isGameOver(){

        // Check if any player has won the game
        for(Player p : this.players){
            if(p.getHand().size() == 0){
                return true;
            }
        }

        return false;
    }


    /**
     * Host setter
     * @param player Player that will be the host
     */
    public void setHost(Player player){
        this.host = player;
    }


    /**
     * Id Getter
     * @return id of this room
     */
    public String getId() {
        return id;
    }

    /**
     * Plays a card on the board
     */
    public void playCard(Player callee, int cardLocation, Color wildcardColor)
            throws IllegalCardException {

        // Get the card from the player's hand
        Card card = callee.getHand().get(cardLocation);
        Card onPile = this.pile.topCard();

        // if the card is a wildcard just put it on
        if(card.getColor() == Color.WILDCARD){

            this.pile.setTop(card);
            this.pile.setTopColor(wildcardColor);
            callee.getHand().remove(cardLocation);
        }

        // If Wild card is on top then get the new color from the pile
        if(onPile.getType() == CardType.WILDCARD){

            Color onPileColor = this.pile.getTopColor();

            // If card being put down matches color then it's all good
            if(card.getColor() == onPileColor){

                this.pile.setTop(card);
                this.pile.setTopColor(card.getColor());
                callee.getHand().remove(cardLocation);
            } else {

                // Color doesn't match and you cannot match type with a wildcard
                throw new IllegalCardException();
            }
        }

        // Handle normal cases
        if(card.getColor() == onPile.getColor() || card.getType() == onPile.getType()){

            this.pile.setTop(card);
            this.pile.setTopColor(card.getColor());
            callee.getHand().remove(cardLocation);
        } else {

            // Only this case means a card is bad
            throw new IllegalCardException();
        }
    }


    /**
     * Players getter
     * @return List of players in the room
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }


    public static void main(String[] args) {

        Player grant = new Player("Grant");
        Player alex = new Player("Alex");
        Player tian = new Player("Tian");
        Player tian2 = new Player("Tian2");
        Player tian3 = new Player("Tian3");

        Room r = new Room("Name");

        try {
            r.addPlayer(grant);
            r.addPlayer(alex);
            r.addPlayer(tian);
        } catch (RoomFullException e) {

            e.printStackTrace();
        }

        grant.getRoom().setHost(grant);

        try {
            grant.getRoom().startGame(grant);
        } catch (NotEnoughPlayersException e) {

            // alert that there are not enough
            e.printStackTrace();
        } catch (GameAlreadyStartedException e) {

            // alert that the game has already began
            e.printStackTrace();
        } catch (InsufficientPrivilegesException e) {

            // alert this player is not the host
            e.printStackTrace();
        }


        try {
            // Grant plays draw two blue on to reverse blue
            grant.getRoom().playCard(grant, 0, null);

            // tian plays 5 blue on draw two blue
            grant.getRoom().playCard(tian, 1, null);

            // grant plays 5 red on 5 blue
            grant.getRoom().playCard(grant, 5, null);

            // Alex plays a wildcard with the replacement as blue
            grant.getRoom().playCard(alex, 5, Color.BLUE);

        } catch (IllegalCardException e) {
            e.printStackTrace();
        }


        boolean gameOver = grant.getRoom().isGameOver();
        grant.setHand(new ArrayList<>());
        gameOver = grant.getRoom().isGameOver();

        return;
    }
}
