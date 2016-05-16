package Chat;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Chat.FileChatConstants.*;

/**
 * This class handles connected users work on server side.
 * Sends servers messages to user.
 * Handles users requests.
 * @author Burak
 */
public class ClientConnection extends Thread implements FileChatConstants
{
    
    String cIp; //Clients ip
    String cPort; // Clients chat port
    
    String cName; // Clients user name (it must be unique)
    Socket cSocket; // Connected socket of client on server side.
    
    PrintWriter cOut; // To write messages.
    BufferedReader cIn; // to read messages.
    private boolean endClientConnection = false; // if its true connection will be finished after next message.
    
    
    public ClientConnection(Socket cSocket,PrintWriter cOut, BufferedReader cIn) 
    {
        
        this.cSocket = cSocket;
        this.cIp = cSocket.getInetAddress().getHostAddress();
        this.cPort = ""+cSocket.getPort();
        
        try {
            
            this.cOut = cOut;
            this.cIn = cIn;
            new Thread(this).start();
            
        }
        catch (Exception e) {
            System.err.println("Error on creating Client Connection :"+e.getMessage());
        }
        catch (Throwable ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    @Override
    public void run()
    {
        try {//Read name. Send stored messages.
            
                if(this.cName == null)
                {
                    String name = this.cIn.readLine();//Reads users name. If there is other one with same name the name will be changed by adding number to names end.
                    while(true)
                    {
                     try 
                     {
                        if(FileChat.monitor.clientNameList.contains(name) || FileChat.monitor.sv.sName.contains(name))
                        {
                            cOut.println(name+FileChat.monitor.clientNameList.size());
                            this.cName = name+FileChat.monitor.clientNameList.size();
                        } 
                        else
                        {
                            cOut.println(name);
                            this.cName = name;
                        }
                            
                        break;
                    }
                    catch (Exception e) {e.printStackTrace(); }   
                    }
                    
                    
                    FileChat.monitor.addNewMessage(this.cName+" Connected."); // Send info message to all about new user.
                    FileChat.monitor.refreshClientNameList();
                    FileChat.monitor.sv.sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST,this.cName+" Connected.");
                    
                }
                
            
        }
        catch (Exception e) {
            System.err.println("Error on Client Connection Reading name Sending all stored messages : "+e.getMessage());
        }
        
        
        while (!endClientConnection) { // Reads messages from defined user and handles it.
            try {
                String message = readMessage();
                if(message.startsWith(SYSTEM_MESSAGE)) // if message is system message runs the function about the defined request type.
                {
                   workUserRequestMessage(message); 
                }else
                {
                    FileChat.monitor.sv.sendMessageToAll(message);
                    FileChat.monitor.addNewMessage(message);
                }
                    
            }
            catch (Exception e) {
                System.err.println("Error on running Clent Connection : "+e.getMessage());
                if(e.getMessage().equals("Connection reset")) // if client closes the connection removes all shared files on him and sends info message about disconnect to all
                {
                    FileChat.monitor.addNewMessage(this.cName+" Disconnected...");
                    //SimpleChat.monitor.sv.sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_INFO, this.cName+" Disconnected...");
                    FileChat.monitor.sv.clientList.remove(this);
                    FileChat.monitor.refreshClientNameList();
                    FileChat.monitor.sv.sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST, "");
                    FileChat.monitor.fileSv.removeUsersFiles(this.cName,this.cIp,this.cPort);
                    FileChat.monitor.refreshFileTable();
                    break;
                }else if(e.getMessage().equalsIgnoreCase("socket closed"))
                {
                    endClientConnection = true;
                    try {
                        if(cOut != null)
                            cOut.close();
                        if(cIn != null)
                            cIn.close();
                        if(cSocket != null)
                            cSocket.close();
                    }
                    catch (IOException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else
                {
                    run();
                    e.printStackTrace();
                    break;
                }
                    
                
            }
        }
    }
    
    /**
     * Closes the connections about client.
     * Client socket, Reader and Writer streams and this object will be finalized.
     */
    public void closeClientConnection()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                endClientConnection = true;
                try {
                    if(cSocket != null)
                        cSocket.close();
                    if(cIn != null)
                        cIn.close();
                    if(cOut != null)
                        cOut.close();
                    
                    System.out.println("ClientConnection Closed !");
                    this.finalize();
                }
                catch (Exception e) {
                    System.out.println("Error on closing client connection : "+e.getMessage());
                }
                catch (Throwable ex) {
                    Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    
    /**
     * Simply reads message from defined client.
     * @throws Exception 
     */
    public String readMessage() throws Exception
    {
        String text = cIn.readLine();
       
        return text;
    }

    
    /**
     * Sends message to client.
     * @throws Exception 
     */
    public void sendMessage(String text) throws Exception
    {
        cOut.println(text);
    }
    
    
   /**
    * Runs user requests came from client.
    * @throws Exception 
    */
    public void workUserRequestMessage(String requestMessage) throws Exception
    {
        String[] splitRequestMessage = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
        if(splitRequestMessage[1].equals(SYSTEM_NAME))
        switch(splitRequestMessage[SYSTEM_MESSAGE_TYPE_SEQUENCE])
        {
            case USER_REQUEST_TYPE_CHANGE_NAME: // Changes clients name and sends system message to all users.
            {
                System.out.println("Change name request :"+requestMessage);
                String oldNameNotification = this.cName;
                this.cName = splitRequestMessage[SYSTEM_MESSAGE_TYPE_SEQUENCE+1];
                FileChat.monitor.fileSv.editSharedFile(oldNameNotification, cName);
                FileChat.monitor.refreshFileTable();
                FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_CHANGE_NAME, oldNameNotification+SYSTEM_MESSAGE_BRACKET+this.cName);
                oldNameNotification = oldNameNotification+" Changed Name To : "+this.cName;
                FileChat.monitor.refreshClientNameList();
                FileChat.monitor.addNewMessage( oldNameNotification );
                FileChat.monitor.sv.sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST, oldNameNotification);
                break;
            }
            case USER_REQUEST_TYPE_PRIVATE_MESSAGE: // Redirects message to private message getter.
            {
                String receiverName = "" ,message = "";
                try {
                    
                    receiverName = splitRequestMessage[3];
                    message = requestMessage.substring(   splitRequestMessage[0].length()
                                                            +splitRequestMessage[1].length()
                                                            +splitRequestMessage[2].length()
                                                            +splitRequestMessage[3].length()+5);
                }
                catch (Exception e) {
                    System.out.println("Error on private message : "+e.getMessage());
                    e.printStackTrace();
                }
                
                
                
                if(FileChat.monitor.sv.sName.equals(receiverName))
                {
                    FileChat.monitor.addNewMessage(SYSTEM_MESSAGE+"@ -"+"***PM| "+this.cName+" : "+message);
                }else
                {
                    for (ClientConnection cConn : FileChat.monitor.sv.clientList) {
                    
                        if(cConn.cName.equals(receiverName))
                        {
                            cConn.cOut.println("***PM| "+this.cName+" : "+message);
                            break;
                        }
                    }
                }
                break;      
            }
            case USER_REQUEST_TYPE_SHARED_FILE_LIST: // Sends shared file list to defined client.
            {
                ArrayList<SharedFile> shList = FileChat.monitor.fileSv.getSharedFileList();
                for (SharedFile sh : shList) {
                    this.cOut.println( USER_REQUEST_NEW_SHAREDFILE+SharedFile.toMessageString(sh) );
                }
                break;
            }
            case USER_REQUEST_TYPE_DISCONNECT: // If client wants to disconenct sends info message to all users.
            {
                FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_DISCONNECT, this.cName);
                throw new Exception("Connection reset");
            }
            case USER_REQUEST_TYPE_UPLOAD: // Redirects the upload request to shared file owner.
            {
                String fileId = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[4];
                SharedFile file = null;
                for (SharedFile sharedFile : FileChat.monitor.fileSv.getSharedFileList()) 
                {
                    if((sharedFile.getId()+"").equals(fileId))
                    {
                        file = sharedFile;
                        break;
                    }
                }
                
                String name = null,ip = null,port = null;
                if(file != null)
                {
                    name = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[0];
                    ip = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[1];
                    port = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[2];
                    
                    //System.out.println(name + " " +FileChat.monitor.sv.sName);
                    
                    if(file.getOwnerNick().equals(FileChat.monitor.sv.sName) && file.getOwnerIp().equals(FileChat.monitor.sv.sIp) )
                    {
                            FileChat.monitor.uploadRequest(name,ip,port,fileId);
                    }else
                    {
                        for (ClientConnection cConn : FileChat.monitor.sv.clientList) {
                            if(cConn.cName.equals(file.getOwnerNick()) && cConn.cIp.equals(file.getOwnerIp()))
                            {
                                FileChat.monitor.sv.sendSystemMessage(USER_REQUEST_TYPE_UPLOAD, cConn, requestMessage);
                                System.out.println("server send request message "+requestMessage);
                            }
                        }
                    }
                }
                
                
                //-----------------------------------------------------------------------------------
                //workUserFileRequests(USER_REQUEST_TYPE_UPLOAD,requestMessage);
                break;
                
            }
            case USER_REQUEST_TYPE_DOWNLOAD: // Redirect request message to downloader.
            {
                String fileId = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[4];
                SharedFile file = null;
                for (SharedFile sharedFile : FileChat.monitor.fileSv.getSharedFileList()) 
                {
                    if((sharedFile.getId()+"").equals(fileId))
                    {
                        file = sharedFile;
                        break;
                    }
                }
                
                String name = null,ip = null,port = null;
                
                if(file != null)
                {
                    name = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[0];
                    ip = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[1];
                    port = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[2];
                    System.out.println("Getted download request for : "+name+" message : "+requestMessage);
                    if(name.equals(FileChat.monitor.sv.sName))
                    {   
                        FileChat.monitor.downloadRequest(name,ip,port,fileId);
                    }else
                    {
                        for (ClientConnection cConn : FileChat.monitor.sv.clientList) {
                            if(cConn.cName.equals(name) && cConn.cIp.equals(ip))
                            {
                                FileChat.monitor.sv.sendSystemMessage(USER_REQUEST_TYPE_DOWNLOAD, cConn, requestMessage);
                                System.out.println("server send request message "+requestMessage);
                            }
                        }
                    }
                }
                //-------------
                //workUserFileRequests(USER_REQUEST_TYPE_DOWNLOAD,requestMessage);
                break;
            }
            case USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED: // Redirects the accept info to file owner.
            {
                String fileId = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[4];
                SharedFile file = null;
                for (SharedFile sharedFile : FileChat.monitor.fileSv.getSharedFileList()) 
                {
                    if((sharedFile.getId()+"").equals(fileId))
                    {
                        file = sharedFile;
                        break;
                    }
                }
                
                String name = null,ip = null,port = null;
                if(file != null)
                {
                    name = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[0];
                    ip = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[1];
                    port = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[3].split(":")[2];
                    System.out.println("Getted isAccepted download from : "+name);
                    if(file.getOwnerNick().equals(FileChat.monitor.sv.sName) && file.getOwnerIp().equals(FileChat.monitor.sv.sIp))
                    {
                               
                        String fileAccept = requestMessage.split(SYSTEM_MESSAGE_BRACKET)[5];
                        System.out.println("is file accepted at server :"+fileAccept);
                        FileChat.monitor.acceptUpload(name,ip,port,fileId,fileAccept.equalsIgnoreCase("true"));

                    }else
                    {
                        for (ClientConnection cConn : FileChat.monitor.sv.clientList) {
                            if(cConn.cName.equals(file.getOwnerNick()) && cConn.cIp.equals(file.getOwnerIp()))
                            {
                                FileChat.monitor.sv.sendSystemMessage(USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED, cConn, requestMessage);
                                System.out.println("server send request message "+requestMessage);
                            }
                        }
                    }
                }                
                
                //-------
                //workUserFileRequests(USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED,requestMessage);
                break;
            }
            case USER_REQUEST_TYPE_NEW_SHAREDFILE://Sends system message to all about new shared file.
            {
                
                String[] splitedMsg = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
                String[] fileNameList = splitedMsg[11].split("<");
                File[] fileList = new File[fileNameList.length];
                System.out.println("clientConn newShared : "+requestMessage);
                for (int i = 0; i < fileNameList.length; i++) {
                    fileList[i] = new File(fileNameList[i]);
                }

                SharedFile shFile = new SharedFile(splitedMsg[3], splitedMsg[4], splitedMsg[5], splitedMsg[6], splitedMsg[7], splitedMsg[8], splitedMsg[9], splitedMsg[10], fileList);

                FileChat.monitor.fileSv.addNewSharedFile(shFile);
                FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_NEW_SHAREDFILE, requestMessage.substring(USER_REQUEST_NEW_SHAREDFILE.length()));
                System.out.println(this.cName+" yeni dosya ekledi : "+requestMessage);
                FileChat.monitor.refreshFileTable();
                FileChat.monitor.addNewMessage("(--"+shFile.getOwnerNick()+" shared "+shFile.getName()+"--)");
                if(FileChat.monitor.isAutomaticDownload)
                    FileChat.monitor.downloadSharedFile(shFile);
                break;
            }
            case USER_REQUEST_TYPE_REMOVE_SHAREDFILE:// Sends system message to all about removed shared file.
            {
                String[] splitedMsg = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
                System.out.println(this.cName+".cConn dosya silme talebi : "+requestMessage);
                FileChat.monitor.fileSv.deleteSharedFile(splitedMsg[3]);
                FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_REMOVE_SHAREDFILE, requestMessage.substring(USER_REQUEST_REMOVE_SHAREDFILE.length()));
                FileChat.monitor.refreshFileTable();
                break;
            }
            case USER_REQUEST_TYPE_EDIT_SHAREDFILE:// Sends system message to all about edited shared file.
            {
                System.out.println(this.cName+" cConn dosya düzenleme taleb :"+requestMessage);
                String[] splitedMsg = requestMessage.split(SYSTEM_MESSAGE_BRACKET);
                String id = splitedMsg[3];
                String[] fileNameList = splitedMsg[12].split("<");
                File[] fileList = new File[fileNameList.length];

                for (int i = 0; i < fileNameList.length; i++) {
                    fileList[i] = new File(fileNameList[i]);
                }

                SharedFile shFile = new SharedFile(splitedMsg[4], splitedMsg[5], splitedMsg[6], splitedMsg[7], splitedMsg[8], splitedMsg[9], splitedMsg[10], splitedMsg[11], fileList);
                FileChat.monitor.fileSv.editSharedFile(Integer.parseInt(id), shFile);
                FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_EDIT_SHAREDFILE, requestMessage.substring(USER_REQUEST_EDIT_SHAREDFILE.length()));
                System.out.println(this.cName+" dosya güncelledi : "+requestMessage);
                FileChat.monitor.refreshFileTable();
                FileChat.monitor.addNewMessage("(--"+shFile.getOwnerNick()+" edited "+shFile.getName()+"--)");
                break;
            }
            default: break;
        }
        
    }
    
    
}

