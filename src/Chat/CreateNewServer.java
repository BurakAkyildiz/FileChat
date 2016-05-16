package Chat;

import FileShare.FileServer;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * This class creates new server with using GUI.
 * There is 2 type of server Local and Remote. 
 * If user wants to open local server he must define 2 free port for servers chat and file connection.
 * If user wants to open remote server there must be opened 2 port on router to remote connection and these ports must be redirected to servers own local ip.
 * @author Burak
 */
public class CreateNewServer extends javax.swing.JFrame
{
    public static final int LOCAL_SERVER = 0;
    public static final int REMOTE_SERVER = 1;
    public boolean isServerCreated = false;
    public Container root = null;
    
    
    /**
     * Creates new form CreateNewServer
     */
    public CreateNewServer(Container root)
    {
        initComponents();
        
        Tool.savedLocalIP = null;
        Tool.savedRemoteIP = null;
        this.root = root;
        ShowWaitAction setup = new ShowWaitAction("Wait action", root, "-Checking IP-", "Serching..", new Runnable() {
            @Override
            public void run() {

                txt_ServerName.setDocument(Tool.getJTextFieldLimit(FileChatConstants.USER_NAME_LENGTH_LIMIT));
                CreateNewServer.this.checkConnectionType();

                btn_createServer.addActionListener(
                        new ShowWaitAction("Wait Action", CreateNewServer.this,"Checking Your Port","Testing your router...",new Runnable() // Shows wait dialog on check.
                        {
                            @Override
                            public void run()
                            {
                                if(checkCreateServerState()) // checks the Server ports , server name and connection type. Tries to create server.
                                {
                                    String name = txt_ServerName.getText().replaceAll(" ", "_");
                                    if(name.isEmpty())
                                        name = "Server";
                                    int serverFilePort = Integer.parseInt(txt_ServerFilePort.getText().isEmpty() ? "0" : txt_ServerFilePort.getText());
                                    int serverPort = Integer.parseInt(txt_ServerPort.getText().isEmpty() ? "0" : txt_ServerPort.getText());
                                    isServerCreated = createServer(name, serverPort, serverFilePort);
                                }

                            }
                        }));

                cmb_ConnectionType.setSelectedIndex(FileChat.monitor.isRemoteConnection ? 1 : 0);

            }
        });
        
        setup.actionPerformed(null);
        
        try {
            System.out.println("Check ip  local:"+Tool.savedLocalIP+" remote:"+Tool.savedRemoteIP);
            if(!(Tool.savedLocalIP != null || Tool.savedRemoteIP != null))
            {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(root, "You do not have any network connection !","Connection Problem !",JOptionPane.WARNING_MESSAGE);
                        CreateNewServer.this.dispose();
                        //CreateNewServer.this.dispatchEvent(new WindowEvent(CreateNewServer.this, WindowEvent.WINDOW_CLOSING));
                    }
                });
            }
                
        } catch (Throwable ex) {
            Logger.getLogger(CreateNewServer.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
   
    /**
     * Creates File Chat Server.
     * If there is a opened server on this app it will ask to disconnect.
     * if user selects yes;
     * Checks the ports and creates the server.
     * @return 
     */
    public boolean createServer(String serverName,int portNo, int filePortNo)
    {
        int selection = JOptionPane.NO_OPTION;
        int port = portNo;
        if(FileChat.monitor.sv != null)
        {
            selection = JOptionPane.showConfirmDialog(this, "Do you want to close Server ?","Server is Closing !",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(selection == JOptionPane.YES_OPTION)
            {
                FileChat.monitor.disconnect();
            }
        }else 
        {
            selection = JOptionPane.YES_OPTION;
        }
        
        if(selection == JOptionPane.YES_OPTION)
        {
            boolean createSv = false;
            
            if(FileChat.monitor.cl != null)
            {
                FileChat.monitor.cl.closeClient();
                FileChat.monitor.cl = null;
                FileChat.monitor.setup();
                createSv = true;
            }
             else if(FileChat.monitor.sv == null)
            {
                FileChat.monitor.setup();
                createSv = true;
            }
            if(createSv)
            {
                 try {
                     FileChat.monitor.sv = new Server(serverName,port,filePortNo);
                 }
                 catch (Exception ex) {
                     Logger.getLogger(CreateNewServer.class.getName()).log(Level.SEVERE, null, ex);
                 }
                if(FileChat.monitor.sv.sSocket != null && !FileChat.monitor.sv.sSocket.isClosed())
                {
                    FileChat.monitor.fileSv = new FileServer(serverName,FileChat.monitor.sv.sIp,filePortNo+"");
                    FileChat.monitor.setMonitorTitle(FileChat.monitor.sv.sName);
                    return true;
                }else
                {
                    JOptionPane.showMessageDialog(this, "Connection Failed !","Server Is Not Created",JOptionPane.WARNING_MESSAGE);
                    FileChat.monitor.setTitle("");
                    FileChat.monitor.sv = null;
                    return false;
                }
            }
        }
        return false;
    } 
    
    /**
     * Checks the connection type.
     * If user selected local connection it checks local connection.
     * if user selected remote connection it check remote connection.
     * @return boolean if connection type is available.
     */
    public boolean checkConnectionType()
    {
        boolean isOnNetwork = false;
        try {
            isOnNetwork = Tool.checkInternetConnection();
        }
        catch (SocketException ex) {isOnNetwork = false;}
        
        if(!isOnNetwork)
        {
            txt_ServerIp.setText("No Connection !");
            return false;
        }else
        {
            if(cmb_ConnectionType.getSelectedIndex() == 0)
            {
                String ip = null;
                try {
                    ip = Tool.getLocalIp();
                    txt_ServerIp.setText("No Connection !");
                }
                catch (Exception e) {
                   return false;
                }
                txt_ServerIp.setText(ip);
                return true;
            }else if(cmb_ConnectionType.getSelectedIndex() == 1)
            {
                String ip = Tool.readIP();
                if(ip == null)
                {
                    cmb_ConnectionType.setSelectedIndex(0);
                    txt_ServerIp.setText("No Remote Connection !");
                    JOptionPane.showMessageDialog(this,"You do not have remote connection !","Connection Problem !",JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                this.txt_ServerIp.setText(ip);
            }
        }
        
        return true;
    }
    
    
    /**
     * Checks ports avaiblity.
     */
    public boolean checkCreateServerState()
    {
        
        
        if(!checkConnectionType())
            return false;
        
        int serverFilePort = Integer.parseInt(txt_ServerFilePort.getText().isEmpty() ? "0" : txt_ServerFilePort.getText());
        int serverPort = Integer.parseInt(txt_ServerPort.getText().isEmpty() ? "0" : txt_ServerPort.getText());
        
        
        if(serverFilePort < 0 || serverFilePort > 65535)
        {
            JOptionPane.showMessageDialog(this, "Your file port must be between 0 and 65535.","Port Info",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }else if(serverPort < 0 || serverPort > 65535)
        {
            JOptionPane.showMessageDialog(this, "Your server port must be between 0 and 65535.","Port Info",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }else
        {
            int result = Tool.isAvailablePort(serverPort);
            
            
            if( result == Tool.SOCKET_IN_USE)
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverPort+" is using by another program !","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }else if( result == Tool.SOCKET_NOT_AVAILABLE )
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverPort+" is not available ...","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            result = Tool.isAvailablePort(serverFilePort);
            
            if( result == Tool.SOCKET_IN_USE)
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverFilePort+" is using by another program !","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }else if( result == Tool.SOCKET_NOT_AVAILABLE )
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverFilePort+" is not available ...","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txt_ServerName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_ServerIp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmb_ConnectionType = new javax.swing.JComboBox<>();
        txt_ServerPort = new javax.swing.JFormattedTextField();
        btn_createServer = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txt_ServerFilePort = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create Server");
        setResizable(false);

        jLabel1.setText("Server Name :");

        jLabel2.setText("Server Port :");

        jLabel3.setText("Server IP :");

        txt_ServerIp.setEditable(false);

        jLabel4.setText("Server Type :");

        cmb_ConnectionType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Local Server ", "Remote Server" }));
        cmb_ConnectionType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ConnectionTypeItemStateChanged(evt);
            }
        });

        txt_ServerPort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        btn_createServer.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btn_createServer.setText("Create Server");

        jLabel6.setText("Server File Port :");

        txt_ServerFilePort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_createServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_ServerPort, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_ServerIp, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_ConnectionType, javax.swing.GroupLayout.Alignment.LEADING, 0, 196, Short.MAX_VALUE)
                            .addComponent(txt_ServerName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_ServerFilePort, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ServerName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_ConnectionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ServerIp, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_ServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ServerFilePort, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_createServer, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // If connection tye changed changes connection type on app.
    private void cmb_ConnectionTypeItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cmb_ConnectionTypeItemStateChanged
    {//GEN-HEADEREND:event_cmb_ConnectionTypeItemStateChanged
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    if(cmb_ConnectionType.getSelectedIndex() == 0)
                    {
                        FileChat.monitor.isRemoteConnection = false;
                    }else if(cmb_ConnectionType.getSelectedIndex() == 1)
                    {
                        FileChat.monitor.isRemoteConnection = true;
                    }
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            CreateNewServer.this.checkConnectionType();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(CreateNewServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(CreateNewServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }).start();
    }//GEN-LAST:event_cmb_ConnectionTypeItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_createServer;
    private javax.swing.JComboBox<String> cmb_ConnectionType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JFormattedTextField txt_ServerFilePort;
    private javax.swing.JTextField txt_ServerIp;
    private javax.swing.JTextField txt_ServerName;
    private javax.swing.JFormattedTextField txt_ServerPort;
    // End of variables declaration//GEN-END:variables
}


