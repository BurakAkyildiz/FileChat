package FileShare;

import Chat.SharedFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class controls the download operations and queue.
 * @author Burak
 */
public class Downloader
{
    ArrayList<SharedFile> downloadList;
    ArrayList<Download> downloadTracker;
    public SharedFile[] downloadArray;
    
    String ownerName,ownerIp,OwnerFilePort;

    public Downloader(String ownerName, String ownerIp, String OwnerFilePort)
    {
        this.downloadList = new ArrayList<SharedFile>();
        this.downloadTracker = new ArrayList<>();
        this.ownerName = ownerName;
        this.ownerIp = ownerIp;
        this.OwnerFilePort = OwnerFilePort;
    }
    
    /**
     * Adds a new download.
     * @param dFile 
     */
    public void newDownload(SharedFile dFile)
    {
        if(dFile != null && !downloadList.contains(dFile))
        {
            downloadList.add(dFile);
            synchronousDlList();
        }
    }
    
    
    //Defines the shared file still wanted to download.
    public boolean downloadAcceptted(SharedFile dFile)
    {
        
        if(dFile != null)
        {
            
            for (int i =0; i < downloadArray.length; i++) {
                if( downloadArray[i] != null && downloadArray[i].equals(dFile))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    //Starts download as asynchronous.
    public void startDownload(SharedFile dFile,DownloadViewer dlView)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Download dl = null;
                SharedFile downloadFile = dFile;
                for (int i = 0; i < 5; i++) {//Tries to connect 5 times. Waits 0.75 time per try.
                    try {
                        dl = new Download(dFile.getOwnerNick(), dFile.getOwnerIp(), dFile.getOwnerPort(), downloadFile);
                        boolean isDownloadWorked = false;
                        
                        if(dl.downloaderSocket.isConnected())
                        {
                            dlView.setDownload(dl);
                            downloadTracker.add(dl);
                            isDownloadWorked = dl.downloadFile();// Starts the download.
                                if(isDownloadWorked)
                                {
                                    break;
                                }
                        }else
                        {
                            Thread.sleep(1000L);
                        }
                        
                    }
                    catch (Exception ex) {ex.printStackTrace(); try {
                        Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex1) {
                            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    
                }
                
                removeDownload(downloadFile);//removes the downlaod from list when its job is done.
                boolean isAllFinished = true;
                if(downloadArray != null)
                for (SharedFile sharedFile : downloadArray) {//If all files on list is downloaded list will be refreshed.
                    if(sharedFile != null)
                    {
                        isAllFinished = false;
                    }
                }
                
                if(isAllFinished)
                {
                    downloadList = new ArrayList<>();
                    synchronousDlList();
                }
                System.gc();
            }
        }).start();
    }
    
    
    //Cancels the download request of rFile.
    public void removeDownload(SharedFile rFile)
    {
        if(rFile != null && !(downloadList.size() <= 0))
        {
            System.out.println("removing : "+rFile.getName());
            for (int i = 0; i<downloadArray.length; i++) {
                if(downloadArray[i] != null && downloadArray[i].equals(rFile))
                {
                    downloadArray[i] = null;
                }
            }
            synchronousDlList();
        }
    }
    
    
    //Changes the old shared file with new one.
    public void editDownload(SharedFile oldFile,SharedFile newFile)
    {
        if(newFile != null && oldFile != null && !(downloadList.size() <= 0))
        {
            System.out.println("Editing download : "+ oldFile.getId()+" to "+ newFile.getId());
            downloadList.set(downloadList.indexOf(oldFile), newFile);
            synchronousDlList();
        }
    }
    
    //synchronous the downloadList and downloadArray.
    //If any of lis or array has a null item they will both have null item on that index.
    //If there is a new download on downloadList the downloadArray will be recreated for that size.
    private void synchronousDlList()
    {
        
        if(downloadArray == null)//If downloadArray is not created (There is no download requests before) it will be created. 
        {
            int length = downloadList.size();
            downloadArray = new SharedFile[length];
            
            
        }else
        {//if is was created it will synchronous to downloadList.
           
           SharedFile[] newArray = new SharedFile[downloadList.size()];
           
           
           for (int i = 0; i < downloadArray.length; i++) {
            
               if(!downloadList.isEmpty())
               {
                   if(downloadArray[i] == null )
                    {
                        downloadList.set(i, null);
                    }
                    newArray[i] = downloadArray[i];
               }
                
            }
           
            downloadArray = newArray;
        }
        
        
        
        
        Object[] objList = downloadList.toArray();
            
        for(int i = 0; i < objList.length; i++)
        {
            downloadArray[i] = (SharedFile)objList[i];
            System.out.println("Download synchronous :"+i+". : "+(downloadArray[i] == null ? "Null" :downloadArray[i].getName())  +" : "+(downloadList.get(i)== null ? "Null" :downloadList.get(i).getName()));
        }
        
        
        
    }
    
    
    
    public void setownerName(String newName)
    {
        this.ownerName = newName;
    }
    
    
    /**
     * Cloeses the downloader and all download operations.
     */
    public void close()
    {
        try {
            for (Download download : downloadTracker) {
                if(download != null)
                {
                    if(!download.downloaderSocket.isClosed())
                        download.downloaderSocket.close();
                    
                    download.close();
                    download.downloaderSocket = null;
                    
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().toLowerCase().contains("modification exception"))
            {
                close();
            }
        }
    }
}