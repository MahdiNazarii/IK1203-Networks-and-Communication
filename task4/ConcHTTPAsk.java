import java.net.*;

public class ConcHTTPAsk{
    public static void main( String[] args) throws Exception{
        // create a serverSocket with a port number at args[0]
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

        // run the server in an infinit loop 
        while(true) { 
            // Listens for a connection to be made to this socket and accepts it
            Socket socket = serverSocket.accept(); 
            System.out.println("Server is ready...");

            // create a thread with the socket as its connection socket and call the start method
            Runnable thread = new MyRunnable(socket);
            new Thread(thread).start();

            System.out.println("A thread was created for the client!"); 
        }
    }
}