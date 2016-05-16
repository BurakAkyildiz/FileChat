package Chat;

/**
* Starts the app.
* @author Burak
*/
public class FileChat
{
    public static Monitor monitor;
    public static void main(String[] args)    
    {
        if(FileChat.monitor == null)
        {
            FileChat.monitor = new Monitor();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                FileChat.monitor.setVisible(true);
            }
        });
    }
}