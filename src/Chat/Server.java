package Chat;


import static Chat.FileChatConstants.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This class creates server file chat app and gets new users.
 * @author Burak 
 */
public class Server implements Runnable,FileChatConstants
{
    //INSTANCE VARIABLES
    //-----------------------------------------------
    int serverPort; //Main connection port to listen for new users.
    ServerSocket sSocket; // Server socket.
    Socket cSocket; // Connected users socket.
    PrintWriter cOut; // Connected users out stream.
    BufferedReader cIn; // connected users input stream.
    String sIp; // Server ip)
    String sName; // Server Name
    ArrayList<ClientConnection> clientList; // Connected users control list.
    private boolean endServer = false; // If its true server will be closed after next getted message.
    int uploadPort; // Servers file upload port.
        
    //------------------------------------------------
    
    
    /**
     * Creates server and starts listening to new users.
     * If server port is in use or not available throws Exception.
     * @param sName
     * @param chatPort
     * @param filePort
     * @param svIP
     * @throws Exception 
     */
    public Server(String sName,String svIP,int chatPort,int filePort ) throws Exception
    {
        
        this.sName = sName;
        this.serverPort = chatPort;
        clientList = new ArrayList<ClientConnection>();
        
        try {
            
            this.sIp = svIP;
            if(sIp == null)
                throw new Exception("Unknown Host ! Cant create Server...");
            
            sSocket = new ServerSocket(this.serverPort);
            this.uploadPort = filePort;
            FileChat.monitor.addNewMessage("--> Server Created.");
            new Thread(this).start();
        }
        catch (Exception e) {
            System.err.println("Error on creating server :"+e.getMessage());
            
            if(e.getMessage().contains("Address already in use"))
                throw new Exception("This port is using by an other program !");
            else
                throw e;
        }
        
    }
    
    
    /**
     * Checks the connected socket if its a real user or not.
     * Sends welcome message and waits for answer if it answers creates ClientConnection instance and user can enter the room.
     * @param cSocket
     * @param cOut
     * @param cIn
     * @return isConnected socket is user or not
     */
    private boolean isConnectionTrue(Socket cSocket,PrintWriter cOut,BufferedReader cIn)
    {
        try {
            
            cOut.println(SYSTEM_MESSAGE_WELLCOME);
            cSocket.setSoTimeout(5000);
            String answer = cIn.readLine();
            
            if(answer.equals(SYSTEM_MESSAGE_WELLCOME))
            {
                cSocket.setSoTimeout(0);
                return true;
                
            }else{return false;}
            
            
        }
        catch (Exception e) {
            System.err.println("Error on isConnectionTrue check :"+e.getMessage());
            System.out.println("Unrecognized Connection. I Said BYE BYE !!!");
        }
        catch (Throwable ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    //--------------------------------------------------------------------------
    
    /**
     * Listens the server port for new users.
     * If anyone connects checks if it is a user connection or not.
     * If its true connection adds it to ClientConnection control list.
     * Listens until server will be closed.
     */
    @Override
    public void run()
    {
        FileChat.monitor.addNewMessage("--> Server Running !");
        
        PrintWriter cOut;
        BufferedReader cIn;
        while (!endServer) {
            try {
                cSocket = sSocket.accept();
                cOut = new PrintWriter(cSocket.getOutputStream(),true);
            
                cIn= new BufferedReader(
                                    new InputStreamReader(cSocket.getInputStream()));
                if(isConnectionTrue(cSocket,cOut, cIn))
                {
                    ClientConnection clTh = new ClientConnection( cSocket,cOut,cIn);
                    clientList.add(clTh);
                    
                }
                
            }
            catch(SocketException e)
            {
                endServer = true;
                System.err.println("Socket Exception ! Ending server !");
            }
            catch (Exception e) {
                System.err.println("Error on running server :"+e.getMessage());
                e.printStackTrace();
            }
                
        }
        if(sSocket != null && !sSocket.isClosed())
        {
            closeServer();
        }
    }
    
    /**
     * Closes the Server connections and streams.
     */
    public void closeServer()
    {
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (ClientConnection clientConnection : clientList) {
                   
                    clientConnection.closeClientConnection();
                    
                    System.out.println( clientConnection.cName+" Closed.");
                }
                 try {
                       if(cIn != null) 
                          cIn.close();
                       if(cOut != null)
                           cOut.close();
                       if(cSocket != null)
                            cSocket.close();
                       if(sSocket != null)
                            sSocket.close();
                       System.out.println("Server Closed !");
                       FileChat.monitor.addNewMessage("***************************   CONNECTİON LOST !!!   ***************************");
                       FileChat.monitor.setTitle("No Connection !");
                       this.finalize();
                   }
                   catch (IOException ex) {
                       System.out.println("Error on closing server : "+ex.getMessage());
                   }
                catch (Throwable ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                    endServer = true;
                    clientList = new ArrayList<ClientConnection>();
                }
        }).start();
    }
    
    
    /**
     * Sends message to all clients.
     * @param message 
     */
    public void sendMessageToAll(String message){
        for (ClientConnection clientConnection : clientList) {
            try {
                clientConnection.sendMessage(message);
            }
            catch (Exception ex) {
                System.err.println("Exception sending message to all :"+ex.getMessage());
            }
        } 
    }
    
    
    
