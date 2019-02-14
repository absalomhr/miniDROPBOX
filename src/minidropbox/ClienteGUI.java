package minidropbox;

import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.*;
//probando cambios
/**
 *
 * @author elpat
 */
public class ClienteGUI extends JFrame implements ActionListener{

    public ClienteGUI() {
        init();
    }
    
    void init() {
       
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(0, 0, 1024, 768);
        this.setContentPane(new Container());
        this.setVisible(true);
        
         btn_upload = new JButton("Upload Files");
         btn_upload.setBounds(100, 100, 150, 20);
         btn_upload.addActionListener(this);
         this.add(btn_upload);
         
        
    }
    public void uploadFiles() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (!jfc.isMultiSelectionEnabled()) {
            jfc.setMultiSelectionEnabled(true);
        }
        int r = jfc.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            File[] files = jfc.getSelectedFiles();
            ClientDB c = new ClientDB();
            c.upload(files);
        }
    }
    public static void main(String[] args) {
        ClienteGUI cl = new ClienteGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource() == btn_upload) {
           uploadFiles();
       }
    }
    private JButton btn_upload;
}
