package FileShare;

import Chat.SharedFile;
import Chat.Tool;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JProgressBar;
import Chat.FileChatConstants;

/**
 * This class handles upload operations.
 * When this classes instance starts uploading it will upload all files on the request list then it will stop.
 * It uploads to 1 person in one time. When its finished it will upload next one.
 */
public class Uploader implements Observer
{
    ServerSocket uploadSocket; // Server socket for upload.
    ArrayList<String> uploadPersonList; // Request list. it contains downloaders name and shared file id.
    public String[] uploadArray; // Request array. It contains downloaders name and shared file id. This variable is using for synchronous lists.
    
    String ownerName,ownerIp,OwnerFilePort;
    String currentUploadInfo;
    public boolean isUploading;
    public boolean isUploadAccepted = false;
    JProgressBar prg_ALL = null;
    JProgressBar prg_CURR = null;
    long startTime = 0;
    
    Upload currUpload = null;
    /**
     * Creates serverSocket for upload and starts schedule for upload speed defination.
     * @param ownerName
     * @param ownerIp
     * @param ownerFilePort 
     */
    public Uploader(String ownerName,String ownerIp,String ownerFilePort)
    {
        
        this.OwnerFilePort = ownerFilePort;
        this.ownerIp = ownerIp;
        this.ownerName = ownerName;
        try {
            uploadSocket = new ServerSocket(Integer.parseInt(OwnerFilePort));
        }
        catch (Exception ex) {System.err.println("Exception on creating upload socet :"+ex.getMessage());}
        isUploading = false;
        this.uploadPersonList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        
        
                
        Timer zamanlayici = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if(uploadCalculator > 0)
                {
                    uploadSpeed = uploadCalculator;
                    uploadCalculator = 0;
                    
                }
            }
        };
        
        zamanlayici.schedule(task, 0, 250);
                  
    }
    
    
    /**
     * Adds new upload request to list.
     * @param shFile
     * @param downloaderName
     * @param downloaderIp
     * @param downloaderPort 
     */
    public void newUpload(SharedFile shFile,String downloaderName, String downloaderIp, String downloaderPort)
    {
        String uploadPersonString = getUploadPersonString(shFile, downloaderName, downloaderIp, downloaderPort);
        
        if(shFile != null)
        {
            uploadPersonList.add(uploadPersonString);
            synchronousUploadLists();
        }
    }
    
    /**
     * Starts the upload operation.
     * It will upload all files on queue.
     * @param senderName
     * @param senderIp
     * @param senderDownloadPort
     * @param shFile
     * @param prg_curr
     * @param prg_all 
     */
    public void startUpload(String senderName,String senderIp, String senderDownloadPort,SharedFile shFile,JProgressBar prg_curr,JProgressBar prg_all)
    {
        this.prg_ALL = prg_all;
        this.prg_CURR = prg_curr;
        currUpload = new Upload(uploadSocket, senderName, senderIp, senderDownloadPort, shFile);
        currUpload.addObserver(this);
        currUpload.uploadFile();
        removeUpload(currUpload);
        
        
        boolean isAllFinished = true;
        for (String str : uploadArray) {//If all files on array is uploaded it will be refreshed.
            if(str != null)
            {
                isAllFinished = false;
            }
        }

        if(isAllFinished)
        {
            uploadPersonList = new ArrayList<>();
        }
        
        synchronousUploadLists();
        System.gc();
    }
    
    /**
     * Removes upload request.
     * @param upload 
     */
    public void removeUpload(Upload upload )
    {
        
        String personStr = getUploadPersonString(upload.shFile, upload.downloaderName, upload.downloaderIp, upload.downloaderPort);
        
        int index = uploadPersonList.indexOf(personStr);
        
        if(index != -1)
        {
            uploadArray[index] = null;
            synchronousUploadLists();
        }
    }
    /**
     * Removes upload by index.
     * @param index 
     */
    public void removeUpload(int index)
    {
        
        if(index != -1)
        {
            uploadArray[index] = null;
            synchronousUploadLists();
        }
    }
    /**
     * Removes upload by id.
     * @param oldId 
     */
    public void removeUpload(String oldId)
    {
        try {
            if(Integer.parseInt(oldId) != -1 && !(uploadPersonList.size() <= 0) )
            {
                for (String string : uploadPersonList) {
                    if(string.startsWith(FileChatConstants.SYSTEM_MESSAGE_BRACKET+oldId))
                    {

                        uploadPersonList.remove(string);
                        synchronousUploadLists();
                        System.out.println("Upload removed : "+oldId);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Changes old shared file with new one.
     * It also changes the request lists.
     * @param oldFile
     * @param newFile 
     */
    public void editUpload(SharedFile oldFile,SharedFile newFile)
    {
        
        if(newFile != null && oldFile != null && !(uploadPersonList.size() <= 0))
        {
            for (String string : uploadPersonList) {
                if(string.startsWith(FileChatConstants.SYSTEM_MESSAGE_BRACKET+oldFile.getId()))
                {
                    uploadPersonList.set(uploadPersonList.indexOf(string), getUploadPersonString(newFile, ownerName, ownerIp, OwnerFilePort));
                    synchronousUploadLists();
                    System.out.println("Upload Edited : "+ oldFile.getName()+" to "+ newFile.getName());
                    break;
                }
            }
            
        }
    }
    
    /**
     * Synchronous the uploadList and uploadArray.
     */
    public void synchronousUploadLists()
    {
         if(uploadArray == null || uploadPersonList.size() == 0)//If uploadArray is not created (There is no upload requests before) it will be created. 
        {
            int length = uploadPersonList.size();
            uploadArray = new String[length];
            
        }else
        {//if is was created it will synchronous to uploadList.
           
           String[] newArray = new String[uploadPersonList.size()];
           
           
           for (int i = 0; i < uploadArray.length; i++) {
            if(!uploadPersonList.isEmpty())
            {
                if(uploadArray[i] == null)
                {
                    uploadPersonList.set(i,null);
                }
                newArray[i] = uploadArray[i];
            }
                
            }
           
            uploadArray = newArray;
        }
        
        
        
        
        Object[] objList = uploadPersonList.toArray();
            
        for(int i = 0; i < objList.length; i++)
        {
            uploadArray[i] = (String)objList[i];
            //System.out.println("upSenkronize :"+i+". : "+(uploadArray[i] == null ? "Null" :uploadArray[i]))  +" : "+(uploadList.get(i)== null ? "Null" :uploadList.get(i).getName()));
            System.out.println("upSenkronize : "+objList[i]+" : "+uploadArray[i]);
        }
    }
    
    double prg_All_Rate = 0,prg_curr_rate=0;
    long prg_All_value=0,prg_curr_value=0;
    long currUpSize = 0L;
    int uploadSpeed = 0,uploadCalculator = 0;
    @Override
    /**
     * 
     */
    public void update(Observable o, Object arg)
    {
        if(currUpload != null)
        {
            if(currUpload.instantReadSize > 0)//If reading file is successful.
            {
                uploadCalculator += currUpload.instantReadSize;
                
                if(currUpload.currFileSize != currUpSize) // If its next file
                {
                    uploadSpeed = 0;
                    prg_curr_rate = 0;
                    prg_curr_value = 0;
                    currUpSize = currUpload.currFileSize;

                    prg_CURR.setToolTipText(currUpload.myFile.getAbsolutePath());
                    System.gc();
                }
                
            prg_ALL.setValue((int) prg_All_Rate);
            prg_ALL.setString(Tool.byteToMb(prg_All_value) + " (mb) / "+Tool.byteToMb(Long.parseLong(currUpload.shFile.getFileSize()))+" (mb)          "
                                                                    + "To : "+currUpload.downloaderName
                                                                    +" [ "+currUpload.downloaderIp
                                                                    +":"+currUpload.downloaderPort+" ]");
            
            
            prg_CURR.setValue((int)prg_curr_rate);
            prg_CURR.setString(Tool.byteToMb(prg_curr_value)+" (mb) / "+Tool.byteToMb(currUpSize)+" (mb)\t\t\t  "+(uploadSpeed/1024)+" (kb) Uploaded.");
            
            
            
            double downRate = (double)currUpload.instantReadSize/(double)Long.parseLong(currUpload.shFile.getFileSize());
            prg_All_value+=currUpload.instantReadSize;
            prg_All_Rate += ((double)prg_ALL.getMaximum()*downRate);
            
            
            double curDownRate = (double)currUpload.instantReadSize/(double)currUpload.currFileSize;
            prg_curr_value+=currUpload.instantReadSize;
            prg_curr_rate += ((double)prg_CURR.getMaximum()*curDownRate);
            
            }else if(currUpload.instantReadSize == -2)// if read file is finished.
            {
                prg_All_Rate = 0;
                prg_curr_rate=0;
                prg_All_value=0;
                prg_curr_value=0;
                currUpSize = 0L;
                
                prg_ALL.setValue(0);
                prg_CURR.setValue(0);
                prg_CURR.setString("UPLOAD COMPLATE ! (Time :"+Tool.getTimeString(startTime, System.currentTimeMillis())+")");
            }
        }
    }
  
    
   
    public void close()
    {
        try {
            if(uploadSocket != null && !uploadSocket.isClosed())
                uploadSocket.close();
            uploadSocket = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    
    
    
    
    /**
     * It returns encrypted string for upload requester. 
     * @param shFile
     * @param downloaderName
     * @param downloaderIp
     * @param downloaderPort
     * @return 
     */
    public String getUploadPersonString(SharedFile shFile, String downloaderName, String downloaderIp,String downloaderPort)
    {
        String str = "@"+shFile.getId()+"@"+downloaderName+"@"+downloaderIp+"@"+downloaderPort;
        return str;
    }
    
    public String getUploadPersonSharedFileId(String personString)
    {
        String sharedFileId = personString.split("@")[1];
        return sharedFileId;
    }
    
    
    public String getUploadPersonDownloaderName(String personString)
    {
        String downloaderName = personString.split("@")[2];
        return downloaderName;
    }
    
    
    public String getUploadPersonDownloaderIp(String personString)
    {
        String downloaderIp = personString.split("@")[3];
        return downloaderIp;
    }
    
    public String getUploadPersonDownloaderPort(String personString)
    {
        String downloaderPort = personString.split("@")[4];
        return downloaderPort;
    }

    public void setownerName(String newName)
    {
        this.ownerName = newName;
    }
    
    
    
}