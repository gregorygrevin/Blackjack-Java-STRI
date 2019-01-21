package Blackjack;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Table{
	
	/**
	 * represents the dealer affected to the table 
	 */
	Dealer dealer;
	/**
	 * This method is given all the data needed to create a table from scratch
	 * @param gNumber - number of the table
	 * @param clientId - id of the client
	 * @param port - port of the table
	 * @param maxPlayers - number of maximum players allowed
	 * @param temp - if this table is temporary
	 * @param minBet - minimum value of bet allowed
	 */
	public Table(int gNumber, String clientId, int port, int maxPlayers, int temp, int minBet) { //1 is temp
		
		if(temp == 1) {
			dealer = new Dealer(gNumber); // create server
		} else {
			dealer = new Dealer(gNumber); // create server
//	  	application.setDefaultCloseOperation( application.EXIT_ON_CLOSE );
			dealer.showDealer(null); 
			dealer.runDeal(port, maxPlayers,minBet); // run server application
	
	}
	}
	/**
	 * This method is give a dealer to be used to create a table
	 * @param d -  dealer
	 */
	public Table (Dealer d) {
		dealer = d;
	}
}

