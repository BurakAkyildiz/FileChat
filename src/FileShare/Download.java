package FileShare;

import Chat.SharedFile;
import Chat.FileChat;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Chat.FileChatConstants;


/**
 *Downloads the given shared file with given values.
 */
public class Download extends java.util.Observable implements FileChatConstants
{
    
    String ownerName,ownerIp,ownerDownloadPort;
    String uploaderName,uploaderIp,uploaderPort;
    Socket downloaderSocket;
    SharedFile shFile;
    
    byte[] tmp = new byte[FileChat.monitor.getDownloadSpeedLimit()]; // Temprory for download operation.
    FileOutputStream output;
    DataInputStream scan;
    File myFile = null; // Current download file.
    String myFileParentPath = ""; // Parent download folder for this download.
    
    long readedSize_All = 0L;
    long currFileSize = 0L;
    int instantReadSize = 0;
    boolean isAvailableDownload = true; // if download is aborded it will be false.
    
    //Creates socket and tests it.
    public Download(String ownerName,String ownerIp, String ownerDownloadPort,SharedFile shFile)
    {
        this.uploaderIp = ownerIp;
        this.uploaderName = ownerName;
        this.uploaderPort = ownerDownloadPort;
        this.shFile = shFile;
        myFileParentPath = DownloadPropertiyTools.getNewParentDownloadFolderPath(shFile.getName());
        
        
        try {
            System.out.println("Downloader connecting... Owner name : "+ownerName+" Owner ip : "+uploaderIp+" port : "+uploaderPort);
            downloaderSocket = new Socket(uploaderIp, Integer.parseInt(uploaderPort));
            downloaderSocket.close();
            
            
        }
        catch (Exception e) {
            System.err.println("Exception on download : " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    //Downloads all shared files to defined download folder.
    public boolean downloadFile()
    {
        ArrayList<File> files = shFile.getFileList();
        System.out.println("download list :"+files.size()+" files. paths : "+shFile.getFileList().toString());
        myFile = null;
        
        for (File file : files) {
            boolean isFileDownloaded = false;
            try 
            {
                if(!isAvailableDownload)
                    break;
                
                downloaderSocket = new Socket(uploaderIp, Integer.parseInt(uploaderPort));
                
                scan = new DataInputStream(new BufferedInputStream(downloaderSocket.getInputStream()));
                
                myFile = DownloadPropertiyTools.getNewPath(file, myFileParentPath, shFile.getParentDirectoryPath());
                
                /*System.out.println("download folder : "+FileChat.monitor.downloadFolder.trim()+"\n"
                                   +"shFile name :"+shFile.getName().trim()+"\n"
                                    + "file path :"+file.toPath().toString()+"\n"
                                   + "shFile parentDirectoryPath : "+shFile.getParentDirectoryPath()+"\n"
                                    +"myfileParent Path : "+myFileParentPath );*/
                //System.out.println(" Myfile "+myFile.isFile()+" path : "+myFile.toPath()+"\n");
                
                
                
                
                if(!myFile.getParentFile().canRead()) // creates download files directories.
                {
                    myFile.getParentFile().mkdirs();
                    System.out.println("mkDir for download : "+myFile.getParentFile());
                }

                
                
                if(!FileChat.monitor.isOverRideFiles()&& myFile.canRead()) // checks if override file is open or not. If its opened deletes existing one and downloads it again. If its flase aborts current files download goes next.
                {
                    System.out.println("Existing file detected :"+myFile.getAbsolutePath());
                    isFileDownloaded = true;
                    if(scan != null)
                        scan.close();
                }else if(myFile.isFile())
                {
                    myFile.delete();
                    myFile.createNewFile();
                }
                    
                    
                    
                
                
                output = new FileOutputStream(myFile,true);
                
                
                readedSize_All = 0L;
                instantReadSize = 0;
                tmp = new byte[FileChat.monitor.downloadSpeedLimit];
                
                
                currFileSize = 0L;
                currFileSize = scan.readLong(); // reads current file size.
                System.out.println("File : "+file+" size : "+currFileSize);
                
                
                while((instantReadSize = scan.read(tmp)) != -1) // Downloads the current file.
                {
                    //System.out.println("Anlık download : "+instantReadSize+" / "+readedSize_All);
                    
                    output.write(tmp, 0, instantReadSize);
                    readedSize_All += instantReadSize;
                    
                    if(Long.compare(currFileSize, readedSize_All) == 0) // If readedSize is equal to Current files size this file is downloaded and goes next.
                    {
                        System.out.println("next file.");
                            output.flush();
                            output.close();
                            setChanged(); // sends information to downloadViewer.
                            this.notifyObservers();
                            break;
                    }
                    
                    setChanged(); // sends information to downloadviewer
                    this.notifyObservers();
                    
                }
                System.out.println("Downloaded "+readedSize_All+" bytes to : "+myFile.getAbsolutePath());
                    isFileDownloaded = true;
                    
                    if(output != null)
                        output.close();
                    
                    if(downloaderSocket != null)
                        downloaderSocket.close();
                    
                    if(scan != null)
                        scan.close();
                    
                }catch (Exception e) {
                    instantReadSize = 0;
                    System.err.println("Exception on writing download : "+e.getMessage());
                    e.printStackTrace();
                    setChanged();
                    this.notifyObservers();
                } finally
                {
                    try {
                        if(output != null)
                            output.close();
                        
                        if(!isFileDownloaded) // If current download is not done file is not downloaded at all and it will be currupted. Because of that it will be deleted.
                        {
                            if(myFile.delete())
                            {
                               System.out.println("Damaged file is deleted.");
                            }    
                        }
                        
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
            
        }
        
        
       return close();
    }
     
    /**
     * Aborts or closes the download.
     * @return 
     */
    public boolean close()
    {
        try {
           // File myFile = new File(""+FileChat.Monitor.downloadFolder.trim() +Paths.get(shFile.getParentDirectoryPath()).getFileName().toString().trim());
            
            
            isAvailableDownload = false;
            
            if(output != null)
                output.close();
            
            if(scan != null)
                scan.close();
              
            if(downloaderSocket != null && downloaderSocket.isConnected())
                downloaderSocket.close();
            
            instantReadSize = -2;
            currFileSize = -2;
            setChanged();
            this.notifyObservers();
            return true;
        }
        catch (IOException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
     
}
