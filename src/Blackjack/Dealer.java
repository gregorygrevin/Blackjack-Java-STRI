package Blackjack;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Dealer  {

	/**
	 * Deal Button of Dealer GUI
	 */
	private JButton deal;

	/**
	 * Restart Button of Dealer GUI
	 */
	private JButton restart;

	/**
	 * Deal Join of Dealer GUI
	 */
	private JButton join;
	/**
	 * TextArea of Dealer GUI - displays information to user
	 */
	private JTextArea dealersDisplayArea; 
	/**
	 * Runs players
	 */
	private ExecutorService executor; 
	/**
	 * server socket
	 */
	private ServerSocket server; 
	/**
	 *  Array of server threads
	 */
	private ServerThread[] sockServer; 
	/**
	 * counter of number of connections
	 */
	private int counter = 1; 
	/**
	 * 1st Card object of dealer
	 */
	/**
	 * 2nd card object of dealer
	 */
	private Card dcard1,dcard2;
	/**
	 * ArrayList of players in this round
	 */
	private ArrayList<Player> players;
	/**
	 * ArrayList of players in the previous round
	 */
	private ArrayList<Player> oldPlayers;
	/**
	 * Dealer 
	 */
	private Player dealerCards = null;
	/**
	 * Number of players still in the game
	 */
	private int playersleft;
	/**
	 * deck of this game
	 */
	private Deck newdeck;
	/**
	 * Jpanel of Buttons for GUI
	 */
	private JPanel buttons;
	
	/**
	 * boolean to keep track of the status of the round
	 */
	private boolean roundover = true;
	/**
	 * Keeps track of the number of Games
	 */
	int numberOfGames = 0;
	/**
	 * This methods creates a Dealer + creates the GUI
	 */
	String clientId;
	
	/**
	 * Port assigned for this dealer - Game
	 */
	int port;
	/**
	 * Number assigned for this dealer - Game
	 */
	int gNumber;
	/**
	 * JFrame for Dealer GUI
	 */
	JFrame frame;
	/**
	 * Minimal bet allowed on this table
	 */
	int minBet;
	/**
	 * Number of Clients that left the game or were removed
	 */
	int toRemove =0;
	/**
	 * List of sockets of clients to be removed
	 */
	ArrayList<Integer> socketsToRemove;
	
	/**
	 * This methods creates a dealer and affects the number of the table to it
	 * @param gNumber - number of the dealer/Table
	 */
	public Dealer(int gNumber) {
		//		super( "Dealer Game: " + gNumber );

		players = new ArrayList<Player>(); //arraylist that will hold the different players for this dealer 
		oldPlayers = new ArrayList<Player>();
		socketsToRemove = new ArrayList<Integer>();
		sockServer = new ServerThread[ 100 ]; // allocate array for up to 10 server threads
		executor = Executors.newFixedThreadPool(100); // create thread pool
		numberOfGames ++;
		this.gNumber = gNumber;

		//		showDealer(frame);
	} 
	/**
	 * This methods creates the dealer's GUI
	 * @param clientId - id of the client
	 */
	public void showDealer(String clientId) {
		this.clientId = clientId;
		frame = new JFrame();
		frame.setTitle("Dealer Game: " + gNumber);
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(1,2));

		deal = new JButton("Deal Cards");

		restart = new JButton("Restart");
		restart.setEnabled(false);
		deal.setEnabled(false);
		join = new JButton("Join this temporary Game");
		if(clientId != null) {
			join.addActionListener(
					new ActionListener() 
					{
						// send message to server
						public void actionPerformed( ActionEvent event )
						{

							(new Thread() {
								@Override
								public void run() {
									try {
										Client application; // declare client application
										application = new Client( "127.0.0.1", gNumber , null, "" ); // connect to localhost
										application.runClient(port);
										System.out.println("client one down");
									} catch (Exception e) {
										e.printStackTrace();
									}
								} 
							}).start();

						}
					});

			frame.add(join, BorderLayout.NORTH);
		}
		deal.addActionListener(
				new ActionListener() {
					// send message to client
					public void actionPerformed( ActionEvent event )
					{
						deal.setEnabled(false);
						newdeck = new Deck();
						roundover=false; 
						dealCards();
						displayMessage("\n\nCARDS DEALT\n\n");

						restart.setEnabled(true);
					}
				}); 

		restart.addActionListener(
				new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						deal.setEnabled(true);
						if(dealerCards != null) {
							dealerCards.resetCardTotal();
						}

						displayMessage( "\n------------- \nNEW GAME\n------------- " );
						for (int i=1;i<= counter;i++) {
							if(sockServer[i].output != null)
								sockServer[i].sendData( "\n------------- \nNEW GAME\n------------- ");

						}

					}

				});

		buttons.add(deal, BorderLayout.SOUTH);
		buttons.add(restart, BorderLayout.SOUTH);
		frame.add(buttons,BorderLayout.SOUTH);
		dealersDisplayArea = new JTextArea(); // create displayArea
		dealersDisplayArea.setEditable(false);
		frame.add( new JScrollPane( dealersDisplayArea ), BorderLayout.CENTER );
		frame.setSize( 300, 300 );
		frame.setVisible( true );
		//		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		//		topFrame.setSize( 300, 300 ); // set size of window
		//		topFrame.setVisible( true ); // show window

	}

	public void close() {
		//		frame.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * set up and run server 
	 * @param p - port number
	 * @param maxPlayers -  max of players allowed
	 * @param bet - min bet allowed
	 */
	public void runDeal(int p, int maxPlayers,int bet)
	{
		port = p;
		minBet =bet;
		try // set up server to receive connections; process connections
		{
			server = new ServerSocket( port , 100 ); // create ServerSocket
			while ( counter < maxPlayers + 1 ) // 6 Players max per round for v1
			{
				try 
				{
					//create a new runnable object to serve the next client to call in
					sockServer[counter] = new ServerThread(counter);
					// make that new object wait for a connection on that new server object
					sockServer[counter].waitForConnection();
					if( counter == 2) {
						System.out.println(counter);

						new java.util.Timer().schedule( 
								new java.util.TimerTask() {
									@Override
									public void run() {
										// your code here
										System.out.println("blaaa");
										deal.setEnabled(true);
										deal.doClick();
									}
								}, 
								5000 
								);



					}
					// launch that server object into its own new thread
					executor.execute(sockServer[ counter ]);
					// then, continue to create another object and wait (loop)
				}
				catch ( EOFException eofException ) 
				{
					displayMessage( "\nServer terminated connection" );
				}
				finally 
				{
					++counter;
				} 
			} 
		} 
		catch ( IOException ioException ) 
		{} 
	} 

	/**
	 * manipulates displayArea in the thread
	 * @param messageToDisplay - message to be displayed on the GUI
	 */
	private void displayMessage( final String messageToDisplay )
	{
		SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run() // updates displayArea
					{
						dealersDisplayArea.append( messageToDisplay ); // append message
					} 
				}
				); 
	} 

	/**
	 * This method deals the cards to the players and the dealer
	 */
	private void dealCards(){

		try{
//			System.out.println("try");

			
			toRemove = 0;
			oldPlayers.removeAll(oldPlayers);

			playersleft = counter-1;
			newdeck.shuffle();
			dcard1 = newdeck.dealCard();
			dcard2 = newdeck.dealCard();
			displayMessage("\n/////////" );
			displayMessage("Bank Total :" +Bank.tokens );
			displayMessage("/////////\n" );
			displayMessage("\n\n" +dcard1 );
			if(!(players.size() == 0)) {
//				System.out.println("players not empty");
				
				System.out.println("-------------");
				System.out.println("counter: "+counter);
				System.out.println("players: " + players.size());
				System.out.println("-------------");
				
				//if new clients connected
				if(counter > players.size()) {
					Player oldDealer = players.get(players.size()-1);
//					ServerThread oldDealerCon = sockServer[players.size()-1];
					players.remove(oldDealer);
					for(int i=players.size() ;i<counter-1;i++) {
						System.out.println("iii " + i);
						Player p = new Player(100);
						System.out.println(i+" bet " +p.getBet() + " tokens " + p.getTokens() );
						players.add(p);
					}
//					for(int i=players.size();i<counter;i++) {
//						sockServer[i] = sockServer[i+1];
//					}

					System.out.println("-------------");
					System.out.println("counter: "+counter);
					System.out.println("players: " + players.size());
					System.out.println("-------------");
					
					players.add(counter-1, oldDealer);
//					sockServer[counter-1] = oldDealerCon;
				}
				System.out.println("-------------");
				System.out.println("players: " + players.size());
				System.out.println("-------------");
				
				for(int i=0;i<players.size();i++) {
					System.out.println(i+" bet " +players.get(i).getBet() + " tokens " + players.get(i).getTokens() );
					
				}
				
				for(int i =0 ;i< players.size();i++) {
					oldPlayers.add(players.get(i));
					System.out.println(i+" bet " +players.get(i).getBet() + " tokens " + players.get(i).getTokens() );
				}
				
				
			}else {
//				System.out.println("players empty");
				
				for(int i=0;i<counter;i++) {
					Player p = new Player(100);
					oldPlayers.add(p);
				}
//				System.out.println("oldPlayers set");
			}
//			System.out.println("counter: "+ counter);
//			System.out.println("playersLeft: "+ playersleft);
			players.removeAll(players);
			for (int i=1;i<= counter;i++) {
				if(i < counter) {
						System.out.println("while + i " + i);
					String gameBet = JOptionPane.showInputDialog(frame,
							"Please place bet, minimum value for game is : " + minBet, null);
					if (Integer.parseInt(gameBet) >= minBet) {
						if(Integer.parseInt(gameBet) > oldPlayers.get(i-1).getTokens() ) {
							System.out.println(i+" bet " +oldPlayers.get(i-1).getBet() + " tokens " + oldPlayers.get(i-1).getTokens() );
							sockServer[i].sendData("You dont have enough tokens");
//							System.out.println("close connection - not enough tokens");
							sockServer[i].closeConnection() ;
							playersleft--;
							toRemove++;
							socketsToRemove.add(i);
//							System.out.println("to remove");
						}else {
							//						bets.add (Integer.parseInt(gameBet));
							Card c1,c2;
							c1 = newdeck.dealCard();
							c2 = newdeck.dealCard();
							Player p = new Player(oldPlayers.get(i-1).getTokens(),c1,c2,Integer.parseInt(gameBet));
							players.add(p);
							sockServer[i].sendData("Your total amount of tokens: " + p.getTokens()+ " tokens");
							sockServer[i].sendData("Your bet: " + p.bet + " tokens");
							sockServer[i].sendData("You were Dealt:\n" + c1 + "\n" + c2);
							sockServer[i].sendData("Your Total: " +  p.getCardTotal());
						}
					}else{
						sockServer[i].sendData("Cant create your player");
						sockServer[i].closeConnection() ;
//						System.out.println("close connection - wrong bet");
						toRemove++;
						playersleft--;
						socketsToRemove.add(i);
//						System.out.println("to remove");
					} 
				}
				else if ( i == counter) {
					Card c1,c2;
					c1 = newdeck.dealCard();
					c2 = newdeck.dealCard();
					Player p = new Player(0, c1,c2,0);
					players.add(p);
				}
			}
			counter = counter - toRemove;
			if(socketsToRemove.size() !=0) {
				// remove socket that is not used anymore
				for(int j=0;j< socketsToRemove.size();j++) {
					    int indexToDelete = socketsToRemove.get(j);
					    for (int i = indexToDelete; i < sockServer.length-1; i++) {
					    		sockServer[i] = sockServer[i+1];
					    }
					    sockServer = Arrays.copyOf(sockServer, sockServer.length - 1);
					    for (int i = 0; i < sockServer.length; i++) {
//					        System.out.println("The element at index " + i + ": " + sockServer[i]);
					    }
				}
				socketsToRemove.removeAll(socketsToRemove);
			}
//			System.out.println("Update counter " + counter);
//			System.out.println("players length:" + players.size());
			for ( int i=0;i<players.size();i++) {

//				System.out.println(i+" bet " +players.get(i).getBet() + " tokens " + players.get(i).getTokens() );
			}

		}
		catch(NullPointerException n){}
	}

	/**
	 * This methods outputs the result of the game after all the players stand
	 * @returns void
	 */
	private void results() {
		try{
			for (int i=1;i<= counter;i++) {
				sockServer[i].sendData("Dealer has " + dealerCards.getCardTotal());
				//when dealer and player are under 21
				if( (dealerCards.getCardTotal() <= 21) && (players.get(i-1).getCardTotal() <= 21 ) ){

					if (dealerCards.getCardTotal() > players.get(i-1).getCardTotal()) {
						players.get(i-1).substractTokens(players.get(i-1).getBet());
						sockServer[i].sendData("\n You Lose!");
						sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
						Bank.addTokens(players.get(i-1).getBet());
						System.out.println("Bank total: " + Bank.tokens);
					}
					if (dealerCards.getCardTotal() < players.get(i-1).getCardTotal()) {
						players.get(i-1).addTokens(players.get(i-1).getBet());
						sockServer[i].sendData("\n You Win!");
						sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
						Bank.substractTokens(players.get(i-1).getBet());
						System.out.println("Bank total: " + Bank.tokens);
					}
					if (dealerCards.getCardTotal() == players.get(i-1).getCardTotal()) {
						//if the dealer and the player have the same total we check the number of cards 
						//if the player has less cards -> Player wins
						if(dealerCards.getNumberOfCards() > players.get(i-1).getNumberOfCards()) {
							players.get(i-1).addTokens(players.get(i-1).getBet());
							sockServer[i].sendData("\n You Win!");
							sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
							Bank.substractTokens(players.get(i-1).getBet());
							System.out.println("Bank total: " + Bank.tokens);
						}else if(players.get(i-1).getNumberOfCards() > dealerCards.getNumberOfCards()) {
							players.get(i-1).substractTokens(players.get(i-1).getBet());
							sockServer[i].sendData("\n You Lose!");
							sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
							Bank.addTokens(players.get(i-1).getBet());
							System.out.println("Bank total: " + Bank.tokens);
						}else {
							sockServer[i].sendData("\n Tie!");
							sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
							System.out.println("Bank total: " + Bank.tokens);
						}
					}

				}

				if(dealerCards.checkBust()){

					if(players.get(i-1).checkBust()){
						sockServer[i].sendData("\n Tie!");
						sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
						System.out.println("Bank total: " + Bank.tokens);
					}
					if(players.get(i-1).getCardTotal() <= 21){
						players.get(i-1).addTokens(players.get(i-1).getBet());
						sockServer[i].sendData("\n You Won!");
						sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
						Bank.substractTokens(players.get(i-1).getBet());
						System.out.println("Bank total: " + Bank.tokens);

					}
				}

				if(players.get(i-1).checkBust() && dealerCards.getCardTotal() <= 21){
					players.get(i-1).substractTokens(players.get(i-1).getBet());
					sockServer[i].sendData("\n You Lose!");
					sockServer[i].sendData("\n Your total amount of tokens: " + players.get(i-1).getTokens());
					Bank.addTokens(players.get(i-1).getBet());
					System.out.println("Bank total: " + Bank.tokens);
				}

			}


		}//end try block
		catch(NullPointerException n){}
	}
	
	/**
	 * This methods is used to shift the ServerThread Array in case one of the clients leave - it takes the id of the client that left
	 * @param id - id of the ServerThread disconnected
	 */
	public void updateSockServer(int id) {
		System.out.println("updateSockServer");
		
		    int indexToDelete = id;
		    for (int i = indexToDelete; i < sockServer.length-1; i++) {
		    		sockServer[i] = sockServer[i+1];
		    }
		    sockServer = Arrays.copyOf(sockServer, sockServer.length - 1);
		    for (int i = 0; i < sockServer.length; i++) {
		        System.out.println("The element at index " + i + ": " + sockServer[i]);
		    }
		    if(players.get(id-1)!= null) {
	    		System.out.println("not null");
	    		System.out.println("player bet " + players.get(id-1).getBet() + " player token " + players.get(id-1).getTokens());
		    		players.remove(id-1);
		    }
	}
	/**
	 *  This new Inner Class implements Runnable and objects instantiated from this
	 *  class will become server threads each serving a different client
	 */
	private class ServerThread implements Runnable
	{
		/**
		 * output stream to client
		 */
		private ObjectOutputStream output; 
		/**
		 * input stream from client
		 */
		private ObjectInputStream input;
		/**
		 * connection to client
		 */
		private Socket connection;
		/**
		 * the id of the connection
		 */
		private int myConID;

		/**
		 * This method takes a counter and creates a client thread for this client
		 * @param counterIn - counter of ServerThread
		 */
		public ServerThread(int counterIn)
		{
			myConID = counterIn;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				try {
					getStreams(); // get input & output streams
					processConnection(); // process connection

				} // end try
				catch ( EOFException eofException ) 
				{
					displayMessage( "\nServer" + myConID + " terminated connection" );
				}
				finally
				{
					closeConnection(); //  close connection


				}// end catch
			} // end try
			catch ( IOException ioException ) 
			{} // end catch
		} // end try

		// wait for connection to arrive, then display connection info
		/**
		 * This method is used when the dealer is waiting for connections
		 * @throws IOException
		 */
		private void waitForConnection() throws IOException
		{

			displayMessage( "Waiting for connection" + myConID + "\n" );
			connection = server.accept(); // allow server to accept connection            
			displayMessage( "Connection " + myConID + " received from: " +
					connection.getInetAddress().getHostName() );
		} // end method waitForConnection

		/**
		 * This method is used to get the streams
		 * @throws IOException
		 * @returns void 
		 */
		private void getStreams() throws IOException
		{
			// set up output stream for objects
			output = new ObjectOutputStream( connection.getOutputStream() );
			output.flush(); // flush output buffer to send header information

			// set up input stream for objects
			input = new ObjectInputStream( connection.getInputStream() );

			displayMessage( "\nGot I/O streams\n" );
		} // end method getStreams
		// process connection with client
		/**
		 * This method is used to process the connection of the thread
		 * @throws IOException
		 */
		private void processConnection() throws IOException
		{
			String message = "Connection " + myConID + " successful";
			sendData( message ); // send connection successful message


			do // process messages sent from client
			{ 
				try // read message and display it
				{
					if(message.contains("hit")){				
						cardhit();
					}

					if(message.contains("stand")){
						this.sendData("Please Wait");
						playersleft--;
						System.out.println("stand - playersLeft: " + playersleft);
						checkDone();
					}
					if(message.contains("leave")) {
						System.out.println("leave leave leave");
						playersleft--;
						counter--;
						updateSockServer(myConID);
						checkDone();
					}


					message = ( String ) input.readObject(); // read new message

				} 
				catch ( ClassNotFoundException classNotFoundException ) 
				{
					displayMessage( "\nUnknown object type received" );
				} 

			} while ( !message.equals( "CLIENT>>> TERMINATE" ) );
		} 

		/**
		 * This method is used when it's the dealer's turn
		 */
		private void dealerGo() {
			displayMessage("\n" +dcard2 + "\n" );
			dealerCards = new Player(dcard1,dcard2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (dealerCards.getCardTotal() < 17){
				while(dealerCards.getCardTotal() < 17){
					Card card1 = newdeck.dealCard();
					dealerCards.cardHit(card1);
					displayMessage("Dealer Hits \n" + card1 +  "\n" + "Total:" + dealerCards.getCardTotal() + "\n");				
				}
			}
			if(dealerCards.checkBust()){
				displayMessage("Dealer Busts!");
			}
			else{
				displayMessage("Dealer has" + " " + dealerCards.getCardTotal());
			}

			results();


		}

		/**
		 * This method is used when we have a card hit 
		 */
		private void cardhit() {
			Card nextc = newdeck.dealCard();
			sendData(nextc.toString());
			players.get(this.myConID -1).cardHit(nextc);
			sendData("Your Total: " +  players.get(this.myConID -1).getCardTotal());
			//if player busted
			if(players.get(this.myConID -1).checkBust()) {			
				sendData("Bust!\n");		
				playersleft--;
				System.out.println("card Hit - playersleft : " + playersleft);
				checkDone();
			}
		}

		/**
		 * T	his method checks if players are still in the game
		 * @return void
		 */
		private void checkDone() {

			if(playersleft==0){

				dealerGo();
			}
		}

		/**
		 * This method closes stream and socket
		 * @return void
		 */
		private void closeConnection() 
		{
			displayMessage( "\nTerminating connection " + myConID + "\n" );


			try 
			{
				output.close(); // close output stream
				input.close(); // close input stream
				connection.close(); // close socket
				System.out.println("Close connection - playersLeft: " + playersleft);
			} // end try
			catch ( IOException ioException ) 
			{}
		} 

		/**
		 * This method sends data to the server
		 * @param message - message to be set to the server
		 * @return void
		 */
		private void sendData( String message )
		{
			try 
			{
				output.writeObject( message );
				output.flush(); // flush output to client

			} 
			catch ( IOException ioException ) 
			{
				dealersDisplayArea.append( "\nError writing object" );
			} 
		} 
	} 
}

