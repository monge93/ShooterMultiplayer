import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class  Client implements Runnable 
{
   private Socket connection; // connection to server
   private Scanner input; // input from server
   private Formatter output; // output to server
   private String  Host; // host name for server
   private String myMark; // this client's mark
   private final static int PORT = 4444;

   public  Client( String host )
   { 
      Host = host;
      startClient();
   } // end  Client constructor

   // start the client thread
   public void startClient()
   {
      try // connect to server, get streams and start outputThread
      {
         // make connection to server
         connection = new Socket( 
            InetAddress.getByName(  Host ), PORT );

         // get streams for input and output
         input = new Scanner( connection.getInputStream() );
         output = new Formatter( connection.getOutputStream() );
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();         
      } // end catch

      // create and start worker thread for this client
      ExecutorService worker = Executors.newFixedThreadPool( 1 );
      worker.execute( this ); // execute client
   } // end method startClient

   // control thread that allows continuous update of displayArea
   public void run()
   {
   
      // receive messages sent to client and output them
      while ( true )
      {
         if ( input.hasNextLine() )
            processMessage( input.nextLine() );
      } // end while
   } // end method run

   // process messages received by client
   private void processMessage( String message )
   {
      System.out.println(message);
   } // end method processMessage

   public void send(String m){
         System.out.println("ESTOU ENVIANDO!");
         output.format(m);
         output.flush();
   }


 public static void main( String args[] )
   {
       Client application;
      Scanner scan = new Scanner (System.in);    
      if ( args.length == 0 )
         application = new  Client( "127.0.0.1" );
      else
         application = new  Client( args[ 0 ] );
     while(true){
         application.send(scan.nextLine());
      }

   } 
}
