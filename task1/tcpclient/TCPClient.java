package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        // allocate a static buffer and a dynamic buffer
        byte[] fromServerBuffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        // create a new socket with a connection to a port on a host
        Socket clientSocket = new Socket(hostname, port); 

        if(toServerBytes != null){
            // send data from client to the server via connection
            clientSocket.getOutputStream().write(toServerBytes);
        }

        // run a loop to get the data from the inputStream and store it first in  
        // the static buffer and then move it to the dynamic buffer  
        int length;
        while((length = clientSocket.getInputStream().read(fromServerBuffer)) != -1){
            byteArrayOutputStream.write(fromServerBuffer,0, length);
        }
       
        // close the socket and return the recieved data as byte array 
        clientSocket.close(); 
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] askServer(String hostname, int port) throws IOException
    {
        byte [] toServerBytes = null;
        return askServer(hostname, port, toServerBytes);
    }
}
