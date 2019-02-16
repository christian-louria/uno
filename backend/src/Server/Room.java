package Server;

import GameParts.*;

import java.util.ArrayList;

public class Room extends Thread {

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

    public void addPlayer(Player player) throws RoomFullException {

        // Make sure this room has enough players
        if(this.players.size() < maximumPlayers - 1){
            this.players.add(player);
        } else {

            throw new RoomFullException();
        }
    }

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
     * Plays a card on the board
     */
    public void playCard(Card card){

        return;
    }


    /**
     * Players getter
     * @return List of players in the room
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }
}