    /**
     * Sends system message to given client.
     * @param SYSTEM_MESSAGE_TYPE
     * @param clientConn
     * @param message 
     */
    public void sendSystemMessage(String SYSTEM_MESSAGE_TYPE,ClientConnection clientConn,String message)
    {
        try {
            if(clientConn != null)
                clientConn.sendMessage(message);
        }
        catch (Exception e) {
            System.err.println("Error on sending system message to "+clientConn.cName+" :"+e.getMessage());
        }
        
            
    }
    
    
    
    /**
     * Sends system message to all users.
     * @param SYSTEM_MESSAGE_TYPE Type of system message where defined on FileChatConstants.
     * @param systemMessage System message with encrption
     */
    public void sendSystemMessageToAll(String SYSTEM_MESSAGE_TYPE,String systemMessage)
    {
        String nameString = SYSTEM_MESSAGE_BRACKET+SYSTEM_NAME+SYSTEM_MESSAGE_BRACKET+SYSTEM_MESSAGE_TYPE+SYSTEM_MESSAGE_BRACKET;
        
        if(SYSTEM_MESSAGE_TYPE.equals(SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST)) // If system message is refres_user_list type it sends info message with it too if info message is not empty.
        {
            for (String name : FileChat.monitor.clientNameList) {
                
                nameString += SYSTEM_MESSAGE_BRACKET+name;
            }
            
            if(!systemMessage.isEmpty() && systemMessage != null)
            {
                sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_INFO, systemMessage);
                systemMessage = "";
            }
            
        }
        
        
        try {
            for (ClientConnection clientConn : FileChat.monitor.sv.clientList) {
                if(clientConn != null)
                clientConn.sendMessage(nameString+systemMessage);
            }
        }
        catch (Exception e) {
            System.err.println("Error on sending system message to all :"+e.getMessage());
        }
        
    }
    
    
    /**
     * Sends private message to user on messageString.
     * @param messageString (@userName message)
     */
    public void sendPrivateMessage(String messageString)
    {
        
        String name = messageString.split(" ")[0].substring(1);
        String privateMessage = messageString.substring(name.length()+1);
        
        if(!FileChat.monitor.clientNameList.contains(name))
        {
            FileChat.monitor.addNewMessage("*** "+name+" Kullanıcısı Bulunamadı ***");
        }else
        {
            for (ClientConnection cConn : FileChat.monitor.sv.clientList) {
                if(cConn.cName.equals(name))
                {
                    try {
                        cConn.sendMessage("**PM| "+this.sName+" :"+privateMessage);
                        FileChat.monitor.addNewMessage(SYSTEM_MESSAGE+"@ -"+"***PM TO "+name+" : "+privateMessage);
                    }
                    catch (Exception ex) {
                        System.err.println("Error on Server sending private message : "+ex.getMessage());
                    }
                    break;
                }
            }
        }
    }
}


