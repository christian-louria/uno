package Server;

import GameParts.*;
import java.util.ArrayList;

public class Room {

    private enum Direction{LEFT, RIGHT}

    private Direction currentPlayDirection;
    private ArrayList<Player> players;
    private Integer maximumPlayers = 4;
    private String id;
    private Deck deck;
    private Pile pile;
    private boolean gameStarted;
    private Player host;
    private Integer currentPlayer;

    Room(String id) {
        this.players = new ArrayList<Player>();
        this.gameStarted = false;
        this.deck = new Deck();
        this.pile = new Pile();
        this.id = id;
        this.currentPlayDirection = Direction.LEFT;
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
        this.currentPlayer = 0;

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
     * Points to the next player
     */
    private void nextPlayer() {

        // Deal reverse direction of play
        if(this.currentPlayDirection == Direction.LEFT){

            this.currentPlayer++;
            if(this.currentPlayer.equals(this.players.size()))
                this.currentPlayer = 0;
        } else {

            this.currentPlayer--;
            if(this.currentPlayer == 0)
                this.currentPlayer = this.players.size() - 1;
        }
    }

    /**
     * Plays a card on the board
     */
    public void playCard(Player callee, int cardLocation, Color wildcardColor)
            throws IllegalCardException, IllegalPlayException {

        // If it is not this players turn they cannot play the card
        if(!this.players.get(currentPlayer).equals(callee)){
            throw new IllegalPlayException();
        }

        // Get the card from the player's hand
        Card card = callee.getHand().get(cardLocation);
        Card onPile = this.pile.topCard();

        // if the card is a wildcard just put it on
        if(card.getColor() == Color.WILDCARD){

            this.pile.setTop(card);
            this.pile.setTopColor(wildcardColor);
            callee.getHand().remove(cardLocation);
            handleCard();
            return;
        }

        // If Wild card is on top then get the new color from the pile
        if(onPile.getType() == CardType.WILDCARD){

            Color onPileColor = this.pile.getTopColor();

            // If card being put down matches color then it's all good
            if(card.getColor() == onPileColor){

                this.pile.setTop(card);
                this.pile.setTopColor(card.getColor());
                callee.getHand().remove(cardLocation);
                handleCard();
                return;
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
            handleCard();
        } else {

            // Only this case means a card is bad
            throw new IllegalCardException();
        }
    }


    /**
     * Deals with the card as needed
     */
    private void handleCard(){

        if(this.pile.topCard().getType() == CardType.SKIP){

            // If skip is played then call next player twice
            this.nextPlayer();
            this.nextPlayer();
        } else if(this.pile.topCard().getType() == CardType.REVERSE){

            // Reverse Direction
            if(this.currentPlayDirection == Direction.LEFT)
                this.currentPlayDirection = Direction.RIGHT;
            else
                this.currentPlayDirection = Direction.LEFT;

            this.nextPlayer();
        } else if(this.pile.topCard().getType() == CardType.ADDTWO){

            // Make the player draw two then move on
            this.nextPlayer();
            this.players.get(currentPlayer).setCalledUno(false);
            this.players.get(currentPlayer).getHand().add(this.deck.draw());
            this.players.get(currentPlayer).getHand().add(this.deck.draw());
            this.nextPlayer();
        } else if(this.pile.topCard().getType() == CardType.ADDFOUR){

            this.nextPlayer();
            this.players.get(currentPlayer).setCalledUno(false);
            for(int i = 0; i < 4; i++)
                this.players.get(currentPlayer).getHand().add(this.deck.draw());
            this.nextPlayer();
        } else {

            this.nextPlayer();
        }
    }


    /**
     * Simulates a player calling uno during the game
     * @param callee Person calling uno
     */
    public void callUno(Player callee) throws IllegalUnoCallException {

        boolean unoCorrectlyCalled = false;

        // Make this person safe from having uno called upon them
        if(callee.getHand().size() == 1){
            callee.setCalledUno(true);
            unoCorrectlyCalled = true;
        }

        // If it is this player's turn and they have 2 they can call uno before
        // playing the card
        if(this.players.get(this.currentPlayer).equals(callee) &&
                callee.getHand().size() == 2){
            callee.setCalledUno(true);
            unoCorrectlyCalled = true;
        }

        // check to see if this player got another person not
        // calling uno. Must do both in case player had 1 card in
        // their hand from earlier but is calling uno against
        // a different player
        for(Player p : callee.getRoom().players){
            if(p.getHand().size() == 1 && !p.didCallUno()){
                p.setCalledUno(false);
                p.getHand().add(this.deck.draw());
                unoCorrectlyCalled = true;
            }
        }

        if(!unoCorrectlyCalled){

            // if UNO was not called correctly throw an exception so player must
            // wait to call it again
            throw new IllegalUnoCallException();
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


        /*try {
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
        } catch (IllegalPlayException e) {
            e.printStackTrace();
        }*/

        grant.setHand(new ArrayList<Card>());
        grant.getHand().add(grant.getRoom().deck.draw());
        //grant.getHand().add(grant.getRoom().deck.draw());

        try {
            grant.getRoom().callUno(grant);
        } catch (IllegalUnoCallException e) {
            e.printStackTrace();
        }


        boolean gameOver = grant.getRoom().isGameOver();
        gameOver = grant.getRoom().isGameOver();

        return;
    }
}
