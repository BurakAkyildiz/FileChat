package Chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;    
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import static javax.swing.Action.NAME;
import javax.swing.plaf.ProgressBarUI;

/**
 * This class creates a wait dialog on defined window and does run function on background.
 * When run finishes wait dialog will be disposed.
 * @author Burak
 */
public class ShowWaitAction extends AbstractAction {
   Runnable runMe = null;
   Container container = null;
   public static boolean isContinue = true;
   String title = "", message = "";
   SwingWorker<Void, Void> mySwingWorker = null;
   public ShowWaitAction(String name,Container container, String message,Runnable runMe) {
      super(name);
      this.runMe = runMe;
      this.container = container;
      if(title != null)
          this.title = title;
      if(message != null)
          this.message = message;
   }

   @Override
   public void actionPerformed(ActionEvent evt) {
       mySwingWorker = new SwingWorker<Void, Void>(){
         @Override
         protected Void doInBackground() {
            if(runMe == null)
                 return null;
             try {
                 runMe.run();
             } catch (Exception e) {
                 e.printStackTrace();
             }
            
            mySwingWorker.firePropertyChange("state", SwingWorker.StateValue.DONE, SwingWorker.StateValue.STARTED);
            
            return null;
         }
      };

      final JDialog dialog = new JDialog((Window)container, title, ModalityType.APPLICATION_MODAL);

      mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

         @Override
         public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("state")) {
               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                  dialog.setVisible(false);
                  dialog.dispose();
               }
            }
         }
      });
      mySwingWorker.execute();

      
      JProgressBar progressBar = new JProgressBar();
      Dimension prog_dimens = new Dimension(12*message.length(),50);
      progressBar.setSize(prog_dimens);
      progressBar.setPreferredSize(prog_dimens);
      progressBar.setIndeterminate(true);
      progressBar.setStringPainted(true);
      progressBar.setString(NAME);
      progressBar.setFont(new Font("Verdana",Font.PLAIN,18));
      progressBar.setString(message);
      progressBar.setBorderPainted(true);
      
      
      BorderLayout layout = new BorderLayout();
      layout.setHgap(1);
      JPanel panel = new JPanel(layout);
      panel.add(progressBar, BorderLayout.CENTER);
      
      
      
      
      panel.setBackground(Color.WHITE);
      dialog.setUndecorated(true);
      dialog.add(panel);
      dialog.pack();
      dialog.setLocationRelativeTo(container);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      dialog.setResizable(false);
      dialog.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
      dialog.setVisible(true);
      
   }
}