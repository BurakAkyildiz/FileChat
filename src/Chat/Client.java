package Chat;


import static Chat.FileChatConstants.USER_REQUEST_TYPE_DISCONNECT;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles connection between FileChat server and user.
 * Jobs;
 * -Holds main connection between server and user.
 * -Sends and reads message to/from server(Chat messages and User requests). Also sends private message to anyone in room over server.
 * -Encrypts system message and starts the requested function.
 * @author Burak
 */
public class Client implements Runnable,FileChatConstants
{
    int serverChatPort; //Servers main message port. (System requests and Chat messages)
    int uploadPort; // Clients file upload port.
    String cIp,serverIp; //Clients and servers ip adress. (This ip adress depends on connection type. Local or Remote.)
    Socket cSocket; //Clients socket for server connection.
    
    PrintWriter cOut; //To send messages.
    BufferedReader cIn; //To read messages
    
    String cName; // User name.
    private boolean endClient = false; // if its true client connection will be finished.
    
    /**
     * Creates connection to the server.
     * Opens reader and writer streams.
     * Checks if connected server is realy filechat server. if its not client will be 
     * If someone has the same username it will be changed with adding a number to name string.
     * @throws Exception 
     */
    public Client(String serverIp,int chatPort,int filePort,String cName) throws Exception
    {       
        
        this.serverIp = serverIp;
        this.serverChatPort = chatPort;
        this.uploadPort = filePort;
        this.cName = cName;
        
        try {
            cSocket = new Socket(this.serverIp,this.serverChatPort);
            this.cIp = cSocket.getLocalSocketAddress().toString().split(":")[0].substring(1);
            System.out.println("Client created : "+cSocket.getLocalSocketAddress());
            if(cIp == null)
                throw new Exception("Unknown Host ! Cant create Client...");
            cOut = new PrintWriter(cSocket.getOutputStream(),true);
            cIn = new BufferedReader(
                    new InputStreamReader(cSocket.getInputStream()));
            cSocket.setSoTimeout(7000);
            
            String welcomeMessage = cIn.readLine();
            if(welcomeMessage.equals(SYSTEM_MESSAGE_WELLCOME))
            {
                cOut.println(SYSTEM_MESSAGE_WELLCOME);
                cOut.println(cName);
                this.cName = cIn.readLine();
            }else
            {
                System.out.println("Not have a welcome message ! BYE BYE !!!");
                
                if(cOut != null)
                    cOut.close();
                if(cIn != null)
                    cIn.close();
                if(cSocket != null)
                    cSocket.close();
                
                return;
            }
            
            cSocket.setSoTimeout(0);
            
            new Thread(this).start(); // Starts to reading messages from server.
            
        }
        catch (Exception e) {
            System.err.println("Error on creating client :"+e.getMessage());
            throw e;
        }
    }

    
    /**
     * Sends message to server.
     */
    public void sendMessage(String message)
    {
        
        if (!message.equals("")) {
           cOut.println(message); 
        }
        
    }
    
    /**
     * Sends private message request to anyone in room over server.
     */
    public void sendPrivateMessage(String message)
    {
        
        String name = message.split(" ")[0].substring(1);
        String privateMessage = message.substring(name.length()+1);
        
        if(!FileChat.monitor.clientNameList.contains(name))
        {
            FileChat.monitor.addNewMessage("*** "+name+" Kullanıcısı Bulunamadı ***");
        }else if(!cName.equals(name))
        {
            sendRequestMessage(USER_REQUEST_TYPE_PRIVATE_MESSAGE, name + SYSTEM_MESSAGE_BRACKET + privateMessage);
            
        }
        
    }
    
