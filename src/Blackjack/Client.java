package Blackjack;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
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


public class Client extends JFrame {
	private String id;
	/**
	 * Hit Button of Client GUI
	 */
	private JButton hit;
	/**
	 * Stand Button of Client GUI
	 */
	private JButton stand;
	/**
	 * GUI buttons
	 */
	private JPanel buttons;
	/**
	 * display information to player
	 */
	private JTextArea displayArea;
	/**
	 * socket to communicate with server
	 */
	private Socket client;
	/**
	 * output stream to server
	 */
	private ObjectOutputStream output; 
	/**
	 * input stream from server
	 */
	private ObjectInputStream input;
	/**
	 * message from server
	 */
	private String message = ""; 
	/**
	 * host server for this application
	 */
	private String chatServer; 

	/**
	 * This methods takes a string as argument and creates a client connected to the host + creates the GUI
	 * @param host - server that will host client connections
	 * @param gameNumber - number of the dealer
	 * @param clientId - id of client
	 * @param isOwner - if this client is the owner of the table or not 
	 */
	public Client( String host, int gameNumber , String clientId, String isOwner){
		super( "Player Game: "+ gameNumber +" "+ isOwner);

		id = clientId;
		chatServer = host; // set server to which this client connects

		buttons = new JPanel();
		buttons.setLayout(new GridLayout(1,2));
		hit = new JButton("Hit");
		stand = new JButton("Stand");
		
		hit.addActionListener(
				new ActionListener() 
				{
					// send message to server
					public void actionPerformed( ActionEvent event )
					{
						sendData( "hit" );
					} 
				});
		
		stand.addActionListener(
				new ActionListener() 
				{
					// send message to server
					public void actionPerformed( ActionEvent event )
					{
						sendData( "stand" );
					} 
				});

		buttons.add(hit, BorderLayout.SOUTH);
		buttons.add(stand, BorderLayout.SOUTH);
		buttons.setVisible(true);
		add(buttons,BorderLayout.SOUTH);
		displayArea = new JTextArea(); // create displayArea
		add( new JScrollPane( displayArea ), BorderLayout.CENTER );

		setSize( 300, 300 ); // set size of window
		setVisible( true ); // show window
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	sendData( "leave" );
				System.out.println("leaving..");
//				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		    }
		});
	} // end Client constructor

//	public void close() {
//		
//	}

	public String getId() {
		return id;
	}
	/**
	 * connect to server and process messages from server
	 * @param port - port number
	 */
	public void runClient( int port){
		try{
			connectToServer(port); // create a Socket
			System.out.println("connected");
			getStreams(); // get the input and output streams
			processConnection(); // process connection
		} catch ( EOFException eofException ) {
			displayMessage( "\nClient terminated connection" );
		} catch ( IOException ioException ) {
		} finally {
			closeConnection(); // close connection
		}
	}

	/**
	 * connect to server
	 * @throws IOException
	 */
	private void connectToServer(int port) throws IOException{      
		displayMessage( "Attempting connection\n" );

		// create Socket to make connection to server
		client = new Socket( InetAddress.getByName( chatServer ), port );

		// display connection information
		displayMessage( "Connected to: " + 
				client.getInetAddress().getHostName() );
	} 

	/**
	 * get streams to send and receive data
	 * @throws IOException
	 * @returns void
	 */
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream( client.getOutputStream() );      
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream( client.getInputStream() );

		displayMessage( "\nGot I/O streams\n" );
	} // end method getStreams

	/**
	 * process connection with server
	 * @throws IOException
	 * @returns void
	 */
	private void processConnection() throws IOException {
		do {  // process messages sent from server

			try { // read message and display it
				message = ( String ) input.readObject(); // read new message
				displayMessage( "\n" + message ); // display message
				if (message.contains("Bust!") || message.contains("Please Wait")){
//					buttons.setVisible(false);				
				}
				
			}
			catch ( ClassNotFoundException classNotFoundException ) {
				displayMessage( "\nUnknown object type received" );
			} 

		} while ( !message.equals( "SERVER>>> TERMINATE" ) );
	}

	/**
	 * close streams and socket
	 * @return void
	 */
	private void closeConnection() {
		displayMessage( "\nClosing connection" );
		try {
			output.close(); 
			input.close(); 
			client.close(); 
		}catch ( IOException ioException ) {} 
	} 

	/**
	 * sends message to server
	 * @param message - message to be sent to connection
	 * @return void
	 */
	private void sendData( String message ){
		try {
			output.writeObject(  message );
			output.flush(); // flush data to output
			
		} catch ( IOException ioException ){
			displayArea.append( "\nError writing object" );
		} 
	} 

	/**
	 * takes string messageToDisplay as argument and displays it in the displayArea
	 * @param messageToDisplay - message to be displayed on the GUI
	 * @return void
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
