package Chat;

import FileShare.FileServer;
import com.sun.glass.events.KeyEvent;
import java.awt.Container;
import java.awt.Window;
import java.net.SocketException;
import java.util.ArrayList;
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
public class Creator_Server extends javax.swing.JDialog
{
    public static final int LOCAL_SERVER = 0;
    public static final int REMOTE_SERVER = 1;
    public boolean isServerCreated = false;
    public Container root = null;
    private ShowWaitAction checkConnectionAction = null;
    
    
    /**
     * Creates new form CreateNewServer
     */
    public Creator_Server(Container root)
    {
        super((Window)root);
        initComponents();
        
        super.setLocationRelativeTo(root);
        super.setModal(true);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        this.root = root;
        ShowWaitAction setup = new ShowWaitAction("Wait action", root, "Checking Your Network Connection...", new Runnable() {
            @Override
            public void run() {
                try {
                Creator_Server.this.txt_ServerName.setDocument(Tool.getJTextFieldLimit(FileChatConstants.USER_NAME_LENGTH_LIMIT));
                Creator_Server.this.checkConnectionType();
                Creator_Server.this.cmb_ConnectionType.setSelectedIndex(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        });
        
        setup.actionPerformed(null);
        
        try {
            System.out.println("Check ip  local: "+Tool.isHaveLocalIP+"   remote: "+Tool.isHaveRemoteIP);
            if(!(Tool.isHaveLocalIP || Tool.isHaveRemoteIP))
            {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(root, "You do not have any network connection !","Connection Problem !",JOptionPane.WARNING_MESSAGE);
                        Creator_Server.this.dispose();
                        //CreateNewServer.this.dispatchEvent(new WindowEvent(Creator_Server.this, WindowEvent.WINDOW_CLOSING));
                    }
                });
            }
                
        } catch (Throwable ex) {
            Logger.getLogger(Creator_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        checkConnectionAction = new ShowWaitAction("Check Connection", this, "Searching For Network Interfaces", new Runnable()
        {
            @Override
            public void run()
            {
                if(cmb_ConnectionType.getSelectedIndex() == 0)
                {
                    FileChat.monitor.isRemoteConnection = false;
                }else if(cmb_ConnectionType.getSelectedIndex() == 1)
                {
                    FileChat.monitor.isRemoteConnection = true;
                }
                Creator_Server.this.checkConnectionType();
                
            }
        });
       
        cmb_ConnectionType.addActionListener(checkConnectionAction);
        
        
        btn_createServer.addActionListener(new ShowWaitAction("Wait Action", this,"Testing Your Ports...",new Runnable() // Shows wait dialog on check.
        {
            @Override
            public void run()
            {
                
                if(Creator_Server.this.checkCreateServerState()) // checks the Server ports , server name and connection type. Tries to create server.
                {
                    String name = txt_ServerName.getText().replaceAll(" ", "_");
                    if(name.isEmpty())
                        name = "Server";
                    int serverFilePort = Integer.parseInt(txt_ServerFilePort.getText().isEmpty() ? "0" : txt_ServerFilePort.getText());
                    int serverPort = Integer.parseInt(txt_ServerPort.getText().isEmpty() ? "0" : txt_ServerPort.getText());
                    Creator_Server.this.isServerCreated = Creator_Server.this.createServer(name, serverPort, serverFilePort);
                    if(Creator_Server.this.isServerCreated)
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(250);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Creator_Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Creator_Server.this.setVisible(false);
                            }
                        }).start();
                    }
                }

            }
        }));
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
            selection = JOptionPane.showConfirmDialog(Creator_Server.this, "Do you want to close Server ?","Server is Closing !",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
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
                     FileChat.monitor.sv = new Server(serverName,(String)cmb_Ip.getSelectedItem(),port,filePortNo);
                 }
                 catch (Exception ex) {
                     Logger.getLogger(Creator_Server.class.getName()).log(Level.SEVERE, null, ex);
                 }
                if(FileChat.monitor.sv.sSocket != null && !FileChat.monitor.sv.sSocket.isClosed())
                {
                    FileChat.monitor.fileSv = new FileServer(serverName,FileChat.monitor.sv.sIp,filePortNo+"");
                    FileChat.monitor.setMonitorTitle(FileChat.monitor.sv.sName);
                    return true;
                }else
                {
                    JOptionPane.showMessageDialog(Creator_Server.this, "Connection Failed !","Server Is Not Created",JOptionPane.WARNING_MESSAGE);
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
        cmb_Ip.removeAllItems();
        boolean isOnNetwork = false;
        try {
            isOnNetwork = Tool.checkInternetConnection();
        }
        catch (SocketException ex) {isOnNetwork = false;}
        
        if(!isOnNetwork)
        {
            cmb_Ip.addItem("No Connection !");
            return false;
        }else
        {
            ArrayList<String> ipList = new ArrayList<String>();
            if(cmb_ConnectionType.getSelectedIndex() == 0)
            {
                try {
                    ipList = Tool.getLocalIp();
                    if(ipList == null)
                        cmb_Ip.addItem("No Local Connection !");
                }
                catch (Exception e) {
                   return false;
                }
                
                for (String ip : ipList) {
                    cmb_Ip.addItem(ip);
                }
                return true;
            }else if(cmb_ConnectionType.getSelectedIndex() == 1)
            {
                ipList = Tool.readIP();
                if(ipList == null)
                {
                    cmb_Ip.addItem("No Remoote Connection !");
                    JOptionPane.showMessageDialog(this,"You do not have remote connection !","Connection Problem !",JOptionPane.WARNING_MESSAGE);
                    return false;
                }
               cmb_Ip.addItem(ipList.get(0));
            }
        }
        
        return true;
    }
    
    
    /**
     * Checks ports avaiblity.
     */
    public boolean checkCreateServerState()
    {
        
        try {
            if(!Tool.checkInternetConnection())
                return false;
        } catch (SocketException ex) {return false;}
        
        int serverFilePort = Integer.parseInt(txt_ServerFilePort.getText().isEmpty() ? "-1" : txt_ServerFilePort.getText());
        int serverPort = Integer.parseInt(txt_ServerPort.getText().isEmpty() ? "-1" : txt_ServerPort.getText());
        
        
        if(serverFilePort <= 0 || serverFilePort > 65535)
        {
            JOptionPane.showMessageDialog(this, "Your file port must be between 0 and 65535.","Port Info",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }else if(serverPort < 0 || serverPort > 65535)
        {
            JOptionPane.showMessageDialog(this, "Your server port must be between 0 and 65535.","Port Info",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }else
        {
            int result = Tool.isAvailablePort(cmb_Ip.getSelectedItem().toString(),serverPort);
            
            
            if( result == Tool.SOCKET_IN_USE)
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverPort+" is using by another program !","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }else if( result == Tool.SOCKET_NOT_AVAILABLE )
            {
                JOptionPane.showMessageDialog(this, "The port :"+serverPort+" is not available ...","Port Warning !",JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            result = Tool.isAvailablePort(cmb_Ip.getSelectedItem().toString(),serverFilePort);
            
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
        jLabel4 = new javax.swing.JLabel();
        cmb_ConnectionType = new javax.swing.JComboBox<>();
        txt_ServerPort = new javax.swing.JFormattedTextField();
        btn_createServer = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txt_ServerFilePort = new javax.swing.JFormattedTextField();
        cmb_Ip = new javax.swing.JComboBox<>();

        setTitle("Create Server");
        setResizable(false);

        jLabel1.setText("Server Name :");

        txt_ServerName.setNextFocusableComponent(cmb_ConnectionType);
        txt_ServerName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ServerNameKeyTyped(evt);
            }
        });

        jLabel2.setText("Server Port :");

        jLabel3.setText("Server IP :");

        jLabel4.setText("Server Type :");

        cmb_ConnectionType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Local Server ", "Remote Server" }));
        cmb_ConnectionType.setNextFocusableComponent(cmb_Ip);

        txt_ServerPort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_ServerPort.setNextFocusableComponent(txt_ServerFilePort);
        txt_ServerPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ServerNameKeyTyped(evt);
            }
        });

        btn_createServer.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btn_createServer.setText("Create Server");
        btn_createServer.setNextFocusableComponent(txt_ServerName);

        jLabel6.setText("Server File Port :");

        txt_ServerFilePort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_ServerFilePort.setNextFocusableComponent(btn_createServer);
        txt_ServerFilePort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ServerNameKeyTyped(evt);
            }
        });

        cmb_Ip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No IP !" }));
        cmb_Ip.setNextFocusableComponent(txt_ServerPort);

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_ServerPort)
                            .addComponent(cmb_ConnectionType, 0, 196, Short.MAX_VALUE)
                            .addComponent(txt_ServerName)
                            .addComponent(txt_ServerFilePort)
                            .addComponent(cmb_Ip, javax.swing.GroupLayout.Alignment.TRAILING, 0, 196, Short.MAX_VALUE))))
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
                    .addComponent(cmb_Ip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void txt_ServerNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ServerNameKeyTyped
        if(evt.getKeyChar() == KeyEvent.VK_ENTER)
            btn_createServer.doClick();
    }//GEN-LAST:event_txt_ServerNameKeyTyped

        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_createServer;
    private javax.swing.JComboBox<String> cmb_ConnectionType;
    private javax.swing.JComboBox<String> cmb_Ip;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JFormattedTextField txt_ServerFilePort;
    private javax.swing.JTextField txt_ServerName;
    private javax.swing.JFormattedTextField txt_ServerPort;
    // End of variables declaration//GEN-END:variables
}


