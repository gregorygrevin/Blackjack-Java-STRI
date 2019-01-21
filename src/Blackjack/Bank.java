package Blackjack;
public class Bank {
	/**
	 * Total amount of tokens owned by the Bank
	 */
     static int tokens = 1000000;    
 	/**
 	 * This method is given an int that is the amount of token to be added to the total amount of tokens owned by the bank
 	 * @param value - value to be added
 	 */
     public static void addTokens(int value) {
    	 	tokens += value;
     }
  	/**
  	 * This method is given an int that is the amount of token to be removed from the total amount of tokens owned by the bank
  	 * @param value - value to be substracted
  	 */
     public static void substractTokens(int value) {
 	 	tokens -= value;
  }
}