package FileShare;

import Chat.SharedFile;
import Chat.FileChat;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *Uploads given file to given user.
 * @author Burak
 */
public class Upload extends Observable 
{
    
    String downloaderName,downloaderIp,downloaderPort;
    ServerSocket uploaderSocket;
    Socket downloaderSocket;
    SharedFile shFile;
    
    int instantReadSize = 0, readedFileSize_All = 0;
    long currFileSize = 0;
    File myFile = null;
    byte[] tmp = new byte[FileChat.monitor.uploadSpeedLimit]; // Temprory for file io operation.
    FileInputStream input;
    DataOutputStream outSize;
    public Upload(ServerSocket uploaderSocket, String downloaderName, String downloaderIp, String downloaderPort, SharedFile shFile)
    {
       
        this.downloaderName = downloaderName;
        this.downloaderIp = downloaderIp;
        this.downloaderPort = downloaderPort;
        this.shFile = shFile;
        
        
        try {
            this.uploaderSocket = uploaderSocket; // gets the upload socket.
            this.uploaderSocket.setSoTimeout(7000);
            
            System.out.println("waiting for downloader... port : "+uploaderSocket.getLocalPort());
            downloaderSocket = uploaderSocket.accept(); // waits the downloader for 7 second to start upload. If its not connected upload will be aborded by uploader instance.
            
            System.out.println("Downloader accepted. "+downloaderSocket.getInetAddress());
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Starts the upload of shFile.
     */
    public void uploadFile()
    {
         ArrayList<File> files = shFile.getFileList();
        
        int exceptionCounter = 0; // Limit of the exception in 1 time. It will refreshed with per upload.
        for (File file : files) { // uploads shared fileList one by one. Sends nextFile message when current one done.
            try 
            {
                
                if(exceptionCounter >= 5) // If limit is exceeded the upload operation will be finished.
                {
                    System.err.println("MAXIMUM ERROR DETECTED. CANCELING UPLOAD !!");
                    System.out.println("Exception counter :"+exceptionCounter);
                    break;
                }
                   
                
                downloaderSocket = uploaderSocket.accept();
                exceptionCounter = 0;
                
                outSize = new DataOutputStream(downloaderSocket.getOutputStream());
                System.out.println("Upload Info : "+"file size : "+file.length()+" isFile = "+file.isFile()+" isreadable = "+file.canRead()+" : " + file.getAbsolutePath());
                
                input = new FileInputStream(file);
                currFileSize = file.length();
                myFile = file;
                instantReadSize = 0;
                readedFileSize_All = 0;
                
                
                outSize.writeLong(file.length()); // sends current files size to downloader.
                outSize.flush();
                tmp = new byte[FileChat.monitor.uploadSpeedLimit];
                
                while((instantReadSize = input.read(tmp)) != -1) // Reads the file and uploads it.
                {
                    outSize.write(tmp, 0, instantReadSize);
                    readedFileSize_All += instantReadSize;
                    //System.out.println("Anlık Upload : "+instantReadSize+" / "+readedFileSize_All);
                    outSize.flush();
                    setChanged(); // notify observers for dynamic information.
                    notifyObservers();
                }
                
                
                
                
                System.out.println("\tUploaded "+readedFileSize_All+" bytes from :"+file.getAbsolutePath());
                
                if (input != null) {
                    input.close();
                }
                if (outSize != null) {
                    outSize.close();
                }
                if(downloaderSocket != null)
                    downloaderSocket.close();
                
            }
            catch(SocketTimeoutException e)
            {
                exceptionCounter +=3;
                System.out.println("Exception counter :"+exceptionCounter);
                e.printStackTrace();
            }
            catch(SocketException e)
            {
                exceptionCounter+=2;
                System.out.println("Exception counter :"+exceptionCounter+" "+e.getMessage());
                e.printStackTrace();
                    
            }
            catch (Exception e) {
                System.err.println("Exception on writing upload : "+e.getMessage());
                e.printStackTrace();
                try {
                    outSize.write("-1".getBytes());//If current file curropted, can not readable , not avaible to send or canceled it will notify downloader to go next file.
                    outSize.flush();
                    if (input != null) {
                    input.close();
                    }
                    if (outSize != null) {
                        outSize.close();
                    }
                    if(downloaderSocket != null)
                        downloaderSocket.close();
                }
                catch (IOException ex) {
                    exceptionCounter++;
                    System.out.println("Exception counter :"+exceptionCounter);
                    Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        close();
    }
   
    /**
     * Closes the upload operation successfully.
     */
    public void close()
    {
        currFileSize = -2;
        instantReadSize = -2;
        setChanged();
        notifyObservers();
    }
}