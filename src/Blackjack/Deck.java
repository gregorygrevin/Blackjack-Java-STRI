package Blackjack;
import java.util.Random;

public class Deck{

	/**
	 * the deck of cards
	 */
	private Card[] deck;
	/**
	 * counter to know where we are in the deck
	 */
	private int counter; 
	/**
	 * number of cards in the deck	 
	 */
	private static final int NUMBER_OF_CARDS = 52;
	/**
	 * random generator to be able to shuffle the cards in the deck
	 */
	private static final Random rand = new Random(); 

	/**
	 * This method creates a Deck object 
	 */
	public Deck(){
		String[] rank = {"Ace","2","3","4","5","6","7","8","9","10","Jack","Queen","King"};
		String [] suit = {"clubs", "diamonds", "hearts", "spades"};
		deck = new Card[NUMBER_OF_CARDS];
		counter = 0;
		
		// loop across the deck array and create the 13 cards of the 4 suits
		int cardNumber=0;
		for(int j=0; j<4;j++ ) {
			for(int i=0; i<13; i++){
				deck[cardNumber] = new Card(rank[i],suit[j]);
				cardNumber++;
			}
		}
	}
	
	/**
	 * This methods shuffles the deck
	 */
	public void shuffle(){
		for(int i=0; i<deck.length; i++){
			int random = rand.nextInt(NUMBER_OF_CARDS);
			Card t = deck[i];
			deck[i] = deck[random];
			deck[random]=t;
		}
	}
 
	/**
	 * Gives card from the deck 
	 * @return Card
	 */
	public Card dealCard(){
		if(counter<deck.length){
			Card c = deck[counter++];
			return c;
		} else {
			return null;
		}
	}
}