package Blackjack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
public class Blackjack {
	
	/**
	 * represents the game
	 */
	static Game game;
    public static void main(String[] args) throws IOException {
    		game = new Game();
  
        //Tables
        startTable1();
        startTable2();

    }

    public static void startTable1() {
        (new Thread() {
            @Override
            public void run() {
                try {
                	 Table table = new Table (1,null,23555,6,0,10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void startTable2() {
        (new Thread() {
            @Override
            public void run() {
            	try {
            	 	 Table table = new Table (2,null,23556,6,0,10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }).start();
    }
    
  
}