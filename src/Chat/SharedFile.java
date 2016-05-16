package Chat;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;



/**
 * This class contains information about share and does conversations about sharedFile instance.
 */
public class SharedFile implements Serializable
{
    public static final long serialVersionUID = 1824982736L;
    
    
    private int id; // Shared File unique id
    private String name; // Share name
    private String parentDirectoryPath; // Shared Files parent folder on file owner.
    private String notification; // Share notes
    private String fileSize; // Shared files total size in byte.
    private String ownerNick; // Shared files owner name
    private String ownerIp;   //                    Ip
    private String ownerPort; //                    port.
    private String shareTime; // Share time.
    private ArrayList<File> fileList; // Shared file list. ( just contains files not directories.)
    

    
    /**
     * Creates new Shared File instance.
     * @param name
     * @param parentDirectoryPath
     * @param notification
     * @param fileSize
     * @param ownerNick
     * @param ownerIP
     * @param ownerPort
     * @param shareTime
     * @param fileList 
     */
    public SharedFile(String name, String parentDirectoryPath, String notification, String fileSize, String ownerNick, String ownerIP, String ownerPort, String shareTime, File[] fileList)
    {
        this.name = name;
        this.parentDirectoryPath = parentDirectoryPath;
        this.notification = notification;
        this.fileSize = fileSize;
        this.ownerNick = ownerNick;
        this.ownerIp = ownerIP;
        this.ownerPort = ownerPort;
        this.shareTime = shareTime;
        this.fileList = new ArrayList<>();
        for (File file : fileList) {
            this.fileList.add(file);
        }
        
        this.id = hashCode();
    }
    
    /**
     * Encryption of shared file to String.
     * This is using for sending shared files informations to other users.
     * @param sh
     * @return 
     */
    public static String toMessageString(SharedFile sh)
    {
        //"name@parentDirectoryPath@notification@fileSize@ownerNick@ownerIP@ownerPort@shareTime@path<path<path"
        StringBuilder strBuild = new StringBuilder();
        strBuild.append( sh.getName()+"@"+sh.getParentDirectoryPath()+"@"+sh.getNotification()+"@"+sh.getFileSize()+"@"+sh.getOwnerNick()+"@"+sh.getOwnerIp()+"@"+sh.getOwnerPort()+"@"+sh.getShareTime()+"@");
        ArrayList<File> files = sh.getFileList();

        for (File file : files) {
            strBuild.append( file.getPath()+"<");
        }
        return strBuild.toString();
    }

    @Override
    public String toString()
    {
        return  "Shared File Name : "+name
                +" @Owner : "+ownerNick
                +" @Owner Ip :"+ownerIp
                +" @Owner Port :" + ownerPort
                +" @Parent Folder :"+parentDirectoryPath
                +" @Notification :"+notification
                +" @File Size :" + fileSize
                +" @Share Time :"+ shareTime
                +" @File Count :"+fileList.size();
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.parentDirectoryPath);
        hash = 89 * hash + Objects.hashCode(this.notification);
        hash = 89 * hash + Objects.hashCode(this.fileSize);
        hash = 89 * hash + Objects.hashCode(this.ownerNick);
        hash = 89 * hash + Objects.hashCode(this.ownerIp);
        hash = 89 * hash + Objects.hashCode(this.ownerPort);
        hash = 89 * hash + Objects.hashCode(this.shareTime);
        hash = 89 * hash + Objects.hashCode(this.fileList);
        return hash;
    }
    

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof SharedFile)
        {
            if (this == obj) {
            return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SharedFile other = (SharedFile) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.parentDirectoryPath, other.parentDirectoryPath)) {
                return false;
            }
            if (!Objects.equals(this.notification, other.notification)) {
                return false;
            }
            if (!Objects.equals(this.fileSize, other.fileSize)) {
                return false;
            }
            if (!Objects.equals(this.ownerNick, other.ownerNick)) {
                return false;
            }
            if (!Objects.equals(this.ownerIp, other.ownerIp)) {
                return false;
            }
            if (!Objects.equals(this.ownerPort, other.ownerPort)) {
                return false;
            }
            if (!Objects.equals(this.shareTime, other.shareTime)) {
                return false;
            }
            if (!Objects.equals(this.fileList, other.fileList)) {
                return false;
            }
            return true;
        }
        return false;
    }

    
    /**
     * Converts this Shared file to Objects.
     * It is using for inserting the shared file to table.
     * @return 
     */
    public Object[] toTableRow()
    {
        Object[] rowObject = new Object[]{
                                     name
                                    ,notification
                                    ,fileSize
                                    ,ownerNick+" "+ownerIp+":"+ownerPort
                                    ,shareTime
                                    ,Arrays.toString(fileList.toArray())
                                    };
    
        return rowObject;
    }
    
    
    
    //--------------------------GETTER & SETTER---------------------------------
    //--------------------------------------------------------------------------
    
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParentDirectoryPath()
    {
        return parentDirectoryPath;
    }

    public void setParentDirectoryPath(String parentDirectoryPath)
    {
        this.parentDirectoryPath = parentDirectoryPath;
    }

    public String getNotification()
    {
        return notification;
    }

    public void setNotification(String notification)
    {
        this.notification = notification;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getOwnerNick()
    {
        return ownerNick;
    }

    public void setOwnerNick(String ownerNick)
    {
        this.ownerNick = ownerNick;
    }

    public String getOwnerIp()
    {
        return ownerIp;
    }

    public void setOwnerIp(String ownerIp)
    {
        this.ownerIp = ownerIp;
    }

    public String getOwnerPort()
    {
        return ownerPort;
    }

    public void setOwnerPort(String ownerPort)
    {
        this.ownerPort = ownerPort;
    }

    public String getShareTime()
    {
        return shareTime;
    }

    public ArrayList<File> getFileList()
    {
        return fileList;
    }

    public void setFileList(ArrayList<File> fileList)
    {
        this.fileList = fileList;
    }

    
    public int getId()
    {
        this.id = hashCode();
        return id;
    }
    
    
    
}