    @Override
    public void run()
    {
        String message;
        System.out.println("Client running. Name of Client is :"+cName);
        
        while (!endClient) {//Reads message from server until get an exception.
            try {
                message = cIn.readLine();
                if(message == null) throw new NullPointerException();
                if(message.startsWith(SYSTEM_MESSAGE)) //If the message is system request handles it.
                    workSystemMessage(message);
                else
                {
                    FileChat.monitor.addNewMessage(message); // adds monitor the new chat message.
                }
               
            }
            catch (Exception e) {
                System.err.println("Error on running Client :"+e.getMessage());
                e.printStackTrace();
                if(e == null||e instanceof NullPointerException || e.getMessage().equalsIgnoreCase("null")) // if server connection is closed client will get null message.
                {
                    FileChat.monitor.disconnect();
                    
                }else if(e.getMessage().equals("Connection reset")) // If server closes the connection
                {
                    FileChat.monitor.addNewMessage("***************************   CONNECTİON LOST !!!   ***************************");
                    FileChat.monitor.disconnect();
                    
                    try {
                        this.finalize();
                    }
                    catch (Throwable ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    FileChat.monitor.disconnect();
                    break;
                }
            }
               
        }
        
    }
    
    
    
    /**
     * Encryptes and runs the request message.
     * It uses FileChatConstants.SYSTEM_MESSAGE_BRACKET to encrypte the message.
     * @param sysMessage 
     */
    private void workSystemMessage(String sysMessage)
    {
        
        String[] spSysMessage = sysMessage.split(SYSTEM_MESSAGE_BRACKET);
        int splitedTypeIndex = SYSTEM_MESSAGE_TYPE_SEQUENCE;
        switch(spSysMessage[splitedTypeIndex])
        {
            case SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST:// Gets new user list and list it to monitor.
            {
                FileChat.monitor.clientNameList = new ArrayList<>();
                for (int i = splitedTypeIndex + 1; i < spSysMessage.length; i++)
                {
                     
                    FileChat.monitor.clientNameList.add(spSysMessage[i]);
                }
                FileChat.monitor.refreshClientNameList();
                break;
            }
            case SYSTEM_MESSAGE_TYPE_INFO://Gets new system info message(Like new user or new file share.)
            {
                FileChat.monitor.addNewMessage(SYSTEM_MESSAGE_BRACKET+"SYSTEM : "+spSysMessage[splitedTypeIndex+1]);
                //System.out.println("Client get info message : "+sysMessage);
                break;
            }
            case USER_REQUEST_TYPE_DISCONNECT://If some one is disconnected system will send this message. It removes shared files which shared by disconnected user.
            {
                System.out.println("Disconnect message : "+sysMessage);
                FileChat.monitor.fileSv.removeUsersFiles(spSysMessage[splitedTypeIndex+1], "", "");
                FileChat.monitor.refreshFileTable();
                break;
            }
            case USER_REQUEST_TYPE_CHANGE_NAME://If some user changes his name
            {
                System.out.println("Change Name Message :"+sysMessage);
                FileChat.monitor.fileSv.editSharedFile(spSysMessage[splitedTypeIndex+1],spSysMessage[splitedTypeIndex+2]);
                FileChat.monitor.refreshFileTable();
                break;
            }
            case USER_REQUEST_TYPE_UPLOAD:// New file request from downloader.
            {
                String name_ip_port = spSysMessage[3];
                String name = name_ip_port.split(":")[0];
                String ip = name_ip_port.split(":")[1];
                String port = name_ip_port.split(":")[2];
                String fileId = spSysMessage[4];
                FileChat.monitor.uploadRequest(name,ip,port,fileId);
                break;
            }
            case USER_REQUEST_TYPE_DOWNLOAD:// Info message to downloader when uploading is ready
            {
                String name_ip_port = spSysMessage[3];
                String name = name_ip_port.split(":")[0];
                String ip = name_ip_port.split(":")[1];
                String port = name_ip_port.split(":")[2];
                String fileId = spSysMessage[4];
                FileChat.monitor.downloadRequest(name,ip,port,fileId);
                break;
            }
            case USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED://If downloader accepted the download it starts upload.
            {
                String name_ip_port = spSysMessage[3];
                String name = name_ip_port.split(":")[0];
                String ip = name_ip_port.split(":")[1];
                String port = name_ip_port.split(":")[2];
                String fileId = spSysMessage[4];
                String fileAccept = spSysMessage[5];
                System.out.println("Is file accepted at client :"+fileAccept);
                FileChat.monitor.acceptUpload(name,ip,port,fileId,fileAccept.equals("true"));

                
                break;
            }
            case USER_REQUEST_TYPE_NEW_SHAREDFILE:// New shared file.
            {
                String[] splitedMsg = sysMessage.split(SYSTEM_MESSAGE_BRACKET);
                System.out.println("Getted request new shared file : "+sysMessage);
                String[] fileNameList = splitedMsg[11].split("<");
                File[] fileList = new File[fileNameList.length];
                
                for (int i = 0; i < fileNameList.length; i++) {
                    fileList[i] = new File(fileNameList[i]);
                }
                
                SharedFile file = new SharedFile(splitedMsg[3], splitedMsg[4], splitedMsg[5], splitedMsg[6], splitedMsg[7], splitedMsg[8], splitedMsg[9], splitedMsg[10], fileList);
                System.out.println("New Shared File :"+file);
                FileChat.monitor.addNewMessage("(-- "+file.getOwnerNick()+" shared "+file.getName()+" --)");
                FileChat.monitor.fileSv.addNewSharedFile(file);
                FileChat.monitor.refreshFileTable();
                if(FileChat.monitor.isAutomaticDownload)
                    FileChat.monitor.downloadSharedFile(file);
                break;
            }
            case USER_REQUEST_TYPE_REMOVE_SHAREDFILE:// Removes shared file.
            {
                String[] splitedMsg = sysMessage.split(SYSTEM_MESSAGE_BRACKET);
                FileChat.monitor.fileSv.deleteSharedFile(splitedMsg[3]);
                FileChat.monitor.refreshFileTable();
                break;
            }
            case USER_REQUEST_TYPE_EDIT_SHAREDFILE:// Replaces old shared file with new one.
            {
                String[] splitedMsg = sysMessage.split(SYSTEM_MESSAGE_BRACKET);
                String id = splitedMsg[3];
                String[] fileNameList = splitedMsg[12].split("<");
                File[] fileList = new File[fileNameList.length];
                
                for (int i = 0; i < fileNameList.length; i++) {
                    fileList[i] = new File(fileNameList[i]);
                }
                
                SharedFile file = new SharedFile(splitedMsg[4], splitedMsg[5], splitedMsg[6], splitedMsg[7], splitedMsg[8], splitedMsg[9], splitedMsg[10], splitedMsg[11], fileList);
                FileChat.monitor.fileSv.editSharedFile(Integer.parseInt(id), file);
                FileChat.monitor.refreshFileTable();
                FileChat.monitor.addNewMessage("(--"+file.getOwnerNick()+" edited  "+file.getName()+"--)");
                break;
            }
            default: break;
                
        }
        
    }
    
    public void sendRequestMessage(String USER_REQUEST_TYPE,String requestMessage)
    {
        
        String[] splitedRequestMessage = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
            switch(USER_REQUEST_TYPE)
            {
                case USER_REQUEST_TYPE_SHARED_FILE_LIST: // to get shared file list.
                {
                    this.cOut.println(USER_REQUEST_SHARED_FILE_LIST);
                    break;
                }
                case USER_REQUEST_TYPE_CHANGE_NAME://request message = newName
                                                    //to change user name
                {
                    this.cName = requestMessage;
                    FileChat.monitor.setMonitorTitle(cName);
                    this.cOut.println(USER_REQUEST_CHANGE_NAME+requestMessage);
                    break;
                }
                case USER_REQUEST_TYPE_PRIVATE_MESSAGE://request message = user@message
                                                        //to send private message
                {
                    String[] splitedMsg = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
                    FileChat.monitor.addNewMessage("PM TO "+splitedMsg[0]+" : "+splitedMsg[1]);
                    this.cOut.println(USER_REQUEST_PRIVATE_MESSAGE+requestMessage);
                    break;
                }
                case USER_REQUEST_TYPE_DISCONNECT://request message = ""
                                                    //To send info message about own disconnection.
                {
                    if(cSocket.isConnected())
                        this.cOut.println(USER_REQUEST_DISCONNECT);
                    break;
                }
                case USER_REQUEST_TYPE_UPLOAD://request message = "requesterUserName:ip:port@sharedFileId"
                                                //Request from some user to upload his shared file.
                {
                    
                    this.cOut.println(USER_REQUEST_UPLOAD+requestMessage);
                    
                    break;
                }
                case USER_REQUEST_TYPE_DOWNLOAD://request message = "requesterUserName:ip:port@sharedFileId"
                                                //To start upload connection between downloader.
                {
                    
                    this.cOut.println(USER_REQUEST_DOWNLOAD+requestMessage);
                    
                    break;
                }
                case USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED://request message = "requesterUserName:ip:port@sharedFileId@isAccepted"
                                                            //To start download if client is still wanting to download file
                {
                    
                    this.cOut.println(USER_REQUEST_DOWNLOAD_ISACCEPTED+requestMessage);
                    
                    break;
                }
                case USER_REQUEST_TYPE_NEW_SHAREDFILE://request message = "name@parentDirectoryPath@notification@fileSize@ownerNick@ownerIP@ownerPort@status@shareTime@fileList(path<path<path)"
                                                        // To share new file.
                {
                    this.cOut.println(USER_REQUEST_NEW_SHAREDFILE+requestMessage);
                    break;
                }
                case USER_REQUEST_TYPE_REMOVE_SHAREDFILE://request message = "fileId"
                                                            // To remove own shared file
                {
                    this.cOut.println(USER_REQUEST_REMOVE_SHAREDFILE+requestMessage);
                    break;
                }
                case USER_REQUEST_TYPE_EDIT_SHAREDFILE://request message = "fileId@name@parentDirectoryPath@notification@fileSize@ownerNick@ownerIP@ownerPort@status@shareTime@fileList(path<path<path)"
                                                        // To edit own shared file.
                {
                    this.cOut.println(USER_REQUEST_EDIT_SHAREDFILE+requestMessage);
                    break;
                }
                
                    
                default: break;
            }
        
    }
    
    
    //Closes client connections.
    public void closeClient()
    {
        sendRequestMessage(USER_REQUEST_TYPE_DISCONNECT, cName);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                
                endClient = true;
                try {
                    if(cSocket != null)
                        cSocket.close();
                    if(cIn != null)
                        cIn.close();
                    if(cOut != null)
                        cOut.close();
                    FileChat.monitor.addNewMessage("***************************   CONNECTİON LOST !!!   ***************************");
                    FileChat.monitor.setTitle("No Connection !");
                    System.out.println("Client Closed !");
                    this.finalize();
                    
                }               
                catch (Exception e) {
                    System.out.println("Error on closing client : "+e.getMessage());
                }
                catch (Throwable ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        
    }
    
    
    
}
