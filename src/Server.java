import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class  Server
{
   private Player[] players; // array of Players
   private ServerSocket server; // server socket to connect with clients
   private int currentPlayer; // keeps track of player with current move
   private final static int PLAYER_X = 0; // constant for first player
   private final static int PLAYER_O = 1; // constant for second player
   private final static int PORT = 4444;
   private final static String[] MARKS = { "X", "O" }; // array of marks
   private ExecutorService runGame; // will run players

   // set up tic-tac-toe server and GUI that displays messages
   public  Server()
   {
      runGame = Executors.newFixedThreadPool( 2 );

      players = new Player[ 2 ]; // create array of players
      currentPlayer = PLAYER_X; // set current player to first player
 
      try
      {
         server = new ServerSocket( PORT, 2 ); // set up ServerSocket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
         System.exit( 1 );
      } // end catch

   } // end  Server constructor

   // wait for two connections so game can be played
   public void execute()
   {
      // wait for each client to connect
      for ( int i = 0; i < players.length; i++ ) 
      {
         try // wait for connection, create Player, start runnable
         {
            players[ i ] = new Player( server.accept(), i );
            runGame.execute( players[ i ] ); // execute player runnable
         } // end try
         catch ( IOException ioException ) 
         {
            ioException.printStackTrace();
            System.exit( 1 );
         } // end catch
      } // end for
   } // end method execute
   // place code in this method to determine whether game over 
   public boolean isGameOver()
   {
      return false; // this is left as an exercise
   } // end method isGameOver

   // private inner class Player manages each Player as a runnable
   private class Player implements Runnable 
   {
      private Socket connection; // connection to client
      private Scanner input; // input from client
      private Formatter output; // output to client
      private int playerNumber; // tracks which player this is
      private String mark; // mark for this player
   //   private boolean suspended = true; // whether thread is suspended

      // set up Player thread
      public Player( Socket socket, int number )
      {
         playerNumber = number; // store this player's number
         mark = MARKS[ playerNumber ]; // specify player's mark
         connection = socket; // store socket for client
         
         try // obtain streams from Socket
         {
            input = new Scanner( connection.getInputStream() );
            output = new Formatter( connection.getOutputStream() );
         } // end try
         catch ( IOException ioException ) 
         {
            ioException.printStackTrace();
            System.exit( 1 );
         } // end catch
      } // end Player constructor
      // control thread's execution
      public void run()
      {
         // send client its mark (X or O), process messages from client
         try 
         {
            System.out.println( "Player " + mark + " connected\n" );
            output.format( "%s\n", mark ); // send player's mark
            output.flush(); // flush output

            // if player X, wait for another player to arrive
            if ( playerNumber == PLAYER_X ) 
            {
               output.format( "%s\n%s", "Player X connected",
                  "Waiting for another player\n" );
               output.flush(); // flush output

               // send message that other player connected
               output.format( "Other player connected. Your move.\n" );
               output.flush(); // flush output
            } // end if
            else
            {
               output.format( "Player O connected, please wait\n" );
               output.flush(); // flush output
            } // end else

            // while game not over
            while ( !isGameOver() ) 
            {
               if(input.hasNext()){ 
                  String buffer = input.next();
                  System.out.println(buffer);
                  output.format( buffer ); // notify client
                  output.flush(); // flush output
               } // end if
            } // end while
         } // end try
         finally
         {
            try
            {
               connection.close(); // close connection to client
            } // end try
            catch ( IOException ioException ) 
            {
               ioException.printStackTrace();
               System.exit( 1 );
            } // end catch
         } // end finally
      } // end method run
   } // end class Player
   public static void main( String args[] )
   {
       Server application = new  Server();
      application.execute();
   } // end main

} // end class  Server
