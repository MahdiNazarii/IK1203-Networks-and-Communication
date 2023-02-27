import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import tcpclient.*;

public class HTTPAsk {

    static String request = null;
    static String hostname = null;
    static Integer port = null;
    static String data = null;
    static byte [] fromBrowserBuffer = new byte[1024];
    static String[] getReq;
    static String[] parameters;
    static byte[] toServerBytes = new byte[0];
    static boolean shutdown = false;
    static Integer limit = null;
    static Integer timeout = null;
    static boolean httpType = false; 

    public static void main( String[] args) throws Exception{
        // create a serverSocket with a port number at args[0]
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

        // run the server in an infinit loop 
        while(true) { 
            try{ 
                // Listens for a connection to be made to this socket and accepts it
                Socket socket = serverSocket.accept(); 
                System.out.println("Server is ready...");

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
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] serverBytes = tcpClient.askServer(hostname, port, toServerBytes);
                        String serverOutput = new String(serverBytes);

                        String outputData = "HTTP/1.1 200 OK\r\n\r\n" + serverOutput;

                        // write the data to the outputstream/host
                        outputStream.write(outputData.getBytes()); 
                       // System.out.println("Output Stream: "+ serverOutput); 
                    }
                    catch(Exception ex){ // the tcpClient could not find the server
                        outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                    }
                } 
                else{ // in Get request there was either no "/ask" or hostname and port-number was null
                    outputStream.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                    // System.out.println("BAD REQUEST!!!");
                }
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
}

