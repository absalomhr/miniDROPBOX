package minidropbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFileChooser;

/**
 *
 * @author Absalom Herrera
 */
public class ServerDB {

    private ServerSocket s;
    private Socket cl;
    private JFileChooser jfc; // Route where files are going to be stored on the server side
    private static String serverRoute;
    private DataOutputStream dosToFile;
    private DataInputStream disFromCl;
    private FilePath paths; // if clients requeries to see whats stored on the server
    private int clientRequest; // 0 = do nothing, 1 = upload

    public ServerDB() {
        // Choosing server directory (for testing test)
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

        disFromCl = null;
        serverRoute = f.getAbsolutePath();
        paths = new FilePath();
        cl = null;
        clientRequest = 0;

        try {
            s = new ServerSocket(9999);
            s.setReuseAddress(true);
        } catch (Exception e) {
            System.err.println("CONSTRUCTOR ERROR:\n");
            e.printStackTrace();
        }
    }

    public void connect() {

        try {
            for (;;) {
                System.out.println("\nSERVER ON, WAITING FOR CLIENT CONNECTION");
                cl = s.accept();
                System.out.println("CLIENT FROM: " + cl.getInetAddress() + " PORT: " + cl.getPort());
                disFromCl = new DataInputStream(cl.getInputStream());
                clientRequest = disFromCl.readInt();
                if (clientRequest == 1) {
                    recieveFiles();
                }
            }
        } catch (Exception e) {
            System.err.println("CONNECT ERROR:\n");
            e.printStackTrace();
        }
    }

    private void recieveFiles() {
        System.out.println("\nRECEIVING FILES/DIRECTORIES");
        long size;
        String name, parentDirectory;
        char type;

        try {
            name = disFromCl.readUTF();
            parentDirectory = disFromCl.readUTF();
            type = disFromCl.readChar();

            if (type == 'd') {
                System.out.println("CREATING DIR: " + "NAME: " + name + " PARENT: " + parentDirectory);
                boolean bol = false;
                if (parentDirectory.equals("")) {

                    bol = new File(serverRoute + OsUtils.getSlash() + name).mkdir();

                    paths.insert(serverRoute + OsUtils.getSlash() + name, "d");

                } else {
                    bol = new File(serverRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name).mkdir();

                    paths.insert(serverRoute + OsUtils.getSlash() + name, "d");

                }
                if (bol) {
                    System.out.println("DIR CREATED: " + name);
                } else {
                    System.err.println("DIR COULDNT BE CREATED!");
                }
            } else {
                size = disFromCl.readLong();
                System.out.println("CREATING FILE: " + "NAME: " + name + " PARENT: " + parentDirectory);

                if (parentDirectory.equals("")) {
                    dosToFile = new DataOutputStream((new FileOutputStream(serverRoute + OsUtils.getSlash() + name)));

                    paths.insert(serverRoute + OsUtils.getSlash() + name, "f");

                    System.out.println("DESTINATARY: " + serverRoute + OsUtils.getSlash() + name);
                } else {
                    dosToFile = new DataOutputStream((new FileOutputStream(serverRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name)));

                    paths.insert(serverRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name, "f");

                    System.out.println("DESTINATARY: " + serverRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name);
                }

                long r = 0;
                int n = 0, percent = 0;
                while (r < size) {
                    byte[] b = new byte[1500];
                    n = disFromCl.read(b);
                    dosToFile.write(b, 0, n);
                    dosToFile.flush();
                    r += n;
                    percent = (int) ((r * 100) / size);
                    System.out.print("\rRECEIVING: " + percent + "%");
                }
                dosToFile.close();
            }

        } catch (Exception e) {
            System.err.println("RECEIVE ERROR:\n");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ServerDB sdb = new ServerDB();
        sdb.connect();
    }
}
