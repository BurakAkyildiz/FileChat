package FileShare;

import static Chat.FileChatConstants.SYSTEM_SEPERATOR;
import java.io.File;

/**
 *This class contains tool methods just for download operations.
 * @author Burak
 */
public class DownloadPropertiyTools {
    
    
    /**
     * Clears file name.
     * The systems marked unavailable characters are  <![CDATA[('.', '\', '/', ':', '?', '*', '<', '>', '|')]]>
     * They will replace to '_' character.
     * @return 
     */
    public static String clearFileName(String name)
    {
        
        StringBuilder strBuild = new StringBuilder();
        char[] nameC = name.toCharArray();
        
        for (char c : nameC) {
            if(c == '.' || c == '\\'
                            || c == '/' 
                            || c == ':' 
                            || c == '?' 
                            || c == '*' 
                            || c == '>' 
                            || c == '<' 
                            || c == '|'  )
            {
               strBuild.append('_');
            }else
            {
                strBuild.append(c);
            }
        }
        
        return strBuild.reverse().toString();
        
    }
    
    
    /**
     * Converts the shared file to new shared file with changing its path.
     * Path will change with using defined downloadFolder and old parent folder.
     * @param sharedFile
     * @param downloadParentFolder
     * @param sharedFileParentFolder
     * @return new shared file with changed path.
     */
    public static File getNewPath(File sharedFile, String downloadParentFolder, String sharedFileParentFolder)
    {
        File myFile = null;
        
        if(sharedFile.getAbsolutePath().toString().startsWith(sharedFileParentFolder))
        {
            myFile = new File(""+downloadParentFolder+sharedFile.toPath().toString().substring(sharedFileParentFolder.length()).trim());

        }else
        {
            myFile = new File(""+downloadParentFolder+sharedFile.getName());

        }
        
        return myFile;
    }
    
    /**
     * Returns new Parent folder for current sharedFile.
     * @param sharedFileName
     * @return 
     */
    public static String getNewParentDownloadFolderPath(String sharedFileName)
    {
        return Chat.Monitor.downloadFolder.trim()
                                        +SYSTEM_SEPERATOR+clearFileName(sharedFileName.trim())
                                        +SYSTEM_SEPERATOR;
    }
}
