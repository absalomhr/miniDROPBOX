package minidropbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFileChooser;

/**
 *
 * @author elpat
 */
public class ClientDB {

    private Socket s;
    private String host = "127.0.0.1";
    private int port = 1234;
    private DataOutputStream dos;
    private DataInputStream dis;

    public ClientDB() {
        try {
            s = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(File[] filesToSend) {
        try {
            dos = new DataOutputStream(s.getOutputStream());
            for (int i = 0; i < filesToSend.length; i++) {
                send(filesToSend[i], "", filesToSend[i].getName());
            }
            dos.writeChar(3);
            dos.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void download (){
        try{
            dos = new DataOutputStream(s.getOutputStream());
            recieveFiles ();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void send(File file, String parentDirectory, String name) {
        try {
            dos.writeChar(0);
            dos.flush();
            long size = file.length();
            dos.writeLong(size);
            dos.flush();
            dos.writeUTF(name);
            dos.flush();
            dos.writeUTF(parentDirectory);
            dos.flush();
            if (file.isDirectory()) {
                dos.writeChar('d');
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    size = f.length();
                    if (f.isDirectory()) {
                        System.out.println("\nSENDING DIR: ");
                        if (parentDirectory.equals("")) {
                            System.out.println("PARENT: " + f.getParentFile().getName() + " NAME: " + name);
                            send(f, f.getParentFile().getName(), f.getName());
                        } else {
                            System.out.println("PARENT: " + parentDirectory + " NAME: " + name);
                            send(f, parentDirectory + "\\" + name, f.getName());
                        }
                    } else if (f.isFile()) {
                        System.out.println("\nSENDING FILE RECURSIVE FROM INNER");
                        if (parentDirectory.equals("")) {
                            System.out.println("EQEMPTY : PARENT: " + f.getParentFile().getName() + " NAME: " + f.getName());
                            send(f, f.getParentFile().getName(), f.getName());
                        } else {
                            System.out.println("PARENT: " + parentDirectory + "\\" + name + " NAME: " + f.getName());
                            send(f, parentDirectory + "\\" + name, f.getName());
                        }
                    }
                }
            } else {
                System.out.println("\nACTUALLY SENDING FILE: NAME:" + name + " PARENT: " + parentDirectory);
                dos.writeChar('f');
                long sent = 0;
                int percent = 0, n = 0;
                dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                while (sent < size) {
                    byte[] b = new byte[1500];
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    sent += n;
                    percent = (int) ((sent * 100) / size);
                    System.out.print("\rSENT: " + percent + " %");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void recieveFiles() {
        String clientRoute = "";
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.isMultiSelectionEnabled()) {
            jfc.setMultiSelectionEnabled(false);
        }
        int l = jfc.showOpenDialog(null);

        if (l == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            clientRoute = f.getAbsolutePath();
        }
        
        if (!clientRoute.equals("")){
            try {
                dos.writeChar(1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        try {
            long size = dis.readLong();
            String name = dis.readUTF();
            String parent = dis.readUTF();
            char option = dis.readChar();

            if (option == 'd') {
                System.out.println("\nCREATING DIR: " + "NAME: " + name + " PARENT: " + parent);
                boolean bol = false;
                if (parent.equals("")) {
                    bol = new File(clientRoute + "\\" + name).mkdir();
                } else {
                    bol = new File(clientRoute + "\\" + parent + "\\" + name).mkdir();
                }
                if (bol) {
                    System.out.println("DIR CREATED: " + name);
                } else {
                    System.err.println("DIR COULDNT BE CREATED!");
                }
            } else {
                System.out.println("\nCREATING FILE: " + "NAME: " + name + " PARENT: " + parent);

                if (parent.equals("")) {
                    dos = new DataOutputStream((new FileOutputStream(clientRoute + "\\" + name)));
                    System.out.println("DESTINATARY: " + clientRoute + "\\" + name);
                } else {
                    dos = new DataOutputStream((new FileOutputStream(clientRoute + "\\" + parent + "\\" + name)));
                    System.out.println("DESTINATARY: " + clientRoute + "\\" + parent + "\\" + name);
                }

                long r = 0;
                int n = 0, percent = 0;
                while (r < size) {
                    byte[] b = new byte[1500];
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    r += n;
                    percent = (int) ((r * 100) / size);
                    System.out.print("\rRECEIVING: " + percent + "%");
                }
                dos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
