package Chat;

import java.io.File;


/*Constants for simple chat program.
*/
public interface FileChatConstants
{
    //---------------------------Default Settings-------------------------------
    public static int UPLOAD_SPEED_LIMIT = 1024*50; // Byte size of upload per time.
    public static int DOWNLOAD_SPEED_LIMIT = 1024*50; // Byte size of download per time.
    public static boolean OVERRIDE_DOWNLOADED_FILES = true; // If its true overrides downloaded same files. If its false its not.
    public static boolean AUTOMATIC_DOWNLOAD_FILES = false; // If its true automatic download shared files.
    public static final int USER_NAME_LENGTH_LIMIT = 30; // User name String lenght limit.
    //--------------------------------------------------------------------------
    
    
    //-----------------TOOL CONSTANTS-------------------------------------------
    public static final int SOCKET_IN_USE = 0;
    public static final int SOCKET_AVAILABLE = 1;
    public static final int SOCKET_NOT_AVAILABLE = -1;
    public static final int CHECK_WAIT_TIME = 10000;
    //--------------------------------------
    
    //--MESSAGE TYPE CONSTANTS
    public static final String SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST = "s1";
    public static final String SYSTEM_MESSAGE_TYPE_INFO = "s2";
    
    
    //--SYSTEM MESSAGE TYPE DEFINATIONS-----------------------------------------
    public static final String SYSTEM_NAME = "ROOT_SYSTEM01";
    public static final String SYSTEM_MESSAGE_BRACKET = "@";
    public static final String SYSTEM_MESSAGE = SYSTEM_MESSAGE_BRACKET + SYSTEM_NAME;
    public static final int SYSTEM_MESSAGE_TYPE_SEQUENCE = 2;
    
    //---SYSTEM MESSAGE CONSTANTS---------------------------------------------------------------
    public static final String SYSTEM_MESSAGE_REFRESH_USER_LIST = SYSTEM_MESSAGE // Refresh user list.
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String SYSTEM_MESSAGE_INFO = SYSTEM_MESSAGE // System info message tag.
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +SYSTEM_MESSAGE_TYPE_INFO
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String SYSTEM_MESSAGE_WELLCOME = SYSTEM_MESSAGE // Client connection check message.
                                                                +SYSTEM_MESSAGE_BRACKET
                                                                +"WELLCOME";
    
    //------------------------------------------------------------------------------------------
    
    
    
    
    
    
    
    //--USER REQUEST TYPE DEFINATIONS----------------------------------------------------------
    public static final String USER_REQUEST_TYPE_CHANGE_NAME = "u0";
    public static final String USER_REQUEST_TYPE_PRIVATE_MESSAGE = "u1";
    public static final String USER_REQUEST_TYPE_DISCONNECT = "u2";
    
    public static final String USER_REQUEST_TYPE_UPLOAD = "u3";
    public static final String USER_REQUEST_TYPE_DOWNLOAD = "u4";
    public static final String USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED = "u5";
    
    public static final String USER_REQUEST_TYPE_NEW_SHAREDFILE = "u6";
    public static final String USER_REQUEST_TYPE_REMOVE_SHAREDFILE = "u7";
    public static final String USER_REQUEST_TYPE_EDIT_SHAREDFILE = "u8";
    public static final String USER_REQUEST_TYPE_SHARED_FILE_LIST = "u9";
    
    
    //----USER REQUEST CONSTANTS----------------------------------------------------------------
    public static final String USER_REQUEST_CHANGE_NAME = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_CHANGE_NAME
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_PRIVATE_MESSAGE =   SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_PRIVATE_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_DISCONNECT =   SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_DISCONNECT
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_SHARED_FILE_LIST = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_SHARED_FILE_LIST
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    //------
    public static final String USER_REQUEST_UPLOAD =   SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_UPLOAD
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_DOWNLOAD =   SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_DOWNLOAD
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_DOWNLOAD_ISACCEPTED = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    //------
    public static final String USER_REQUEST_NEW_SHAREDFILE = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_NEW_SHAREDFILE
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_REMOVE_SHAREDFILE = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_REMOVE_SHAREDFILE
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    public static final String USER_REQUEST_EDIT_SHAREDFILE = SYSTEM_MESSAGE
                                                                 +SYSTEM_MESSAGE_BRACKET
                                                                 +USER_REQUEST_TYPE_EDIT_SHAREDFILE
                                                                 +SYSTEM_MESSAGE_BRACKET;
    
    
    
    
    //---------------------------------------------------------------------------------------------------
    //FİLE DEFINATION CONSTANTS
    
    public static final String NEXT_FILE = "165NEXT165"; // To define one files transfer stream is finished and next one is starting.
    public static final char SYSTEM_SEPERATOR = File.separatorChar; //System file seperator char.
    
    
    
}
 
 
 