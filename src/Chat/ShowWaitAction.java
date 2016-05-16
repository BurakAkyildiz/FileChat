package Chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;    
import javax.swing.*;

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
   public ShowWaitAction(String name,Container container,String title, String message,Runnable runMe) {
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
      SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
         @Override
         protected Void doInBackground() {
            if(runMe == null)
                 return null;
            
            runMe.run();
            
            return null;
         }
      };

      final JDialog dialog = new JDialog((Window)container, title, ModalityType.APPLICATION_MODAL);

      mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

         @Override
         public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("state")) {
               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                  dialog.dispose();
               }
            }
         }
      });
      mySwingWorker.execute();

      JProgressBar progressBar = new JProgressBar();
      progressBar.setIndeterminate(true);
      
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(progressBar, BorderLayout.CENTER);
      
      JLabel label = new JLabel(message);
      label.setFont(new Font("verdana",Font.PLAIN,17));
      panel.add(label, BorderLayout.PAGE_START);
      
      dialog.add(panel);
      dialog.pack();
      dialog.setLocationRelativeTo(container);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      dialog.setResizable(false);
      dialog.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
      dialog.setVisible(true);
   }
}