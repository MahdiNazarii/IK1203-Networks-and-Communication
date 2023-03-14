package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public boolean shutdown = false;
    public Integer timeout = null;
    public Integer limit = null;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        // allocate a static buffer and a dynamic buffer
        byte[] fromServerBuffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        // create a new socket with a connection to a port on a host
        Socket clientSocket = new Socket(hostname, port); 
        try{
            if(toServerBytes != null){ 
                // send data from client to the server via connection
                clientSocket.getOutputStream().write(toServerBytes);
            }
            
            // check the shutdown flag
            if(this.shutdown == true){
                clientSocket.shutdownOutput();
            }
            if(this.limit != null && this.limit > fromServerBuffer.length){
                fromServerBuffer = new byte[this.limit];
            }
            int length;
            if(this.timeout != null){
                clientSocket.setSoTimeout(this.timeout);
            }
            while((length = clientSocket.getInputStream().read(fromServerBuffer)) != -1){
                if(this.limit != null && this.limit <= length){
                   byteArrayOutputStream.write(fromServerBuffer,0, this.limit);
                   break;
                }
                byteArrayOutputStream.write(fromServerBuffer,0, length); 
            }
    
            clientSocket.close(); 
            return byteArrayOutputStream.toByteArray();

        }catch (SocketTimeoutException ex){
            System.out.println("Timeout Expired!" + ex);
            clientSocket.close(); 
            return byteArrayOutputStream.toByteArray();
        }
    }


    public byte[] askServer(String hostname, int port) throws IOException {
        byte [] toServerBytes = null;
        return askServer(hostname, port, toServerBytes);
    }
}