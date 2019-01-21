package Blackjack;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Game extends JFrame {

	/**
	 * JPanel of the Game
	 */
	static JPanel game ;

	/**
	 * Button to join Game1
	 */
	private  JButton game1;

	/**
	 * Button to join Game2
	 */
	private  JButton game2;
	/**
	 * Button to create a new Game
	 */
	private JButton newGame;

	/**
	 * Button to create a dealer
	 */
	private JButton createDealer;

	/**
	 * JPanel of buttons
	 */
	private JPanel buttons;
	

	/**
	 * Text Area
	 */
	private JTextArea displayArea; 
	

	/**
	 * List of dealers available
	 */
	ArrayList <Dealer> availableDealers = new ArrayList<Dealer>();
	

	/**
	 * Used for creation of Owner of Temporary tables
	 */
	Client applicationClient;
	
	/**
	 * Server thread
	 */
	Thread server;	
	/**
	 * Client thread
	 */
	Thread client;
	
	/**
	 * Counter of temporary games - starts at 2 since we have the 2 tables created automatically that are not temporary
	 */
	int counterTempGames=2;
	
	/**
	 * first port for temporary tables
	 */
    int counterTempPort = 23556;
	
	/**
	 * Number given to the game to be created
	 */
    int gameNumber;
	
	/**
	 * port number given to the game to be created
	 */
    int port;
    /**
	 * id
	 */
    String id; 
    
	/**
	 * Method to create a game GUI
	 */
	public  Game() {
		
		super( "Blackjack Options"  );

		game = new JPanel();
		game1 = new JButton("Join Game 1");
		game2 = new JButton("Join Game 2");
		newGame = new JButton("Create New Game");
		createDealer = new JButton("Create Dealer");

		buttons = new JPanel();
		buttons.setLayout(new GridLayout(2,2));


		displayArea = new JTextArea(); // create displayArea
		displayArea.setEditable(false);
		game.add( new JScrollPane( displayArea ), BorderLayout.NORTH );
		
		buttons.add(game1,BorderLayout.SOUTH);
		buttons.add(game2,BorderLayout.SOUTH);
		buttons.add(newGame,BorderLayout.SOUTH);
		buttons.add(createDealer,BorderLayout.SOUTH);
		

		game.add(buttons,BorderLayout.SOUTH);
		
		game1.addActionListener(
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
					      application = new Client( "127.0.0.1", 1 , null,""); // connect to localhost
//					      application.setDefaultCloseOperation( application.EXIT_ON_CLOSE );
					      application.runClient(23555);
//					      application.show();
					      System.out.println("client one down");
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					} 
					        }).start();
					                
				}
				});
		
		game2.addActionListener(
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
					      application = new Client( "127.0.0.1", 2 , null , ""); // connect to localhost
//					      application.setDefaultCloseOperation( application.EXIT_ON_CLOSE );
					      application.runClient(23556);
//					      application.show();
					      System.out.println("client one down");
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					} 
					        }).start();
					                
				}
				});
		
		newGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				
				String name = JOptionPane.showInputDialog(game,
                        "max number of players?", null);
	              
		
				if( name.isEmpty() == true )
				{
				// quit the loop
					System.out.println("name  equal zero");
					JOptionPane.showMessageDialog(null, "cant create your table");
				}else{
					String minBet = JOptionPane.showInputDialog(game,
	                        "Please enter the minimum value of bet accepted", null);
					if(minBet.isEmpty() == true) {
						System.out.println("name  equal zero");
						JOptionPane.showMessageDialog(null, "cant create your table");
					}else {
					if(availableDealers.size() > 0) {
						counterTempPort++;
						
						 port=counterTempPort;
						 id = UUID.randomUUID().toString();
						Table t = new Table(availableDealers.get(0));
						Dealer dealer = t.dealer;
						availableDealers.remove(dealer);	
						displayMessage( availableDealers.size() + " Available Dealers \n");

						try {
							startServer(dealer, port,dealer.gNumber,id,Integer.parseInt(name),Integer.parseInt(minBet));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					
						System.out.println("start Client " + port);
					
						
					} else {
						counterTempPort++;
						counterTempGames++;
						
						 port=counterTempPort;
						 gameNumber = counterTempGames;
						 id = UUID.randomUUID().toString();
						System.out.println("name not equal zero");
						Table table = new Table (gameNumber,id,port,Integer.parseInt(name),1, Integer.parseInt(minBet));
						
						Dealer dealer = table.dealer; 
						try {
							startServer(dealer, port,dealer.gNumber,id,Integer.parseInt(name),Integer.parseInt(minBet));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
				}
			

//				 startClient(port,gameNumber,id,d);
				
			}
			
		});
		
		createDealer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				counterTempGames++;
				Dealer d = new Dealer(counterTempGames);
				availableDealers.add(d);
				displayMessage( availableDealers.size() + " Available Dealers \n");
			}
			
		});
		
		add(game,BorderLayout.SOUTH);
		game.setLayout(new GridLayout(3,1));
		game.setSize( 300, 300 ); // set size of window
		game.setVisible( true ); // show window

		setSize( 300, 300 ); // set size of window
		setVisible( true ); // show window
	}
	/**
	 * Method to start the dealer 
	 * @param d - dealer
	 * @param port - port number
	 * @param gameNumber - game number
	 * @param id - id
	 * @param maxPlayers - max number of players
	 * @param minBet - min bet allowed
	 * @throws InterruptedException - exception in case of interruption
	 */
	public void startServer(Dealer d, int port, int gameNumber, String id, int maxPlayers, int minBet) throws InterruptedException {
//		
		
		server = new Thread() {
	            @Override
	            public void run() {
	                try {
	                	  // create server
	            	    d.showDealer(id);   
	                	d.runDeal(port, maxPlayers,minBet); // run server application
	            	     System.out.println("dealer "+ gameNumber + " " + id);
	            	    
	            	    
	                } catch (Exception e) {
	                    e.printStackTrace();
	                } finally {
	                		
	                }
	            }
	        };
	        server.start();
			startClient(port,gameNumber,id,d);
		 
	}
	/**
	 * Method to start the client 
	 * @param gPort - port
	 * @param gNumber - number of the dealer
	 * @param gId - id 
	 * @param gDealer - dealer
	 * @throws InterruptedException - exception in case of interruption
	 */
	
	public void startClient(int gPort, int gNumber, String gId, Dealer gDealer ) throws InterruptedException {
		TimeUnit.SECONDS.sleep(2);
		client = new Thread() {
	            @Override
	            public void run() {
	                try {
	                	// declare client application
	                	System.out.println("After the counter");
	                	applicationClient = new Client( "127.0.0.1", gNumber , gId , " Owner"); // connect to localhost	                	
	                	
	                	applicationClient.setDefaultCloseOperation( applicationClient.HIDE_ON_CLOSE );
	                	
	             
	               ((JFrame) applicationClient).addWindowListener(new java.awt.event.WindowAdapter() {
	            	  
	                	    @Override
	                	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	                	        if (JOptionPane.showConfirmDialog(applicationClient, 
	                	            "Are you sure you want to end this game?", "End Game?", 
	                	            JOptionPane.YES_NO_OPTION,
	                	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	                	        			applicationClient.dispose();
	                	        			gDealer.frame.hide();
//	                	        			gDealer.frame.dispatchEvent(new WindowEvent(dealer, WindowEvent.WINDOW_CLOSING));
	                	        			  
	                	   
	                	        }
	                	    }
	               });
	               System.out.println(gPort +"");
	                	applicationClient.runClient(gPort);

	     
	      System.out.println("client one down");
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	} 
	        };
	        client.start();
	}
	/**
	 * Method to display text on GUI
	 */
	private void displayMessage( final String messageToDisplay ) {
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run() {
						displayArea.append( messageToDisplay );
					} 
				}); 
	}
} 