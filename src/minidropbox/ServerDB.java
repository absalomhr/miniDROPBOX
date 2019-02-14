package minidropbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFileChooser;

/**
 *
 * @author elpat
 */
public class ServerDB {

    // Route where files are going to be stored on the serer side
    JFileChooser jfc = new JFileChooser();

    
    private static String serverRoute = "";

    DataOutputStream dos;
    DataInputStream dis;
    FilePath p;

    public ServerDB() {
        File f = null;
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.isMultiSelectionEnabled()) {
            jfc.setMultiSelectionEnabled(false);
        }
        int r = jfc.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            f = jfc.getSelectedFile();
        }
        serverRoute = f.getAbsolutePath();
        
        p = new FilePath();
        ServerSocket s;
        Socket cl;

        // Client can upload (0), download (1), exit (3), show (4)
        char clientRequest;
        try {
            s = new ServerSocket(1234);
            s.setReuseAddress(true);
            System.out.println("SERVER ON, WAITING FOR CLIENT CONNECTION");
            cl = s.accept();
            if (cl != null) {
                System.out.println("CONNECTED FROM: " + cl.getInetAddress() + " PORT: " + cl.getPort());
            }
            for (;;) {
                if (!cl.isConnected()) {
                    cl = s.accept();
                    System.out.println("CONNECTED FROM: " + cl.getInetAddress() + " PORT: " + cl.getPort());
                } else {
                    dis = new DataInputStream(cl.getInputStream());
                    clientRequest = dis.readChar();
                    // Client requests to upload:
                    if (clientRequest == 0) {
                        recieveFiles();
                    }
                    else if (clientRequest == 3){
                        for (int i = 0; i < p.getPaths().size(); i++) {
                            String k = p.getPaths().get(i).getKey();
                            String v = p.getPaths().get(i).getValue();
                            System.out.println("path: " + k + " type: " + v);
                        }
                        
                        cl.close();
                        break;
                    }
                    else if (clientRequest == 1){
                        
                        //send();
                    }
                    else if (clientRequest == 4){
                        
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recieveFiles() {
        try {
            long size = dis.readLong();
            String name = dis.readUTF();
            String parent = dis.readUTF();
            char option = dis.readChar();

            if (option == 'd') {
                System.out.println("\nCREATING DIR: " + "NAME: " + name + " PARENT: " + parent);
                boolean bol = false;
                if (parent.equals("")) {
                    bol = new File(serverRoute + "\\" + name).mkdir();
                    
                    p.insert(serverRoute + "\\" + name, "d");
                    
                } else {
                    bol = new File(serverRoute + "\\" + parent + "\\" + name).mkdir();
                    
                    p.insert(serverRoute + "\\" + name, "d");
                    
                }
                if (bol) {
                    System.out.println("DIR CREATED: " + name);
                } else {
                    System.err.println("DIR COULDNT BE CREATED!");
                }
            } else {
                System.out.println("\nCREATING FILE: " + "NAME: " + name + " PARENT: " + parent);

                if (parent.equals("")) {
                    dos = new DataOutputStream((new FileOutputStream(serverRoute + "\\" + name)));
                    
                    p.insert(serverRoute + "\\" + name, "f");
                    
                    System.out.println("DESTINATARY: " + serverRoute + "\\" + name);
                } else {
                    dos = new DataOutputStream((new FileOutputStream(serverRoute + "\\" + parent + "\\" + name)));
                    
                    p.insert(serverRoute + "\\" + parent + "\\" + name, "f");
                    
                    System.out.println("DESTINATARY: " + serverRoute + "\\" + parent + "\\" + name);
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

    public static void main(String[] args) {
        ServerDB sdb = new ServerDB();
    }
}
