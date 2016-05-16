package FileShare;

import Chat.SharedFile;
import java.util.ArrayList;

/**
 * This class operates the requested downloads and uploads at the same time.
 * It uses upload port for file operations.
 * @author Burak
 */
public class FileServer
{   
    private ArrayList<SharedFile> sharedFileList; // All shared files list.
    public ArrayList<SharedFile> mySharedFiles; // Self shared files list.
    public Uploader uploader;
    public Downloader downloader;
    private String fileServerIp; //File server informations for upload and download.
    private String fileServerSendPort;
    private String fileServerOwner;
    
    
    
    public FileServer(String fileServerOwner,String fileServerIp, String fileServerSendPort)
    {
        this.sharedFileList = new ArrayList<>();
        this.uploader = new Uploader(fileServerOwner,fileServerIp,fileServerSendPort);
        this.downloader = new Downloader(fileServerOwner,fileServerIp,fileServerSendPort);
        this.fileServerIp = fileServerIp;
        this.fileServerSendPort = ""+fileServerSendPort;
        this.fileServerOwner = fileServerOwner;
        this.mySharedFiles = new ArrayList<>();
    }
    /**
     * Creates new CreateNewSharedFile instance with own user informations.
     * @return 
     */
    public CreateNewSharedFile createAddNewSharedFile()
    {
        CreateNewSharedFile fileCreater = new CreateNewSharedFile(
                                        this,
                                        fileServerOwner,
                                        fileServerIp,
                                        fileServerSendPort);
        fileCreater.setVisible(true);
        return fileCreater;
    }
    
    /**
     * Adds new shared file to the list.
     * @param newSharedFile 
     */
    public void addNewSharedFile(SharedFile newSharedFile)
    {
        sharedFileList.add(0,newSharedFile);
    }
    
    /**
     * Creates new CreateEditSharedFile instance with own user informations.
     * @param oldSharedFile
     * @return 
     */
    public CreateEditSharedFile createEditSharedFile(SharedFile oldSharedFile)
    {
        CreateEditSharedFile fileEdit = new CreateEditSharedFile(
                                        this,
                                        oldSharedFile);
        
        fileEdit.setVisible(true);
        return fileEdit;
    }
    
    /**
     * Replaces the old shared file with new one.
     * @param oldId
     * @param newSharedFile 
     */
    public void editSharedFile(int oldId,SharedFile newSharedFile)
    {
        downloader.editDownload(getSharedFile(oldId), newSharedFile);
        uploader.editUpload(getSharedFile(oldId), newSharedFile);
        sharedFileList.set(sharedFileList.indexOf(getSharedFile(oldId)), newSharedFile);
    }
    
    /**
     * Changes shared files owner name.
     * @param oldName
     * @param newName 
     */
    public void editSharedFile(String oldName,String newName)
    {
        try {
            for (int i = 0; i < sharedFileList.size(); i++) {
                if(sharedFileList.get(i).getOwnerNick().equals(oldName))
                {
                    SharedFile newFile = sharedFileList.get(i);
                    newFile.setOwnerNick(newName);
                    System.out.println("FileSv edited file :"+sharedFileList.get(i).getId()+" to : "+newFile);
                    editSharedFile(sharedFileList.get(i).getId(),newFile);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().contains("ConcurrentModificationException"))
            {
                editSharedFile(oldName,newName);
            }
        }
    }
    
    /**
     * Removes shared file from list.
     * If its own file it will be removed from mySharedFiles list.
     * @param oldId 
     */
    public void deleteSharedFile(String oldId)
    {
        try {
            SharedFile delFile = getSharedFile(Integer.parseInt(oldId));
            if(delFile != null)
            {
                downloader.removeDownload(getSharedFile(Integer.parseInt(oldId)));
                uploader.removeUpload(oldId);
                mySharedFiles.remove(delFile);
                sharedFileList.remove(delFile);
            }
        }
        catch (Exception e) {
            System.err.println("Exception on deleting shared file : "+e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    /**
     * Removes all shared files of given user.
     * It removes shared files checking owner name (it is unique) and ip-port combination.
     * @param name
     * @param ip
     * @param port 
     */
    public void removeUsersFiles(String name, String ip, String port)
    {
        try {
            for (int i = 0; i < sharedFileList.size(); i++) {
                if(sharedFileList.get(i).getOwnerNick().equals(name) || (sharedFileList.get(i).getOwnerIp().equals(ip) && sharedFileList.get(i).getOwnerPort().equals(port)))
                {
                    System.out.println("Deleting shared file : "+sharedFileList.get(i).toString());
                    deleteSharedFile(""+sharedFileList.get(i).getId());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().contains("ConcurrentModificationException"))
            {
                removeUsersFiles(name, ip, port);
            }
        }
        
    }
    
    /**
     * Closes fileServer.
     * All downloads and uploads will be canceled.
     */
    public void close()
    {
        if(downloader != null)
        {
            downloader.close();
        }
        if(uploader != null)
        {
            uploader.close();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //----------------------GETTER & SETTER-------------------------------------
    //--------------------------------------------------------------------------
    
    public SharedFile getSharedFile(int id)
    {
        
        for (SharedFile sharedFile : sharedFileList) {
            if(sharedFile.getId() == id)
                return sharedFile;
        }
        
        return null;
    }
    
    
    public ArrayList<SharedFile> getSharedFileList()
    {
        return sharedFileList;
    }

    public void setSharedFileList(ArrayList<SharedFile> sharedFileList)
    {
        this.sharedFileList = sharedFileList;
    }

    public Uploader getFileSender()
    {
        return uploader;
    }

    public void setFileSender(Uploader uploader)
    {
        this.uploader = uploader;
    }

    public Downloader getFileReader()
    {
        return downloader;
    }

    public void setFileReader(Downloader downloader)
    {
        this.downloader = downloader;
    }

    public String getFileServerIp()
    {
        return fileServerIp;
    }

    public void setFileServerIp(String fileServerIp)
    {
        this.fileServerIp = fileServerIp;
    }

    public String getFileServerSendPort()
    {
        return fileServerSendPort;
    }

    public void setFileServerSendPort(String fileServerSendPort)
    {
        this.fileServerSendPort = fileServerSendPort;
    }

    public String getFileServerOwner()
    {
        return fileServerOwner;
    }

    public void setFileServerOwner(String fileServerOwner)
    {
        this.fileServerOwner = fileServerOwner;
        this.uploader.setownerName(fileServerOwner);
        this.downloader.setownerName(fileServerOwner);
    }
    
    
    
   
    
}
//CLOSE FILE SERVER EKLENECEK VE CONNECT / CREATE SERVER CLIENT DURUMUNDA KULLANILACAK
//ShareFile history ile download ve upload tracker durumları izlenebilecek.