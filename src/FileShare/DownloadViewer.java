package FileShare;

import Chat.FileChat;
import Chat.SharedFile;
import Chat.Tool;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;


/**
 *This class creates a GUI to give download control and view dynamic information about download to the user.
 * @author Burak
 */
public class DownloadViewer extends javax.swing.JPanel implements Observer
{

    Download dl = null;
    HashMap<String,Boolean> downloadedMap = null; // file list to download.
    long startTime = 0; // downloads start time in milisecond.
    
    /**
     * Creates new form DownloadViewer
     */
    public SharedFile downFile = null;
    String myFileParentPath = null;
    
    
    public DownloadViewer(SharedFile shFile)
    {
        downFile = shFile;
        myFileParentPath = DownloadPropertiyTools.getNewParentDownloadFolderPath(downFile.getName());
        initComponents();
        setup();
        list_FileInfoList.setCellRenderer(new SelectedListCellRenderer());
    }
    
    /**
     * Refreshes or creates the download objects of GUI.
     */
    public void setup()
    {
        setShareInfo("SHARE NAME : "+downFile.getName()+" ["+downFile.getOwnerNick()+"]");
        setNotification(downFile.getNotification());
        myFileParentPath = DownloadPropertiyTools.getNewParentDownloadFolderPath(downFile.getName());
        if(downloadedMap != null)
        {
            downloadedMap.clear();
        }
        downloadedMap = new HashMap<>();
        DefaultListModel dlm = new DefaultListModel();
        
        for (File f : downFile.getFileList()) {
            
            f = DownloadPropertiyTools.getNewPath(f, myFileParentPath, downFile.getParentDirectoryPath());
            
            String elem = fileToListItem(f);
            dlm.addElement(elem);
            downloadedMap.put(elem, false);
        }
        
        list_FileInfoList.setModel(dlm);
        
        prg_All_Rate = 0;
        prg_curr_rate=0;
        prg_All_value=0;
        prg_curr_value=0;
        currDownSize = 0L;
        lastFile = null;
        prg_ALL.setValue(0);
        prg_Current.setValue(0);
        prg_ALL.setString("Waiting...");
        prg_Current.setString("Waiting...");
        
        btn_AbortProgress.setEnabled(true);
        
    }
    
