package Chat;

import FileShare.DownloadViewer;
import FileShare.Creator_SharedFileEditor;
import FileShare.Creator_SharedFile;
import FileShare.FileServer;
import static Chat.FileChatConstants.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

/**Main control and GUI class of File chat App.
 * 
 * @author BURAK
 */
public class Monitor extends javax.swing.JFrame implements FileChatConstants
{
    public static String downloadFolder = System.getProperty("user.home")+SYSTEM_SEPERATOR+"Desktop"+SYSTEM_SEPERATOR+"Download"; // download folder path
    
        //-----INSTANCE VARIABLES-----
    //-------------------------------------------------
    ArrayList<String> clientNameList; // User name list
    ArrayList<String> mList; // Message list
    
    Server sv; //If a server will be created user will be a server.
    Client cl; // If user connects a server user will be client and uses this variable.
    boolean isShifPressed = false; // Using for listening keyboard. If its true the cursor will goes to next line. if its false enter key will send the wroten message.
    
    public boolean isOverRideFiles = OVERRIDE_DOWNLOADED_FILES; // If its true the program will override downloaded same files.
    public boolean isAutomaticDownload = AUTOMATIC_DOWNLOAD_FILES; // If its true programm will download shared files automatic.
    public int downloadSpeedLimit = DOWNLOAD_SPEED_LIMIT; // Byte size limit of download/time
    public int uploadSpeedLimit = UPLOAD_SPEED_LIMIT; // Byte size limit of upload/time
    public FileServer fileSv; // Apps file server.
    
    String lastMessageOwnerName = ""; // It is using to define last writed message on monitor. If its same the name will be not writed again with his message.
    DefaultTableModel dtm; // Shared file table model.
    Thread uploadThread; //Upload Control thread.
    GridBagConstraints cons_pnl_DownUp_View = null; // Constats of download viewer panel layout.
    ArrayList<DownloadViewer> dlViewList = null; // download viewer list.
    boolean isRemoteConnection = false; //Connection type of server.
    boolean isAnswered = false; // upload accepted answer
    Creator_Client createNewClientGui = null; // Client-Server connection creator.
    Creator_Server svCreater = null; // Server Creator.
    //-------------------------------------------------

    

    /**
     * Creates new form Monitor
     */
    public Monitor()
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        initComponents();
        
        setup();
        createNewClientGui = new Creator_Client(this);
        svCreater = new Creator_Server(Monitor.this);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    

