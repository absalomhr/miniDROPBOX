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
public class ClienteGUI extends JFrame implements ActionListener {

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

        btn_show = new JButton("Show");
        btn_show.setBounds(250, 100, 150, 20);
        btn_show.addActionListener(this);
        this.add(btn_show);

        btn_download = new JButton("Download");
        btn_download.setBounds(350, 100, 150, 20);
        btn_download.addActionListener(this);
        this.add(btn_download);

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

    public void showFiles() {
        ClientDB c = new ClientDB();
        c.showFiles();
    }

    public void downloadFiles() {
        //Choosing client route
        JFileChooser jfc;
        File f = null;
        jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.isMultiSelectionEnabled()) {
            jfc.setMultiSelectionEnabled(false);
        }
        int r = jfc.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            f = jfc.getSelectedFile();
        }

        ClientDB c = new ClientDB();
        c.setClientRoute(f.getAbsolutePath());

    }

    public static void main(String[] args) {
        ClienteGUI cl = new ClienteGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_upload) {
            uploadFiles();
        } else if (e.getSource() == btn_show) {
            showFiles();
        } else if (e.getSource() == btn_download) {
            downloadFiles();
        }
    }
    private JButton btn_upload;
    private JButton btn_show;
    private JButton btn_download;
}
