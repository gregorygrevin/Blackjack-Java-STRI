package Blackjack;
import java.util.ArrayList;
import java.util.UUID;

public class Player	{


	/**
	 * true if Player looses (cardTotal > 21)
	 */
	private boolean bust=false; 
	/**
	 *  Total value of player's cards
	 */
	private int cardTotal=0; 
	/**
	 * Player Cards
	 */
	private ArrayList<Card> cards;
	/**
	 * Number of cards in player's hand 
	 */
	private int numberOfCards = 0;

	boolean dealer;
	private int playerTokens;
	int bet;
	/**
	 * This methods takes 2 Cards as arguments and the amount of tokens as well as the bet and creates a Player
	 * @param tokens - tokens of the player
	 * @param card1 - first card of the player
	 * @param card2 - second card of the player
	 * @param bet - bet of the player
	 */
	public Player(int tokens, Card card1, Card card2, int bet) {
		this.bet = bet;
		playerTokens = tokens;
		dealer = false;
		cards = new ArrayList<Card>();
		cards.add(card1);
		cards.add(card2);
		computeTotal();
		setNumberOfCard();
	}
	/**
	 * This methods takes 2 Cards as arguments and creates a Player
	 * @param card1 - first card of the player
	 * @param card2 - second card of the player
	 */
	public Player( Card card1, Card card2) {
		bet = -1;
		playerTokens = 0;
		dealer =true;
		cards = new ArrayList<Card>();
		cards.add(card1);
		cards.add(card2);
		computeTotal();
		setNumberOfCard();
	}
	/**
	 * This methods takes the amount of tokens and creates a Player
	 * @param tokens - tokens of the player
	 */
	public Player(int tokens) {
		playerTokens = tokens;
	}
	/**
	 * This methods sets the bet of the player 
	 * @param value - value to set the bet value
	 */
	public void setBet(int value) {
		if(!dealer) {
			bet =  value;
		}
	}
	/**
	 * This methods returns the bet of the player 
	 * @return bet
	 */
	public int getBet() {
		return bet;
	}

	/**
	 * This methods returns the tokens of the player 
	 * @return playerTokens
	 */
	public int getTokens() {
		return playerTokens;
	}

	/**
	 * This methods adds the value given in the method variable to the tokens of the player 
	 * @param value - value to add to the tokens
	 */
	public void addTokens(int value) {
		if(!dealer) {
			playerTokens = playerTokens + value;
		}
	}
	/**
	 * This methods removes the value given in the method variable from the tokens of the player 
	 * @param value - value to be substracted from the tokens
	 */
	public void substractTokens (int value) {
		if(!dealer) {
			playerTokens = playerTokens - value;
		}
	}
	/**
	 * This method returns the total value of the player's card
	 * @return Integer
	 */
	public int getCardTotal() {
		return cardTotal;
	}

	/**
	 * This methods resets the card total to 0
	 */
	public void resetCardTotal() {
		cardTotal = 0;
	}

	/**
	 * This methods resets the number of cards  to 0
	 */
	public void resetNumberOfCards() {
		numberOfCards = 0;
	}

	/**
	 * This method returns the number of cards in the player's hand
	 * @return Integer
	 */
	public int getNumberOfCards() {
		return numberOfCards;
	}

	/**
	 * This method takes a card as argument and adds it to the player's cards
	 * @param card - card to be added to the player
	 */
	public void cardHit(Card card){
		cards.add(card);
		computeTotal();
		setNumberOfCard();
		checkBust();
	}
	/**
	 * Compute total value of player's cards and save in CardTotal
	 * @return void
	 */
	private void computeTotal() {
		cardTotal = 0;
		for(Card c : cards){
			if(!(c.getRank() == "Ace"))
				cardTotal += c.getValue();
			else { // if card = Ace 
				//if the player's total is less than or equal to 10  -> Ace value = 11
				if (cardTotal <= 10){
					cardTotal += 11;
				}//if the player's total is more than 10 -> Ace value = 1
				else { 
					cardTotal += 1;
				}
			}
		}	
	}

	/**
	 * This methods sets the value of numberOfCards 
	 * @return void
	 */
	private void setNumberOfCard() {
		numberOfCards = cards.size();
	}

	/**
	 * check if cardTotal bigger than 21 so the player looses
	 * @return Boolean
	 */
	public boolean checkBust(){
		if(cardTotal > 21){
			bust = true;
		}
		else {
			bust = false;
		}
		return bust;
	}

}