    //Setup to start position of Application.
    protected void setup()
    {
        this.mList = new ArrayList<String>();
        this.clientNameList = new ArrayList<String>();
        this.txtArea_MessageStore.removeAll();
        this.txtArea_MessageStore.setText("");
        this.txtArea_NewNessage.removeAll();
        this.txtArea_NewNessage.setText("");
        DefaultListModel dlm = new DefaultListModel();
        this.list_OnlineUser.setModel(dlm);
        isShifPressed = false;  
        lastMessageOwnerName = "";
        dtm = (DefaultTableModel)tbl_SharedFileTable.getModel();
        DefaultCaret caret = (DefaultCaret)txtArea_MessageStore.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        txtArea_MessageStore.setCaret(caret);
        tbl_SharedFileTable.setModel(dtm);
        
        cons_pnl_DownUp_View = new GridBagConstraints();
        cons_pnl_DownUp_View.insets = new Insets(5, 0, 0, 0);
        cons_pnl_DownUp_View.fill = GridBagConstraints.HORIZONTAL;
        cons_pnl_DownUp_View.anchor = GridBagConstraints.FIRST_LINE_END;
        cons_pnl_DownUp_View.weightx = 1;
        cons_pnl_DownUp_View.gridx = 0;
        cons_pnl_DownUp_View.weighty = 1;
        
        pnl_DownUPView.removeAll();
        pnl_DownUPView.repaint();
        refreshFileTable();
        
        GridBagLayout gLy = new GridBagLayout();
        pnl_DownUPView.setLayout(gLy);
        dlViewList = new ArrayList<DownloadViewer>();
        
        
        new File(downloadFolder).mkdirs();
        System.out.println("Setup is Done !");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        pnl_Chat = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea_MessageStore = new javax.swing.JTextArea();
        sndButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_OnlineUser = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea_NewNessage = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        pnl_SharedFiles = new javax.swing.JPanel();
        btn_AddNewFile = new javax.swing.JButton();
        btn_SelectAll = new javax.swing.JButton();
        btn_DownloadFile = new javax.swing.JButton();
        txt_SharedFileSearchText = new javax.swing.JTextField();
        btn_DeleteFile = new javax.swing.JButton();
        btn_EditFile = new javax.swing.JButton();
        btn_RefreshFileTable = new javax.swing.JButton();
        cmb_SearchType = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        prg_upload_all = new javax.swing.JProgressBar();
        prg_upload_curr = new javax.swing.JProgressBar();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_SharedFileTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        pnl_DownUPView = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItem_CreateRoom = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuItem_closeConnection = new javax.swing.JMenuItem();
        menuItem_EditMenu = new javax.swing.JMenu();
        menuItem_ChangeName = new javax.swing.JMenuItem();
        menuItem_ChangeFont = new javax.swing.JMenuItem();
        menuItem_ChangeDlFolder = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuItem_OpenDownloadFolder = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItem_LimitDownload = new javax.swing.JMenuItem();
        menuItem_LımıtUpload = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuItem_OverrideDownloadedFiles = new javax.swing.JCheckBoxMenuItem();
        menuItem_AutomaticDownloadFies = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("FILE CHAT");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));

        txtArea_MessageStore.setEditable(false);
        txtArea_MessageStore.setColumns(20);
        txtArea_MessageStore.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        txtArea_MessageStore.setLineWrap(true);
        txtArea_MessageStore.setRows(5);
        txtArea_MessageStore.setAutoscrolls(false);
        jScrollPane1.setViewportView(txtArea_MessageStore);

        sndButton.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        sndButton.setText("SEND");
        sndButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sndButtonActionPerformed(evt);
            }
        });

        list_OnlineUser.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        list_OnlineUser.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list_OnlineUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ListClicked(evt);
            }
        });
        jScrollPane2.setViewportView(list_OnlineUser);

        txtArea_NewNessage.setColumns(20);
        txtArea_NewNessage.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        txtArea_NewNessage.setRows(1);
        txtArea_NewNessage.setToolTipText("Your Message ...");
        txtArea_NewNessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EnterShiftTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keyRelased(evt);
            }
        });
        jScrollPane3.setViewportView(txtArea_NewNessage);

        javax.swing.GroupLayout pnl_ChatLayout = new javax.swing.GroupLayout(pnl_Chat);
        pnl_Chat.setLayout(pnl_ChatLayout);
        pnl_ChatLayout.setHorizontalGroup(
            pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_ChatLayout.createSequentialGroup()
                .addGroup(pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sndButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
        );
        pnl_ChatLayout.setVerticalGroup(
            pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ChatLayout.createSequentialGroup()
                .addGroup(pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sndButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane2.addTab("Chat", pnl_Chat);

        pnl_SharedFiles.setForeground(new java.awt.Color(255, 255, 255));

        btn_AddNewFile.setBackground(new java.awt.Color(255, 255, 255));
        btn_AddNewFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/newFile.png"))); // NOI18N
        btn_AddNewFile.setToolTipText("Share New File");
        btn_AddNewFile.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_AddNewFile.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_AddNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddNewFileActionPerformed(evt);
            }
        });

        btn_SelectAll.setBackground(new java.awt.Color(255, 255, 255));
        btn_SelectAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/selectAll.png"))); // NOI18N
        btn_SelectAll.setToolTipText("Select All Files");
        btn_SelectAll.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_SelectAll.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_SelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SelectAllActionPerformed(evt);
            }
        });

        btn_DownloadFile.setBackground(new java.awt.Color(255, 255, 255));
        btn_DownloadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/downloadFiles.png"))); // NOI18N
        btn_DownloadFile.setToolTipText("Download Selected Files");
        btn_DownloadFile.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_DownloadFile.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_DownloadFile.setPreferredSize(new java.awt.Dimension(65, 53));
        btn_DownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DownloadFileActionPerformed(evt);
            }
        });

        txt_SharedFileSearchText.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        txt_SharedFileSearchText.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_SharedFileSearchText.setPreferredSize(new java.awt.Dimension(6, 53));
        txt_SharedFileSearchText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SharedFileSearchTextKeyTyped(evt);
            }
        });

        btn_DeleteFile.setBackground(new java.awt.Color(255, 255, 255));
        btn_DeleteFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/deleteFile.png"))); // NOI18N
        btn_DeleteFile.setToolTipText("Delete File");
        btn_DeleteFile.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_DeleteFile.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_DeleteFile.setPreferredSize(new java.awt.Dimension(65, 41));
        btn_DeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DeleteFileActionPerformed(evt);
            }
        });

        btn_EditFile.setBackground(new java.awt.Color(255, 255, 255));
        btn_EditFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/editFile.png"))); // NOI18N
        btn_EditFile.setToolTipText("Edit File");
        btn_EditFile.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_EditFile.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_EditFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EditFileActionPerformed(evt);
            }
        });

        btn_RefreshFileTable.setBackground(new java.awt.Color(255, 255, 255));
        btn_RefreshFileTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/loading/loading1.png"))); // NOI18N
        btn_RefreshFileTable.setToolTipText("Refresh Table");
        btn_RefreshFileTable.setMaximumSize(new java.awt.Dimension(57, 57));
        btn_RefreshFileTable.setMinimumSize(new java.awt.Dimension(57, 57));
        btn_RefreshFileTable.setPreferredSize(new java.awt.Dimension(65, 41));
        btn_RefreshFileTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RefreshFileTableActionPerformed(evt);
            }
        });

        cmb_SearchType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Share Name", "Notification", "Size ( BYTE )", "User", "Share Time", "Files" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "UPLOAD PROGRESS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 69));

        prg_upload_all.setBackground(new java.awt.Color(255, 255, 255));
        prg_upload_all.setMaximum(1000);
        prg_upload_all.setString("");
        prg_upload_all.setStringPainted(true);

        prg_upload_curr.setBackground(new java.awt.Color(255, 255, 255));
        prg_upload_curr.setMaximum(1000);
        prg_upload_curr.setString("");
        prg_upload_curr.setStringPainted(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prg_upload_all, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
            .addComponent(prg_upload_curr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(prg_upload_all, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(prg_upload_curr, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnl_SharedFilesLayout = new javax.swing.GroupLayout(pnl_SharedFiles);
        pnl_SharedFiles.setLayout(pnl_SharedFilesLayout);
        pnl_SharedFilesLayout.setHorizontalGroup(
            pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_SharedFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_SharedFilesLayout.createSequentialGroup()
                        .addComponent(cmb_SearchType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_SharedFileSearchText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnl_SharedFilesLayout.createSequentialGroup()
                        .addComponent(btn_AddNewFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_EditFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_DeleteFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_RefreshFileTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_DownloadFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_SelectAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_SharedFilesLayout.setVerticalGroup(
            pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_SharedFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_SharedFileSearchText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(cmb_SearchType, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_SelectAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_SharedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_AddNewFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_EditFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_DeleteFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_RefreshFileTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_DownloadFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SHARED FILES", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        tbl_SharedFileTable.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        tbl_SharedFileTable.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        tbl_SharedFileTable.setForeground(new java.awt.Color(51, 51, 51));
        tbl_SharedFileTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Share Name", "Notification", "Size ( BYTE )", "User", "Share Time", "Files"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_SharedFileTable.setAutoscrolls(false);
        tbl_SharedFileTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbl_SharedFileTable.setFillsViewportHeight(true);
        tbl_SharedFileTable.setName("Shared File Table"); // NOI18N
        tbl_SharedFileTable.setRowHeight(30);
        tbl_SharedFileTable.setRowMargin(2);
        tbl_SharedFileTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tbl_SharedFileTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbl_SharedFileTable.setShowHorizontalLines(true);
        tbl_SharedFileTable.setShowVerticalLines(true);
        tbl_SharedFileTable.getTableHeader().setReorderingAllowed(false);
        tbl_SharedFileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_SharedFileTableMouseClicked(evt);
            }
        });
        tbl_SharedFileTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbl_SharedFileTableKeyTyped(evt);
            }
        });
        jScrollPane4.setViewportView(tbl_SharedFileTable);

        jScrollPane5.setPreferredSize(new java.awt.Dimension(0, 0));

        pnl_DownUPView.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DOWNLOAD PROGRESS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnl_DownUPView.setMinimumSize(new java.awt.Dimension(500, 138));

        javax.swing.GroupLayout pnl_DownUPViewLayout = new javax.swing.GroupLayout(pnl_DownUPView);
        pnl_DownUPView.setLayout(pnl_DownUPViewLayout);
        pnl_DownUPViewLayout.setHorizontalGroup(
            pnl_DownUPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 766, Short.MAX_VALUE)
        );
        pnl_DownUPViewLayout.setVerticalGroup(
            pnl_DownUPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        jScrollPane5.setViewportView(pnl_DownUPView);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_SharedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnl_SharedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("File Transfer", jPanel1);

        jMenu1.setText("Connection");

        menuItem_CreateRoom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        menuItem_CreateRoom.setText("CreateServer");
        menuItem_CreateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_CreateRoomActionPerformed(evt);
            }
        });
        jMenu1.add(menuItem_CreateRoom);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Connect a Server");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        menuItem_closeConnection.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        menuItem_closeConnection.setText("Close Connection");
        menuItem_closeConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_closeConnectionActionPerformed(evt);
            }
        });
        jMenu1.add(menuItem_closeConnection);

        jMenuBar1.add(jMenu1);

        menuItem_EditMenu.setText("Edit");

        menuItem_ChangeName.setText("Change Name");
        menuItem_ChangeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ChangeNameActionPerformed(evt);
            }
        });
        menuItem_EditMenu.add(menuItem_ChangeName);

        menuItem_ChangeFont.setText("Change Font");
        menuItem_ChangeFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ChangeFontActionPerformed(evt);
            }
        });
        menuItem_EditMenu.add(menuItem_ChangeFont);

        menuItem_ChangeDlFolder.setText("Change Download Folder");
        menuItem_ChangeDlFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ChangeDlFolderActionPerformed(evt);
            }
        });
        menuItem_EditMenu.add(menuItem_ChangeDlFolder);

        jMenuBar1.add(menuItem_EditMenu);

        jMenu3.setText("File Transfer");

        menuItem_OpenDownloadFolder.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuItem_OpenDownloadFolder.setText("Open Download Folder");
        menuItem_OpenDownloadFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_OpenDownloadFolderActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_OpenDownloadFolder);
        jMenu3.add(jSeparator1);

        menuItem_LimitDownload.setText("Limit Download Speed");
        menuItem_LimitDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_LimitDownloadActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_LimitDownload);

        menuItem_LımıtUpload.setText("Limit Upload Speed");
        menuItem_LımıtUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_LımıtUploadActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_LımıtUpload);
        jMenu3.add(jSeparator3);

        menuItem_OverrideDownloadedFiles.setSelected(true);
        menuItem_OverrideDownloadedFiles.setText("Override Downloaded Files");
        menuItem_OverrideDownloadedFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_OverrideDownloadedFilesActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_OverrideDownloadedFiles);

        menuItem_AutomaticDownloadFies.setText("Automatic Download Shared Files");
        menuItem_AutomaticDownloadFies.setToolTipText("Automatic Download Shared Files when shared.");
        menuItem_AutomaticDownloadFies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_AutomaticDownloadFiesActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_AutomaticDownloadFies);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    



    //Starts a new Creator_Server instance. If there is a connection programm wil lask to disconnect.
    // Listens Creator_Server instance if a server created or canceled handles it.
    private void menuItem_CreateRoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_CreateRoomActionPerformed

    {//GEN-HEADEREND:event_menuItem_CreateRoomActionPerformed
        
        boolean isDisconnected = false;
        if(this.sv != null || this.cl != null)
            isDisconnected = askAndDisconnect();
        else
            isDisconnected = true;
        
        if(isDisconnected)
        {
                    
            svCreater.setLocationRelativeTo(FileChat.monitor);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    svCreater.setVisible(true);
                }
            });
        }
        
        
    }//GEN-LAST:event_menuItem_CreateRoomActionPerformed

    

    //Asks for new name and changes old one.
    private void menuItem_ChangeNameActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_ChangeNameActionPerformed

    {//GEN-HEADEREND:event_menuItem_ChangeNameActionPerformed

        if(this.sv != null)
        {
            String sName = this.sv.sName;
            do
            {
                sName = JOptionPane.showInputDialog("New Server Name : ").trim();
                if (FileChat.monitor.clientNameList.contains(sName)) {
                    JOptionPane.showMessageDialog(this, "Bu isim kullanılıyor !");
                    sName = "";
                }
            }while(sName.isEmpty());
            
            changeName(sName.replaceAll(" ", "_"));
        }else if(this.cl != null)
        {
            String name = this.cl.cName;
            do
            {
                name = JOptionPane.showInputDialog("New Name  : ");
                if (FileChat.monitor.clientNameList.contains(name)) {
                    JOptionPane.showMessageDialog(this, "Bu isim kullanılıyor !");
                    name = "";
                }
            }while(name.isEmpty());
            changeName(name);
        }
    }//GEN-LAST:event_menuItem_ChangeNameActionPerformed

   

    //Asks for new font and changes the message areas and users lists font.
    private void menuItem_ChangeFontActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_ChangeFontActionPerformed

    {//GEN-HEADEREND:event_menuItem_ChangeFontActionPerformed

        FontChooser fontCh = new FontChooser(this);
        fontCh.setVisible(true);
        Font newFont = fontCh.getSelectedFont();
        if(newFont != null)
        {
            txtArea_MessageStore.setFont(newFont);
            txtArea_NewNessage.setFont(newFont);
        }
    }//GEN-LAST:event_menuItem_ChangeFontActionPerformed
    
    //Changes the default download folder with new selected one.
    private void menuItem_ChangeDlFolderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_ChangeDlFolderActionPerformed
    {//GEN-HEADEREND:event_menuItem_ChangeDlFolderActionPerformed
        JFileChooser fc = new JFileChooser(new File(downloadFolder));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setApproveButtonToolTipText("Select a download folder");
        int selection = fc.showDialog(this, "Select Folder");
        if(selection == JFileChooser.APPROVE_OPTION)
        {
            File downloadFolder = fc.getSelectedFile();
            this.downloadFolder = downloadFolder.getAbsolutePath();
        }
    }//GEN-LAST:event_menuItem_ChangeDlFolderActionPerformed
    
    // Opens download folder on Operating System Explorer.
    private void menuItem_OpenDownloadFolderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_OpenDownloadFolderActionPerformed
    {//GEN-HEADEREND:event_menuItem_OpenDownloadFolderActionPerformed
        try {
                Runtime.getRuntime().exec("cmd /c mkdir "+downloadFolder);
                Runtime.getRuntime().exec("cmd /c explorer.exe "+downloadFolder);
        }
        catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItem_OpenDownloadFolderActionPerformed
    
    //Asks and deletes the own selected shared file.
    private void tbl_SharedFileTableKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_tbl_SharedFileTableKeyTyped
    {//GEN-HEADEREND:event_tbl_SharedFileTableKeyTyped
        if(evt.getKeyChar() == KeyEvent.VK_DELETE)
        {
            deleteSelectedSharedFile();
        }
    }//GEN-LAST:event_tbl_SharedFileTableKeyTyped
    
    //Changes the row height of selected shared file item.
    private void tbl_SharedFileTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tbl_SharedFileTableMouseClicked
    {//GEN-HEADEREND:event_tbl_SharedFileTableMouseClicked
        int[] selectedIndexes = tbl_SharedFileTable.getSelectedRows();
        tbl_SharedFileTable.setRowHeight(25);
        if(selectedIndexes != null)
        {
            for (int selectedIndex : selectedIndexes)
            {
                tbl_SharedFileTable.setRowHeight(selectedIndex, 60);
            }
        }
    }//GEN-LAST:event_tbl_SharedFileTableMouseClicked
    
    //Refreshes the Shared file table.
    private void btn_RefreshFileTableActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_RefreshFileTableActionPerformed
    {//GEN-HEADEREND:event_btn_RefreshFileTableActionPerformed
        refreshFileTable();
    }//GEN-LAST:event_btn_RefreshFileTableActionPerformed
    
    //Opens a new Creator_SharedFileEditor instance.
    //Changes the new Shared file with old one and notices it to server.
    private void btn_EditFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_EditFileActionPerformed
    {//GEN-HEADEREND:event_btn_EditFileActionPerformed

        try {
            if(sv != null || cl != null)
        {
            SharedFile[] sharedFiles = getSelectedSharedFiles();
            if(sharedFiles != null && sharedFiles.length == 1 && fileSv.mySharedFiles.contains(sharedFiles[0]))
            {

                Creator_SharedFileEditor crFile = fileSv.createEditSharedFile(sharedFiles[0]);
                crFile.setLocation(this.getLocation());
                crFile.toFront();
                new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            while(true)
                            {
                                try
                                {
                                    if(crFile == null)
                                    {
                                        break;
                                    }
                                    else if(crFile.newSharedFile != null)
                                    {
                                        String sharedFileStr = crFile.oldSharedFile.getId()+SYSTEM_MESSAGE_BRACKET+SharedFile.toMessageString(crFile.newSharedFile);
                                        if(FileChat.monitor.sv != null)
                                        {
                                            SharedFile shFile = crFile.newSharedFile;
                                            fileSv.editSharedFile(crFile.oldSharedFile.getId(),shFile);
                                            FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_EDIT_SHAREDFILE,sharedFileStr);
                                        }else if(FileChat.monitor.cl != null)
                                        {
                                            FileChat.monitor.cl.sendRequestMessage(USER_REQUEST_TYPE_EDIT_SHAREDFILE, sharedFileStr);
                                        }
                                        FileChat.monitor.refreshFileTable();
                                        crFile.dispose();
                                        break;
                                    }
                                    Thread.sleep(100L);
                                }
                                catch (InterruptedException ex)
                                {
                                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }).start();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().toLowerCase().equals("current modification"))
            {
                btn_EditFile.doClick();
            }
        }
    }//GEN-LAST:event_btn_EditFileActionPerformed
    
    //Deletes the selected shared file.
    private void btn_DeleteFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_DeleteFileActionPerformed
    {//GEN-HEADEREND:event_btn_DeleteFileActionPerformed
        deleteSelectedSharedFile();
    }//GEN-LAST:event_btn_DeleteFileActionPerformed
    
    //Lists matching items with typed text with using search type.
    private void txt_SharedFileSearchTextKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txt_SharedFileSearchTextKeyTyped
    {//GEN-HEADEREND:event_txt_SharedFileSearchTextKeyTyped
        SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    String text = txt_SharedFileSearchText.getText().toLowerCase();
                    int searchType = cmb_SearchType.getSelectedIndex();
                    if(text.length() > 0)
                    {
                        dtm.setRowCount(0);
                        for (SharedFile sharedFile : fileSv.getSharedFileList())
                        {
                            if(searchType == 2 && Tool.isNumeric(text))//Eğer size a göre arama yapılıyorsa
                            {
                                long size = Long.parseLong(sharedFile.toTableRow()[searchType].toString());
                                long maxSize = Long.parseLong(text);
                                if(size < maxSize)
                                {
                                    dtm.addRow(sharedFile.toTableRow());
                                }
                            }else if(sharedFile.toTableRow()[searchType].toString().toLowerCase().contains(text))
                            {
                                dtm.addRow(sharedFile.toTableRow());
                            }
                        }
                    }else
                    {
                        refreshFileTable();
                    }
                }
            });
    }//GEN-LAST:event_txt_SharedFileSearchTextKeyTyped
    
    //Sends upload request to download a shared file if its not users own file.
    private void btn_DownloadFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_DownloadFileActionPerformed
    {//GEN-HEADEREND:event_btn_DownloadFileActionPerformed
        SharedFile[] selectedFiles = getSelectedSharedFiles();
        if(selectedFiles != null && !(selectedFiles.length <= 0) )
        {
            for (SharedFile selectedFile : selectedFiles)
            {
                downloadSharedFile(selectedFile);
            }
        }
    }//GEN-LAST:event_btn_DownloadFileActionPerformed
    
    //Selects all shared files on table.
    private void btn_SelectAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_SelectAllActionPerformed
    {//GEN-HEADEREND:event_btn_SelectAllActionPerformed
        tbl_SharedFileTable.selectAll();
    }//GEN-LAST:event_btn_SelectAllActionPerformed

    // Creates new Creator_SharedFile instance.
    //Listens the instance.
    //If user shares a file sends notice to users.
    //If its not does noting and Creator_SharedFile instance will disposed.
    private void btn_AddNewFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_AddNewFileActionPerformed
    {//GEN-HEADEREND:event_btn_AddNewFileActionPerformed
        if(sv != null || cl != null)
        {
            Creator_SharedFile crFile = fileSv.createAddNewSharedFile();
            crFile.setLocation(this.getLocation());
            crFile.toFront();
            new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        while(true)
                        {
                            try
                            {
                                if(crFile == null)
                                {
                                    break;
                                }
                                else if(crFile.newSharedFile != null)
                                {
                                    String sharedFileStr = SharedFile.toMessageString(crFile.newSharedFile);
                                    if(FileChat.monitor.sv != null)
                                    {
                                        SharedFile shFile = crFile.newSharedFile;
                                        fileSv.addNewSharedFile(shFile);
                                        FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_NEW_SHAREDFILE,sharedFileStr);
                                    }else if(FileChat.monitor.cl != null)
                                    {
                                        FileChat.monitor.cl.sendRequestMessage(USER_REQUEST_TYPE_NEW_SHAREDFILE, sharedFileStr);
                                    }
                                    FileChat.monitor.refreshFileTable();
                                    crFile.setVisible(false);
                                    crFile.dispose();
                                    break;
                                }
                                Thread.sleep(100L);
                            }
                            catch (InterruptedException ex)
                            {
                                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }).start();
            }
    }//GEN-LAST:event_btn_AddNewFileActionPerformed

    //Listens keyboard for shift key.
    private void keyRelased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_keyRelased
    {//GEN-HEADEREND:event_keyRelased

        if(evt.getKeyCode() == KeyEvent.VK_SHIFT )
        {
            isShifPressed = false;
        }
    }//GEN-LAST:event_keyRelased

    //If user pressed shift key and enter key at the same time the cursor will go to next line.
    //If user presses only enter key app will send the wroten message.
    private void EnterShiftTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_EnterShiftTyped
    {//GEN-HEADEREND:event_EnterShiftTyped

        if(evt.getKeyCode() == KeyEvent.VK_SHIFT )
        {
            isShifPressed = true;
        }
        if(isShifPressed && evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            txtArea_NewNessage.setText(txtArea_NewNessage.getText()+"\n");
        }else if(!isShifPressed && evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            sndButton.doClick();
        }
    }//GEN-LAST:event_EnterShiftTyped

    //To send private message adds PM tag to message write area.
    private void ListClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_ListClicked
    {//GEN-HEADEREND:event_ListClicked

        if(evt.getClickCount() == 2)
        {
            txtArea_NewNessage.setText(txtArea_NewNessage.getText()+"@"+list_OnlineUser.getSelectedValue()+" ");
            txtArea_NewNessage.requestFocus();
        }
    }//GEN-LAST:event_ListClicked

    //Sends message. If it starts with system bracket it will send Pm.
    private void sndButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sndButtonActionPerformed
    {//GEN-HEADEREND:event_sndButtonActionPerformed

        String message = txtArea_NewNessage.getText();
        if(!message.isEmpty())
        if(this.cl != null)
        {
            if(message.startsWith(SYSTEM_MESSAGE_BRACKET))
            {
                this.cl.sendPrivateMessage(message);
            }else
            {
                this.cl.sendMessage(this.cl.cName+" : "+message);
            }
        }else if(this.sv != null)
        {
            if(message.startsWith(SYSTEM_MESSAGE_BRACKET))
            {
                this.sv.sendPrivateMessage(message);
            }else
            {
                sv.sendMessageToAll(sv.sName+" : "+message);
                addNewMessage(sv.sName+" : "+message);
            }
        }

        SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    txtArea_NewNessage.setText("");
                    txtArea_NewNessage.requestFocus();
                }
            });
    }//GEN-LAST:event_sndButtonActionPerformed
    
