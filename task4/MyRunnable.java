import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import tcpclient.*;

public class MyRunnable implements Runnable{
    Socket socket;

    public MyRunnable(Socket connectionSocket) {
        this.socket = connectionSocket; 
    }
 
    public void run() {
        String request = null;
        String hostname = null;
        Integer port = null;
        String data = null;
        byte [] fromBrowserBuffer = new byte[1024];
        String[] getReq;
        String[] parameters;
        byte[] toServerBytes = new byte[0];
        boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;
        boolean httpType = false; 

        try{ 
            // Create input/outputstream for reading the GET request from a host and writing the data to the host
            InputStream inputStream = socket.getInputStream(); 
            OutputStream outputStream = socket.getOutputStream();

            inputStream.read(fromBrowserBuffer);
            request = new String(fromBrowserBuffer, StandardCharsets.UTF_8);
            
            
            //split request into three sections. GET , /ask... and HTTP/1.1
            getReq= request.split("\\R")[0].split(" ");

            System.out.println(request.split("\\R")[0]);

            // extract the tcpClient parameters from GET request
            parameters = getReq[1].split("[\\?\\=\\&]");

            // check if the GET request is valid
            if(getReq[0].equals("GET")){
                for(int i = 0; i < parameters.length; i++){
                    if(parameters[i].equals("hostname"))
                        hostname = parameters[++i];

                    else if(parameters[i].equals("port"))
                        port = Integer.parseInt(parameters[++i]);

                    else if(parameters[i].equals("limit"))
                        limit = Integer.parseInt(parameters[++i]);

                    else if(parameters[i].equals("timeout"))
                        timeout = Integer.parseInt(parameters[++i]);

                    else if(parameters[i].equals("shutdown"))
                        shutdown = true;

                    else if(parameters[i].equals("string"))
                        data = parameters[++i] + "\n";

                }
                if(getReq[2].equals("HTTP/1.1"))
                    httpType = true;
            }

            // check the data to server
            if(data != null){
                toServerBytes = data.getBytes();
            }

            // If the Get request contains "/ask" and the non-optional arguments are not null, 
            // use tcpClient.askServer() to get the response
            if(parameters[0].equals("/ask") && hostname != null && port != null && httpType){
                try{
                    System.out.println("start ");
                    TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                    byte[] serverBytes = tcpClient.askServer(hostname, port, toServerBytes);
                    System.out.println("FromServer ");
                    String serverOutput = new String(serverBytes);

                    String outputData = "HTTP/1.1 200 OK\r\n\r\n" + serverOutput;

                    // write the data to the outputstream/host
                    outputStream.write(outputData.getBytes()); 
                   System.out.println("Output Stream: "+ outputData); 
                }
                catch(Exception ex){ // the tcpClient could not find the server
                    outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                }
            } 
            else{ // in Get request there was either no "/ask" or hostname and port-number was null
                outputStream.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                // System.out.println("BAD REQUEST!!!");
            }
            System.out.println("Client was served!");  

            // close input/outputstream and socket 
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch(Exception ex){
            System.out.println("Catched exception: " + ex);
        }
    } 
        
}   

