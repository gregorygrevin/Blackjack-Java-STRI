package Blackjack;
import java.io.Serializable;

public class Card implements Serializable {

	/**
	 * Ace 2 3 4 5 6 7 8 9 10 Jack Queen King
	 */
	private String rank;  
	/**
	 * clubs, diamonds, hearts, spades 
	 */
	private String suit; 
	/**
	 * represents the value of the card 1 2 3 4 5 6 7 8 9 10 11
	 */
	private int value;  
	
	/**
	 * This method is given 2 strings, one that represents the rank and another one that represents the suit, and creates a Card object 
	 * @param cardRank - rank of the card
	 * @param cardSuit - suit of the card
	 */
	public Card(String cardRank, String cardSuit){
		rank= cardRank;
		suit = cardSuit;
		switch (rank) {
		case "Ace":
			// to be taken care of later: 1 or 11 depending on total
			value = 0;
			break;
		case "2":
		case "3":
		case "4":
		case "5":
		case "6":
		case "7":
		case "8":
		case "9":		
		case "10":
			value=Integer.parseInt(rank);
			break;
		case "Jack":
		case "Queen":
		case "King":
			value = 10;
			break;
		default:
			value = 0;
			break;
		}
	}
	
	/**
	 * This method returns the rank of the card
	 * @return String
	 */
	public String getRank() {
		return rank;
	}
	
	/**
	 * This methods sets the rank of the card
	 * @param rank - rank to be set to the card
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	/**
	 * This method returns the suit of the card
	 * @return String
	 */
	public String getSuit() {
		return suit;
	}
	
	/**
	 * This methods sets the suit of the card
	 * @param suit - suit to be set to the card
	 */
	public void setSuit(String suit) {
		this.suit = suit;
	}
	
	/**
	 * This method returns the value of the card
	 * @return Integer
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * This methods sets the value of the card
	 * @param value - value to be set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return rank + " " + suit;
	}
}