// If there is opened connection asks for disconnect. 
    // If answer will be yes user disconnects and app closes.
    // If answer will be no program will not close and didnt be disconnected.
    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        boolean result = askAndDisconnect();
        
        if(result)
            System.exit(0);
        
    }//GEN-LAST:event_formWindowClosing
    
    //Changes the state of Override option.
    private void menuItem_OverrideDownloadedFilesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_OverrideDownloadedFilesActionPerformed
    {//GEN-HEADEREND:event_menuItem_OverrideDownloadedFilesActionPerformed
        setIsOverRideFiles(!isOverRideFiles);
        menuItem_OverrideDownloadedFiles.setSelected(isOverRideFiles());
    }//GEN-LAST:event_menuItem_OverrideDownloadedFilesActionPerformed

    //Changes the state of automatic download option.
    private void menuItem_AutomaticDownloadFiesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_AutomaticDownloadFiesActionPerformed
    {//GEN-HEADEREND:event_menuItem_AutomaticDownloadFiesActionPerformed
        setIsAutomaticDownload(!isAutomaticDownload);
        menuItem_AutomaticDownloadFies.setSelected(isAutomaticDownload());
    }//GEN-LAST:event_menuItem_AutomaticDownloadFiesActionPerformed

    //Asks and changes the download speed upper limit.
    private void menuItem_LimitDownloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_LimitDownloadActionPerformed
    {//GEN-HEADEREND:event_menuItem_LimitDownloadActionPerformed
        int speed = 0;
        while(speed < 1)
        {
            String s = JOptionPane.showInputDialog("Enter New Download Speed : ",downloadSpeedLimit);

            if(s == null)break;

            try {
                speed = Integer.parseInt(s);
            }
            catch (Exception e) {
                System.err.println("Parse error @PORT INPUT");
            }
        }
        
        if(speed > 0)
        {
            downloadSpeedLimit = speed;
        }
    }//GEN-LAST:event_menuItem_LimitDownloadActionPerformed

    //Asks and changes the upload upper limit.
    private void menuItem_LımıtUploadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_LımıtUploadActionPerformed
    {//GEN-HEADEREND:event_menuItem_LımıtUploadActionPerformed
        int speed = 0;
        while(speed < 1)
        {
            String s = JOptionPane.showInputDialog("Enter New Upload Speed : ",uploadSpeedLimit);

            if(s == null)break;

            try {
                speed = Integer.parseInt(s);
            }
            catch (Exception e) {
                System.err.println("Parse error @PORT INPUT");
            }
        }
        
        if(speed > 0)
        {
            uploadSpeedLimit = speed;
        }
    }//GEN-LAST:event_menuItem_LımıtUploadActionPerformed

    //Asks and closes if there is a connection.
    private void menuItem_closeConnectionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuItem_closeConnectionActionPerformed
    {//GEN-HEADEREND:event_menuItem_closeConnectionActionPerformed
        askAndDisconnect();
    }//GEN-LAST:event_menuItem_closeConnectionActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createNewClientGui.setVisible(true);
            }
        });
        createNewClientGui.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    //--------------Server & Client Connection Control Methods------------------
    //--------------------------------------------------------------------------
    
     // Changes the user name on this app and server.
    public void changeName(String name)
    {
        if(name != null && name.length() > USER_NAME_LENGTH_LIMIT)
            name = name.substring(0,USER_NAME_LENGTH_LIMIT);
        if(this.sv  != null)
        {
            String nameChangeString = "SERVER - "+this.sv.sName+ " Changed name to : ";
            FileChat.monitor.fileSv.editSharedFile(sv.sName, name);
            FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_CHANGE_NAME, this.sv.sName+SYSTEM_MESSAGE_BRACKET+name);
            this.sv.sName = name;
            nameChangeString += name;
            setMonitorTitle(sv.sName);
            refreshClientNameList();
            FileChat.monitor.sv.sendSystemMessageToAll(SYSTEM_MESSAGE_TYPE_REFRESH_USER_LIST, nameChangeString);
            FileChat.monitor.fileSv.setFileServerOwner(name);
            FileChat.monitor.refreshFileTable();
            
        }else if(this.cl != null)
        {
            this.cl.cName = name;
            refreshClientNameList();
            setMonitorTitle(cl.cName);
            FileChat.monitor.cl.sendRequestMessage(USER_REQUEST_TYPE_CHANGE_NAME,name);
            FileChat.monitor.fileSv.setFileServerOwner(name);
        }
    }
    
    /** Adds new message and refreshes the message area.
     @return*/
    public void addNewMessage(String message)
    {
        String newName = message.split(":")[0].trim();
        if(newName != null && !newName.isEmpty() && clientNameList.contains(newName) && lastMessageOwnerName.equals(newName))
        {
            mList.add(message.substring(newName.length()+3));
            lastMessageOwnerName = newName;
        }else if(newName != null && !newName.isEmpty() && clientNameList.contains(newName))
        {
            mList.add(getTimeText()+message);
            lastMessageOwnerName = newName;
        }else
        {
            mList.add(message);
        }
        
        if(refreshMessageAreaTh != null)
        {
            isNewRefreshRequest = true;
            try {
                refreshMessageAreaTh.join();
            }
            catch (InterruptedException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            refreshMessageAreaTh = null;
            
        }
        
        refreshMessageAreaTh = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                FileChat.monitor.refreshMessageArea();
            }
        });
        
        refreshMessageAreaTh.start();
    }

    
    Thread refreshMessageAreaTh;
    static boolean isNewRefreshRequest = false;
    /**
     * Removes all messages from message area and adds all messages in mList
     **/
    public void refreshMessageArea()
    {
        Object[] mList = this.mList.toArray();
        try
        {
            String messages = "";
            txtArea_MessageStore.setText("");
            if(mList != null)
            {
                
                for (Object message : mList) {
                    if(isNewRefreshRequest)
                    {
                        txtArea_MessageStore.setText("");
                        break;
                    }
                    
                    messages += txtArea_MessageStore.getText()+message+"\n";
                    
                }
                txtArea_MessageStore.setText(messages);
                isNewRefreshRequest = false;
            }
            
            refreshMessageAreaTh = null;
        }
        catch (Exception e) {
            System.err.println("Error on monitor refresh : "+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the user name list on monitor.
     * uses list_OnlieUser variable.
     */
    public void refreshClientNameList()
    {
        try {
            if(this.cl != null)
        {
            list_OnlineUser.removeAll();
            DefaultListModel dlm = new DefaultListModel();
            int index = 0;
            while (true) {
                if (clientNameList.size()==index) {
                    break;
                }
                dlm.add(index,clientNameList.get(index));
                index++;
            }
            list_OnlineUser.setModel(dlm);
        }else if(this.sv != null)
        {
            clientNameList = new ArrayList<>();
            list_OnlineUser.removeAll();
            DefaultListModel dlm = new DefaultListModel();
            int index = 0;
            dlm.add(0, this.sv.sName);
            while (true) {
                if (this.sv.clientList.size()==index) {
                    break;
                }
                clientNameList.add(this.sv.clientList.get(index).cName);
                dlm.add(index,clientNameList.get(index));
                index++;
            }
            clientNameList.add(this.sv.sName);
            list_OnlineUser.setModel(dlm);
        }
        }
        catch (Exception e) {

            System.err.println("EXCEPTION REFRESHING CLIENT NAME LIST : "+e.getMessage());

        }
    }

    /**
     * Sets the main JFrames title with format ("FILE CHAT     ( name ServerIp ServerChatPort  /  ServerLocalIp ClientsUploadPort )")
     * @param name UserName
     */
    public void setMonitorTitle(String name){
        String ip;
        String port;
        String uploadPort;
        if(cl != null)
        {
            ip = cl.cIp;
            port = ""+cl.serverChatPort;
            uploadPort = ""+cl.uploadPort;
        }else if(sv != null)
        {
            ip = sv.sIp;
            port = ""+sv.serverPort;
            uploadPort = ""+sv.uploadPort;
        }else
        {
            ip = "No Connection.";
            port = "-";
            uploadPort = "-";
        }

        this.setTitle("FILE CHAT "+(sv!=null ? "-SERVER-":"")+"    ( "+name+"  IP : "+ip+" : "+port+"     /     Your Upload Port :"+uploadPort+" )");
    }

    /**
     * Asks if there is a connection with yes no confirm dialog.
     * If user selects yes disconnects the connection and returns true.
     * If user selects no does noting and returns false.
     * If there is no connection returns true.
     * @return 
     */
    protected boolean askAndDisconnect()
    {
        int selection = JOptionPane.NO_OPTION;
        if(this.sv != null || this.cl != null)
            selection = JOptionPane.showConfirmDialog(this, "Are You Sure ?","Disconnecting..",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        else return true;
            
        if(selection == JOptionPane.YES_OPTION)
        {
            disconnect();
            return true;
        }
        return false;
            
    }
    
    /**
     * Disconnects the all connections.
     * Sockets, FileServer etc.
     */
    public void disconnect()
    {
        
        try {
            isRemoteConnection = false;
            if(fileSv != null)
            {
                fileSv.close();
                fileSv = null;
            }
            
            
            dlViewList.clear();
            pnl_DownUPView.removeAll();
            pnl_DownUPView.repaint();
            refreshFileTable();
            
            if(this.sv != null)
            {
                this.sv.closeServer();
                this.sv = null;
                
                if(svCreater != null)
                    svCreater.isServerCreated = false;
            }
            
            if(this.cl != null)
            {
                this.cl.closeClient();
                this.cl = null;
                
                if(createNewClientGui != null)
                    createNewClientGui.setup();
            }
            
            System.out.println("Sending disconnect message... \nBye Bye ...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //--------------------------------------------------------------------------
    
    
    
    
    
    //--------------FILE DOWNLOAD & UPLOAD CONTROL METHODS----------------------
    //--------------------------------------------------------------------------

    /**
     * Sends upload request to file owner.
     * If there is already a request to this shared file it will do nothing.
     * If its own shared file it will do nothing.
     */
    public void downloadSharedFile(SharedFile selectedFile)
    {
        if(!fileSv.mySharedFiles.contains(selectedFile))
        {
            System.out.println("Action Download : "+selectedFile);
            boolean hasDownload = false;

            if(fileSv.downloader.downloadArray != null && fileSv.downloader.downloadArray.length > 0)
            for (int i = 0; i < fileSv.downloader.downloadArray.length; i++)
            {
                if(fileSv.downloader.downloadArray[i].equals(selectedFile))
                {
                    hasDownload = true;
                }
            }

            if(!hasDownload)
            {
               sendUploadRequest(selectedFile);
            }
        }
    }
    
    /**Asks and deletes selected shared file on table.*/
    private void deleteSelectedSharedFile()
    {
        
        SharedFile[] selectedFiles = getSelectedSharedFiles();
        if(selectedFiles != null)
        {
           SharedFile shFile = selectedFiles[0];
            if(selectedFiles.length > 0 && fileSv.mySharedFiles.contains(shFile))
            {
                int selection = JOptionPane.showConfirmDialog(this,
                                                                "Do you want to remove : "+selectedFiles[0].getName(),
                                                                "Removing shared file..",
                                                                JOptionPane.OK_CANCEL_OPTION,
                                                                JOptionPane.WARNING_MESSAGE);
                if(selection == JOptionPane.OK_OPTION)
                {


                    if(FileChat.monitor.sv != null)
                    {
                        FileChat.monitor.sv.sendSystemMessageToAll(USER_REQUEST_TYPE_REMOVE_SHAREDFILE,""+shFile.getId());
                        System.out.println("Sended delete file request by server : "+shFile.getId());

                    }else if(FileChat.monitor.cl != null)
                    {
                        FileChat.monitor.cl.sendRequestMessage(USER_REQUEST_TYPE_REMOVE_SHAREDFILE, ""+shFile.getId());
                        System.out.println("Sended delete file request by client :"+""+shFile.getId());

                    }


                    fileSv.deleteSharedFile(""+selectedFiles[0].getId());
                    refreshFileTable();
                }
            } 
        }
        
    }

    /**Returns all selected files on shared file table.*/
    private SharedFile[] getSelectedSharedFiles()
    {
        if(tbl_SharedFileTable.getSelectedRowCount() >  0)
        {
            SharedFile[] sharedFiles = new SharedFile[tbl_SharedFileTable.getSelectedRowCount()];
            int count = 0;
            int[] selectedRows = tbl_SharedFileTable.getSelectedRows();
            for (int index : selectedRows) {
               sharedFiles[count] = getSharedFile(index);
               count++;
            }
            return sharedFiles;
        }
        return null;
    }

    /**Returns shared file on table with given index.*/
    private SharedFile getSharedFile(int index)
    {
        if(dtm.getRowCount()>0 && index > -1 && index < dtm.getRowCount())
        {
            Object[] tblObj = new Object[] {
                                            dtm.getValueAt(index, 0),
                                            dtm.getValueAt(index, 1),
                                            dtm.getValueAt(index, 2),
                                            dtm.getValueAt(index, 3),
                                            dtm.getValueAt(index, 4),
                                            dtm.getValueAt(index, 5)};

            
            for (SharedFile sharedFile : fileSv.getSharedFileList()) {
                if(Arrays.toString(sharedFile.toTableRow()).equals(Arrays.toString(tblObj)))
                {
                    System.out.println("Get selected file id : "+sharedFile.getId());
                    return sharedFile;
                }
            }
        }
        return null;
    }
    
    /**Refreshes the shared file table.
    *It uses shared file list to refresh. Not connects the server to get list.*/
    public void refreshFileTable()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dtm.setRowCount(0);
                if(fileSv != null)
                    try {
                        for (SharedFile shFile : fileSv.getSharedFileList()) {
                            System.out.println("shared file to row "+shFile.toString());
                            dtm.addRow(shFile.toTableRow());


                        }
                    } catch (ConcurrentModificationException e) {
                        refreshFileTable();
                        return;
                    }
            }
        });
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int count = 1;
                while(count < 31)
                {
                    try {
                        btn_RefreshFileTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/loading/loading"+count+".png")));
                        Thread.sleep(5L);
                        count++;
                    }
                    catch (InterruptedException ex) {
                        Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                btn_RefreshFileTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/loading/loading1.png")));
            }
        }).start();
    }

    /**Sends upload request to file owner and adds a download viewer for new download*/
    public void sendUploadRequest(SharedFile shFile)
    {
        String id = ""+shFile.getId();
        String name = null;
        String ip = null;
        String port = fileSv.getFileServerSendPort();
        newDlView(shFile);
        
        try {
            if(this.sv != null && !this.sv.sName.equals(shFile.getOwnerNick()))
            {
                name = this.sv.sName;
                ip = this.sv.sIp;
                String requestMessage = name+":"+ip+":"+port+SYSTEM_MESSAGE_BRACKET+id;
                for (ClientConnection cConn : this.sv.clientList) {
                    System.out.println(cConn.cName+ " "+shFile.getOwnerNick()+" "+cConn.cIp+ " "+shFile.getOwnerIp());
                    if(cConn.cName.equals(shFile.getOwnerNick()) && cConn.cIp.equals(shFile.getOwnerIp()))
                    {
                        System.out.println(this.sv.sName+" sending upload request : "+requestMessage);
                        cConn.sendMessage(USER_REQUEST_UPLOAD+requestMessage);
                    }
                }
            }
            else if(this.cl != null && !this.cl.cName.equals(shFile.getOwnerNick()))
            {
                name = this.cl.cName;
                ip = this.cl.cIp;
                String requestMessage = name+":"+ip+":"+port+SYSTEM_MESSAGE_BRACKET+id;
                System.out.println(this.cl.cName+" sending upload request : "+requestMessage);
                this.cl.sendRequestMessage(USER_REQUEST_TYPE_UPLOAD, requestMessage);
            }
            fileSv.downloader.newDownload(shFile);
            showDlViews();
            
            this.repaint();
            System.out.println("dlview index :"+dlViewList.indexOf(new DownloadViewer(shFile)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Sends download request to downloader to learn the downloader still wanting the download.*/
    public void sendDownloadRequest(String name,String ip,String port,String fileId)
    {
        
        //SharedFile shFile = fileSv.getSharedFile(Integer.parseInt(fileId));
        if(this.sv != null)
        {
            String requestMessage = name+":"+ip+":"+port+SYSTEM_MESSAGE_BRACKET+fileId;
            try {
                
                for (ClientConnection cConn : this.sv.clientList) {
                System.out.println("Send download request :"+name +" : "+cConn.cName+"  "+ip+" : "+cConn.cIp);
                    if(cConn.cName.equals(name) && cConn.cIp.equals(ip))
                    {
                        System.out.println("Sending download request : "+requestMessage);
                        cConn.sendMessage(USER_REQUEST_DOWNLOAD+requestMessage);
                        break;
                    }
                }
                
            
            }
            catch (Exception ex) {ex.printStackTrace();}
        }else if(this.cl != null)
        {
            try {
                String requestMessage = name+":"+ip+":"+port+SYSTEM_MESSAGE_BRACKET+fileId;
                System.out.println("Sending download request : "+requestMessage);
                this.cl.sendRequestMessage(USER_REQUEST_TYPE_DOWNLOAD, requestMessage);
                
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
    /**Sends a boolean to shared file owner about download still be wanted.*/
    public void sendAcceptDownload(SharedFile shFile)//Download kabul bildir ve downloadı başlat.
    {
        System.out.println("searching send accep download for :"+shFile);
        String id = ""+shFile.getId();
        String name = fileSv.getFileServerOwner();
        String ip = fileSv.getFileServerIp();
        String port = fileSv.getFileServerSendPort();
        boolean isAccepted = fileSv.downloader.downloadAcceptted(shFile);
        String requestMessage = name+":"+ip+":"+port+SYSTEM_MESSAGE_BRACKET+id+SYSTEM_MESSAGE_BRACKET+isAccepted;


        try {
            if(this.sv != null )
            {
                for (ClientConnection cConn : this.sv.clientList) {
                    if(cConn.cName.equals(shFile.getOwnerNick()) && cConn.cIp.equals(shFile.getOwnerIp()))
                    {
                        System.out.println("Sending accept download :"+requestMessage);     
                        cConn.sendMessage(USER_REQUEST_DOWNLOAD_ISACCEPTED+requestMessage);
                    }
                }
            }else if(this.cl != null )
            {
                System.out.println("Sending accept download :"+requestMessage);
                this.cl.sendRequestMessage(USER_REQUEST_TYPE_DOWNLOAD_ISACCEPTED, requestMessage);
            }
            if(isAccepted)//Eğer hala indirilebilirse download başlatılır.
            {
                System.out.println("Starting download :"+shFile);
                fileSv.downloader.startDownload(shFile,getDlView(shFile));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**If downloader accepted the download this metod will be run. And then upload will begin if its not started yet.
    *If its not accepted upload will be canceled.*/
    public boolean acceptUpload(String senderName,String senderIp, String senderDownloadPort,String fileId,boolean isAccepted)
    {
        if(isAccepted)
        {
            if(uploadThread != null)
            {
                fileSv.uploader.isUploadAccepted = true;
                //uploadThread.interrupt();
            }
        }else
        {
            if(uploadThread != null)
            {
                fileSv.uploader.isUploadAccepted = false;
                //uploadThread.interrupt();
            }
        }
        isAnswered = true;
        System.out.println("Upload Accepted : " + fileSv.uploader.isUploadAccepted);
        return isAccepted;
    }
    /**If a user wants a file this method will add new downloaders information to list and starts the upload controller.*/
    public void uploadRequest(String senderName,String senderIp, String senderDownloadPort,String fileId)
    {
        SharedFile shFile = fileSv.getSharedFile(Integer.parseInt(fileId));
        System.out.println("upload request from :"+senderName+" "+shFile);
        fileSv.uploader.newUpload(shFile, senderName, senderIp, senderDownloadPort);
        
        uploadThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                startUploadController();
            }
        });
        
        if(!fileSv.uploader.isUploading)
        {
            fileSv.uploader.isUploading = true;
            uploadThread.start();
        }
            
    }
    /** Gets download request from file owner to start download. Sends system message about if its still wanting or not*/
    public void downloadRequest(String senderName,String senderIp, String senderDownloadPort,String fileId)
    {
        SharedFile shFile = fileSv.getSharedFile(Integer.parseInt(fileId));
        System.out.println("download request : "+shFile);
        sendAcceptDownload(shFile);
    }
    
    
    
    
    /* Fundemental Upload - Download algorithm.
    downloader
    file download
    getted confirmation
    start download
    ------------
    uploader
    file request
    sended confirmation
    getted confirmation
    start upload
    ----------
    1.)upload ->
    2.)upload <-
    2.)download ->
    1.)download <-
    1.)download confirmation -> open
    2.)download confirmation <-
    2.)start
    */

    
    /**It starts the upload file.
     * Uploads all files on queue and then ends.
     * Sends download request to downloader when its queue is came. If user sends accept download upload will be begin.
     * It waits 45 second to get accept message if there is no message in this time current upload will be canceled and upload controller passes to next upload.
     */
    public void startUploadController()
    {
        System.out.println("UPLOAD-CONTROLLER-STARTED");
                
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                
                int index = 0;
                while(index < fileSv.uploader.uploadArray.length)
                {
                    if(fileSv.uploader.uploadArray[index] != null)
                    {
                        fileSv.uploader.isUploadAccepted = false;
                        String name = fileSv.uploader.getUploadPersonDownloaderName(fileSv.uploader.uploadArray[index]),
                                            ip = fileSv.uploader.getUploadPersonDownloaderIp(fileSv.uploader.uploadArray[index]),
                                            port = fileSv.uploader.getUploadPersonDownloaderPort(fileSv.uploader.uploadArray[index]);
                        SharedFile shFile = fileSv.getSharedFile(Integer.parseInt(fileSv.uploader.getUploadPersonSharedFileId(fileSv.uploader.uploadArray[index])));
                        try {
                            sendDownloadRequest(name, ip, port, ""+shFile.getId());
                                int counter = 0;
                                while(!isAnswered && counter < 45)
                                {
                                    Thread.sleep(1000L);
                                    counter++;
                                }
                                isAnswered = false;
                                if(!fileSv.uploader.isUploadAccepted)
                                {
                                    fileSv.uploader.removeUpload(index);
                                    index++;
                                    continue;
                                }else
                                {
                                    System.out.println("Upload Starting : "+fileSv.uploader.uploadArray[index]);
                                    fileSv.uploader.startUpload(name, ip, port,shFile,prg_upload_curr,prg_upload_all);
                                }
                        }
                        catch (Exception ex) {System.err.println("Error on running upload controller : "+ex.getMessage());ex.printStackTrace();}
                    }
                    index++;
                }
                fileSv.uploader.isUploading = false;
                
            }
        }).start();
    }

    
    
    //------------------------DOWNLOAD VIEWER CONTROL METHODS-------------------
    //--------------------------------------------------------------------------
    /**
     * Adds the created downloadViewer instances to the screen by using downloadViewerList
     * If the instance is added before it will do nothing.
     */
    public void showDlViews()
    {
        try {
            for (DownloadViewer downloadViewer : dlViewList) {
                
                //if(downloadViewer.getParent() == null)
                    pnl_DownUPView.add(downloadViewer,cons_pnl_DownUp_View);
            }
            
        }
        catch (ConcurrentModificationException ex)
        {
            showDlViews();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**Creates a new DownloadViewer container for given shared file and adds it to list.
     * 
     * @param file 
     */
    public void newDlView(SharedFile file)//Yeni dlView oluşturur. eğer önceden indirilmiş bir dosya ise aynı viewi yenileyip kullanır.
    {
        DownloadViewer view = null;
        if((view = getDlView(file)) != null)
        {
            view.setup();
        }else
        {
            dlViewList.add(new DownloadViewer(file));
        }
        
    }
    
    /**
     * Returns the downloadViewer of given shared file.
     * There will be 1 downloadViewer for 1 shared file always.
     * @param sh
     * @return DownloadViewer of sharedFile
     */
    public DownloadViewer getDlView(SharedFile sh)
    {
        try {
                if(dlViewList == null)
                return null;

            for (DownloadViewer downloadViewer : dlViewList) {
                if(downloadViewer.downFile.equals(sh))
                {
                    System.out.println("Returned downloadView :"+downloadViewer);
                    return downloadViewer;
                }

            }
        }
        catch (Exception e) {
            if(e.getMessage().toLowerCase().contains("current modification"))
            {
                return getDlView(sh);
            }
            e.printStackTrace();
        }
        
        
        return null;
    }
    /**
     * Removes DownloadViewer from list and refreshes the downloadViewer panel.
     * @param view 
     */
    public void removeDlViewFromList(DownloadViewer view)
    {
        try {
            if(view != null)
            {
                dlViewList.remove(view);
            }
        }
        catch (Exception e) {
            if(e.getMessage().toLowerCase().contains("current modification"))
            {
                removeDlViewFromList(view);
            }
            e.printStackTrace();
        }
        
        showDlViews();
        
    }
    //--------------------------------------------------------------------------
    
    
    //-----------------File Chat Options Getters and Setters--------------------
    //--------------------------------------------------------------------------
    /**
     * @return Wroten chat message. 
     */
    public String getNewMessage()
    {
        return txtArea_NewNessage.getText();
    }

    public boolean isOverRideFiles()
    {
        return isOverRideFiles;
    }

    public void setIsOverRideFiles(boolean isOverRideFiles)
    {
        this.isOverRideFiles = isOverRideFiles;
    }

    public boolean isAutomaticDownload()
    {
        return isAutomaticDownload;
    }

    public void setIsAutomaticDownload(boolean isAutomaticDownload)
    {
        this.isAutomaticDownload = isAutomaticDownload;
    }

    public int getDownloadSpeedLimit()
    {
        return downloadSpeedLimit;
    }

    public void setDownloadSpeedLimit(int downloadSpeedLimit)
    {
        this.downloadSpeedLimit = downloadSpeedLimit;
    }

    public int getUploadSpeedLimit()
    {
        return uploadSpeedLimit;
    }

    public void setUploadSpeedLimit(int uploadSpeedLimit)
    {
        this.uploadSpeedLimit = uploadSpeedLimit;
    }

    /**
     * Returns current time with format (HH:mm:ss)
     * @return 
     */
    public String getTimeText()
    {
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        return (sdf.format(cal.getTime())+" ");
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddNewFile;
    private javax.swing.JButton btn_DeleteFile;
    private javax.swing.JButton btn_DownloadFile;
    private javax.swing.JButton btn_EditFile;
    private javax.swing.JButton btn_RefreshFileTable;
    private javax.swing.JButton btn_SelectAll;
    private javax.swing.JComboBox<String> cmb_SearchType;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JList<String> list_OnlineUser;
    private javax.swing.JCheckBoxMenuItem menuItem_AutomaticDownloadFies;
    private javax.swing.JMenuItem menuItem_ChangeDlFolder;
    private javax.swing.JMenuItem menuItem_ChangeFont;
    private javax.swing.JMenuItem menuItem_ChangeName;
    private javax.swing.JMenuItem menuItem_CreateRoom;
    private javax.swing.JMenu menuItem_EditMenu;
    private javax.swing.JMenuItem menuItem_LimitDownload;
    private javax.swing.JMenuItem menuItem_LımıtUpload;
    private javax.swing.JMenuItem menuItem_OpenDownloadFolder;
    private javax.swing.JCheckBoxMenuItem menuItem_OverrideDownloadedFiles;
    private javax.swing.JMenuItem menuItem_closeConnection;
    private javax.swing.JPanel pnl_Chat;
    private javax.swing.JPanel pnl_DownUPView;
    private javax.swing.JPanel pnl_SharedFiles;
    private javax.swing.JProgressBar prg_upload_all;
    private javax.swing.JProgressBar prg_upload_curr;
    private javax.swing.JButton sndButton;
    private javax.swing.JTable tbl_SharedFileTable;
    private javax.swing.JTextArea txtArea_MessageStore;
    private javax.swing.JTextArea txtArea_NewNessage;
    private javax.swing.JTextField txt_SharedFileSearchText;
    // End of variables declaration//GEN-END:variables

}