package minidropbox;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
//probando cambios


public class ClienteGUI extends JFrame implements ActionListener {
    ClientDB c;
    String path_download;
    public ClienteGUI() { 
        c = new ClientDB();
        init();
        path_download = "";
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
        btn_show.setBounds(350, 100, 150, 20);
        btn_show.addActionListener(this);
        this.add(btn_show);

        btn_download = new JButton("Download");
        btn_download.setBounds(550, 100, 150, 20);
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
            c = new ClientDB();
            c.upload(files);
            
        }
    }

    public void showFiles() {
        TreeFiles mytree= c.showFiles();
        JTree tree_swing = mytree.getTree();
        
        tree_swing.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
              DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                  .getPath().getLastPathComponent();
              System.out.println("You selected " + node);
              path_download = node.toString();
            }
            
        });
            
        /*JScrollPane scrollP = new JScrollPane(tree_swing);
        this.add(scrollP);
        scrollP.setBorder(BorderFactory.createEmptyBorder()); //How to remove the border of JScrollPane.
        scrollP.setPreferredSize(new Dimension(300, 230));
        this.setLayout(new FlowLayout());*/
        this.add(tree_swing);
        this.repaint();
        tree_swing.setBounds(200, 200, 500, 400);

        
    }

    public void downloadFiles(String path) {
        //Choosing client route
        File f = null;

        ClientDB c = new ClientDB();
        c.download(path);

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
            downloadFiles(path_download);
        }
    }
    private JButton btn_upload;
    private JButton btn_show;
    private JButton btn_download;
}