    /**
     * Converts files path to list view.
     * Simple : File.Path C:/Users/user/Desktop/a/1.txt -> String /a/1.txt
     * (If there will be inner directories after parent download folder they will be added to string too.)
     * @param f
     * @return minimal pathString for JList.
     */
    private String fileToListItem(File f)
    {
        String listItem = f.getAbsolutePath().toString().substring(myFileParentPath.length()-1);
        //System.out.println("fileToListItem :"+f.getAbsolutePath()+" : "+listItem);
        return listItem;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Info = new javax.swing.JPanel();
        lbl_ShareInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Notification = new javax.swing.JTextArea();
        prg_ALL = new javax.swing.JProgressBar();
        prg_Current = new javax.swing.JProgressBar();
        pnl_progress = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_FileInfoList = new javax.swing.JList<>();
        pnl_control = new javax.swing.JPanel();
        btn_AbortProgress = new javax.swing.JButton();
        btn_OpenDownloadFile = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 204));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setForeground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(32767, 140));
        setMinimumSize(new java.awt.Dimension(450, 140));
        setPreferredSize(new java.awt.Dimension(550, 140));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });

        pnl_Info.setBackground(new java.awt.Color(255, 255, 255));
        pnl_Info.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        pnl_Info.setForeground(new java.awt.Color(255, 255, 255));

        txt_Notification.setEditable(false);
        txt_Notification.setColumns(20);
        txt_Notification.setLineWrap(true);
        txt_Notification.setRows(5);
        txt_Notification.setWrapStyleWord(true);
        txt_Notification.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txt_Notification.setPreferredSize(null);
        jScrollPane1.setViewportView(txt_Notification);

        prg_ALL.setBackground(new java.awt.Color(255, 255, 255));
        prg_ALL.setForeground(new java.awt.Color(51, 51, 51));
        prg_ALL.setMaximum(1000);
        prg_ALL.setString("Waiting...");
        prg_ALL.setStringPainted(true);

        prg_Current.setBackground(new java.awt.Color(255, 255, 255));
        prg_Current.setForeground(new java.awt.Color(51, 51, 51));
        prg_Current.setMaximum(1000);
        prg_Current.setToolTipText("<html>");
        prg_Current.setString("Waiting...");
        prg_Current.setStringPainted(true);

        javax.swing.GroupLayout pnl_InfoLayout = new javax.swing.GroupLayout(pnl_Info);
        pnl_Info.setLayout(pnl_InfoLayout);
        pnl_InfoLayout.setHorizontalGroup(
            pnl_InfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_ShareInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(prg_ALL, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(prg_Current, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnl_InfoLayout.setVerticalGroup(
            pnl_InfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnl_InfoLayout.createSequentialGroup()
                .addComponent(lbl_ShareInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(prg_ALL, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(prg_Current, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pnl_progress.setBackground(new java.awt.Color(255, 255, 255));
        pnl_progress.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        pnl_progress.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setMinimumSize(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(100, 130));

        list_FileInfoList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list_FileInfoList.setToolTipText("Double click to Open file if it's downloaded.");
        list_FileInfoList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        list_FileInfoList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list_FileInfoListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(list_FileInfoList);

        javax.swing.GroupLayout pnl_progressLayout = new javax.swing.GroupLayout(pnl_progress);
        pnl_progress.setLayout(pnl_progressLayout);
        pnl_progressLayout.setHorizontalGroup(
            pnl_progressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
        );
        pnl_progressLayout.setVerticalGroup(
            pnl_progressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnl_control.setBackground(new java.awt.Color(255, 255, 255));
        pnl_control.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btn_AbortProgress.setBackground(new java.awt.Color(255, 255, 255));
        btn_AbortProgress.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btn_AbortProgress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cancel2.png"))); // NOI18N
        btn_AbortProgress.setToolTipText("STOP DOWNLOAD !");
        btn_AbortProgress.setMaximumSize(new java.awt.Dimension(50, 40));
        btn_AbortProgress.setMinimumSize(new java.awt.Dimension(50, 40));
        btn_AbortProgress.setPreferredSize(null);
        btn_AbortProgress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_AbortProgressMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_AbortProgressMouseExited(evt);
            }
        });
        btn_AbortProgress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbortProgressActionPerformed(evt);
            }
        });

        btn_OpenDownloadFile.setBackground(new java.awt.Color(255, 255, 255));
        btn_OpenDownloadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/openFile.png"))); // NOI18N
        btn_OpenDownloadFile.setToolTipText("Open download folder");
        btn_OpenDownloadFile.setPreferredSize(null);
        btn_OpenDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_OpenDownloadFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_controlLayout = new javax.swing.GroupLayout(pnl_control);
        pnl_control.setLayout(pnl_controlLayout);
        pnl_controlLayout.setHorizontalGroup(
            pnl_controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_controlLayout.createSequentialGroup()
                .addComponent(btn_OpenDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(btn_AbortProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnl_controlLayout.setVerticalGroup(
            pnl_controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_controlLayout.createSequentialGroup()
                .addComponent(btn_AbortProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_OpenDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Info, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_control, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnl_control, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_progress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_Info, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Cancels the download.
     * If download is began it will end directly and current downloading file will be deleted.
     * @param evt 
     */
    private void btn_AbortProgressActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_AbortProgressActionPerformed
    {//GEN-HEADEREND:event_btn_AbortProgressActionPerformed
        int selection = JOptionPane.showConfirmDialog(this, "Do you want to cancel the download ?","Warning !",JOptionPane.YES_NO_OPTION);
        if(selection == JOptionPane.YES_OPTION)
        {
           if(dl != null)
            {
                dl.close();
                btn_AbortProgress.setEnabled(false);
            }else
           {
               FileChat.monitor.fileSv.downloader.removeDownload(downFile);
               prg_ALL.setString("Canceled !");
               prg_Current.setString("");
               btn_AbortProgress.setEnabled(false);
           }
        }
    }//GEN-LAST:event_btn_AbortProgressActionPerformed

    /**
     * Opens This downloads parent folder on system explorer.
     * @param evt 
     */
    private void btn_OpenDownloadFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_OpenDownloadFileActionPerformed
    {//GEN-HEADEREND:event_btn_OpenDownloadFileActionPerformed
        try {
            if(dl != null)
                Desktop.getDesktop().open(new File(dl.myFileParentPath));
        }
        catch (IOException ex) {
            Logger.getLogger(DownloadViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_OpenDownloadFileActionPerformed

    // Color operations for mouse track.
    private void formMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseEntered
    {//GEN-HEADEREND:event_formMouseEntered
        this.setBackground(new Color(51,153,255));
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseExited
    {//GEN-HEADEREND:event_formMouseExited
        this.setBackground(Color.WHITE);
    }//GEN-LAST:event_formMouseExited

    private void btn_AbortProgressMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_AbortProgressMouseEntered
    {//GEN-HEADEREND:event_btn_AbortProgressMouseEntered
        btn_AbortProgress.setBackground(new Color(150,14,14));
    }//GEN-LAST:event_btn_AbortProgressMouseEntered

    private void btn_AbortProgressMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_btn_AbortProgressMouseExited
    {//GEN-HEADEREND:event_btn_AbortProgressMouseExited
        btn_AbortProgress.setBackground(Color.WHITE);
    }//GEN-LAST:event_btn_AbortProgressMouseExited

    /**
     *  Opens selected file from jList on system explorer.
     * @param evt 
     */
    private void list_FileInfoListMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_list_FileInfoListMouseClicked
    {//GEN-HEADEREND:event_list_FileInfoListMouseClicked
        if(evt.getClickCount() == 2 && list_FileInfoList.getSelectedIndex() != -1)
        {
            if(dl != null)
            {
                try {
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                Desktop.getDesktop().open(new File(dl.myFileParentPath+list_FileInfoList.getSelectedValue().substring(1)));
                                System.out.println("Opening file :"+dl.myFileParentPath+list_FileInfoList.getSelectedValue().substring(1));
                            }
                            catch (IOException ex) {
                                Logger.getLogger(DownloadViewer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }).start();
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "File not downloaded !","Error !",JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_list_FileInfoListMouseClicked

    
    public void setShareInfo(String info)
    {
        lbl_ShareInfo.setText(info);
    }
    public void setNotification(String notification)
    {
        txt_Notification.setText(notification);
    }
    
    public void setList(DefaultListModel dlm)
    {
        list_FileInfoList.setModel(dlm);
    }
    /**
     * It sets download instance and starts schedule tasks for lists dynamic refresh and download speed track.
     * @param dl 
     */
    public void setDownload(Download dl)
    {
        this.dl = dl;
        dl.addObserver(this);
        startTime = System.currentTimeMillis();
        
        
        Timer zamanlayici = new Timer();
        TimerTask dlSpeedCalculator = new TimerTask()
        {
            @Override
            public void run()
            {
                if(downloadCalculator > 0)
                {
                    downloadSpeed = downloadCalculator;
                    downloadCalculator = 0;
                    
                }
            }
        };
        
        zamanlayici.schedule(dlSpeedCalculator, 0, 1000);
        
        TimerTask updateListView = new TimerTask()
        {
            @Override
            public void run()
            {
                list_FileInfoList.repaint();
            }
        };
        
        zamanlayici.schedule(updateListView, 0, 125);
        
        
    }
    

    @Override
    public boolean equals(Object obj)
    {
        
        if(obj instanceof SharedFile)
            if(downFile.equals((SharedFile)obj))
                return true;
        
        if(obj instanceof DownloadViewer)
        if(downFile.equals(((DownloadViewer)obj).downFile))
            return true;
        
        return false;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AbortProgress;
    private javax.swing.JButton btn_OpenDownloadFile;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_ShareInfo;
    private javax.swing.JList<String> list_FileInfoList;
    private javax.swing.JPanel pnl_Info;
    private javax.swing.JPanel pnl_control;
    private javax.swing.JPanel pnl_progress;
    private javax.swing.JProgressBar prg_ALL;
    private javax.swing.JProgressBar prg_Current;
    private javax.swing.JTextArea txt_Notification;
    // End of variables declaration//GEN-END:variables

    double prg_All_Rate = 0,prg_curr_rate=0;
    long prg_All_value=0,prg_curr_value=0;
    long currDownSize = 0L;
    File lastFile = null;
    int downloadSpeed = 0,downloadCalculator = 0;
    /**
     * This method dynamically shows download info.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg)
    {
        
        if(dl.instantReadSize > 0 )
        {
            downloadCalculator += dl.instantReadSize;
            
            //System.out.println(prg_All_value+" : size :"+dl.shFile.getFileSize());
            if(dl.currFileSize != currDownSize && dl.currFileSize != -2)//True when this update is for a new file.
            {
                prg_curr_rate = 0;
                prg_curr_value = 0;
                lastFile = dl.myFile;
                currDownSize = dl.currFileSize;
                
                //System.out.println("Checking file if is downloaded :"+lastFile.canRead()+" "+lastFile.getAbsolutePath().toString().substring(dl.myFileParentPath.length()));
                if(lastFile != null && lastFile.canRead())//If file is downloaded its state will be changed.
                {
                    downloadedMap.put(fileToListItem(lastFile), true);
                }
                prg_Current.setToolTipText(dl.myFile.getAbsolutePath());
                
                
            }
            
            
            //Writes updated values to JProgressBars.
            prg_ALL.setValue((int) prg_All_Rate);
            prg_ALL.setString(Tool.byteToMb(prg_All_value) + " (mb) / "+Tool.byteToMb(Long.parseLong(dl.shFile.getFileSize()))+" (mb)");
            
            prg_Current.setValue((int)prg_curr_rate);
            
            prg_Current.setString(Tool.byteToMb(prg_curr_value)+" (mb) / "+Tool.byteToMb(currDownSize)+" (mb)\t\t\t  "+(downloadSpeed/1024)+" (kb/s)");
            //System.out.println("new Value  : "+(int) (prg_ALL.getValue()+((double)prg_ALL.getMaximum()*downRate))+" prg val :"+prg_ALL.getValue());
            
            
            
            //-----Defines the download rate and incrases the download progress value.
            
            double downRate = (double)dl.instantReadSize/(double)Long.parseLong(dl.shFile.getFileSize());
            prg_All_value+=dl.instantReadSize;
            //System.out.println("all value :"+prg_All_value+" all rate :"+prg_All_Rate+" anlik okuma :"+dl.instantReadSize);
            prg_All_Rate += ((double)prg_ALL.getMaximum()*downRate);
            
            
            double curDownRate = (double)dl.instantReadSize/(double)dl.currFileSize;
            prg_curr_value+=dl.instantReadSize;
            prg_curr_rate += ((double)prg_Current.getMaximum()*curDownRate);
            
        }else if(dl.currFileSize == -2) // True if download is finished successfuly.
        {
            prg_Current.setString("DOWNLOAD COMPLETE ! ( Time :"+Tool.getTimeString(startTime, System.currentTimeMillis())+")");
            btn_AbortProgress.setEnabled(false);
            downloadSpeed = 0;
        }
    }
    
    
    
    
    
    
    
    /**
     * This class changes the name of downloaded files on list view.
     * If file is canReadible it will be green
     * If its not it will be red backgrounded.
     */
    class SelectedListCellRenderer extends DefaultListCellRenderer {
     @Override
     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         if(downloadedMap.get(""+value))
         {
             c.setBackground(new Color(200,242,187));
         }else
         {
             c.setBackground(new Color(252,194,194));
         }
         
         return c;
     }
    }
}
