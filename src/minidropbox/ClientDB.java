package minidropbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javafx.util.Pair;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Absalom Herrera
 */
public class ClientDB {

    private Socket s;
    private String host;
    private String clientRoute;
    private int port;
    private DataOutputStream dosToServer;
    private DataOutputStream dosToFile;
    private DataInputStream disFromServer;
    private DataInputStream disFromFile;
    private FilePath fp;

    public ClientDB() {
        try {
            host = "127.0.0.1";
            port = 1234;
            dosToServer = null;
            disFromServer = null;
            dosToFile = null;
            fp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(File[] filesToSend) {
        try {
            for (int i = 0; i < filesToSend.length; i++) {
                send(filesToSend[i], "", filesToSend[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(File file, String parentDirectory, String name) {
        System.out.println("\nSENDING FILES/DIRECTORIES");
        try {
            s = new Socket(host, port);
            dosToServer = new DataOutputStream(s.getOutputStream());
            long size = file.length();
            char type;
            if (file.isDirectory()) {
                type = 'd';
            } else {
                type = 'f';
            }
            dosToServer.writeInt(1);
            dosToServer.flush();
            dosToServer.writeUTF(name);
            dosToServer.flush();
            dosToServer.writeUTF(parentDirectory);
            dosToServer.flush();
            dosToServer.writeChar(type);
            dosToServer.flush();

            if (type == 'd') {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    size = f.length();
                    if (f.isDirectory()) {
                        System.out.println("SENDING DIR: ");
                        if (parentDirectory.equals("")) {
                            System.out.println("PARENT: " + f.getParentFile().getName() + " NAME: " + name);
                            send(f, f.getParentFile().getName(), f.getName());
                        } else {
                            System.out.println("PARENT: " + parentDirectory + " NAME: " + name);
                            send(f, parentDirectory + OsUtils.getSlash() + name, f.getName());
                        }
                    } else if (f.isFile()) {
                        System.out.println("SENDING FILE RECURSIVE FROM INNER");
                        if (parentDirectory.equals("")) {
                            System.out.println("EQEMPTY : PARENT: " + f.getParentFile().getName() + " NAME: " + f.getName());
                            send(f, f.getParentFile().getName(), f.getName());
                        } else {
                            System.out.println("PARENT: " + parentDirectory + OsUtils.getSlash() + name + " NAME: " + f.getName());
                            send(f, parentDirectory + OsUtils.getSlash() + name, f.getName());
                        }
                    }
                }
            } else {
                dosToServer.writeLong(size);
                dosToServer.flush();
                System.out.println("ACTUALLY SENDING FILE: NAME:" + name + " PARENT: " + parentDirectory);
                long sent = 0;
                int percent = 0, n = 0;
                disFromFile = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                while (sent < size) {
                    byte[] b = new byte[1500];
                    n = disFromFile.read(b);
                    dosToServer.write(b, 0, n);
                    dosToServer.flush();
                    sent += n;
                    percent = (int) ((sent * 100) / size);
                    System.out.print("\rSENT: " + percent + " %");
                }
            }
            disFromFile.close();
            dosToServer.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TreeFiles showFiles() {
        TreeFiles tf1 = null;
        try {
            s = new Socket(host, port);
            dosToServer = new DataOutputStream(s.getOutputStream());
            dosToServer.writeInt(3);
            Socket s1 = new Socket(host, port + 1);
            ObjectInputStream ois = new ObjectInputStream(s1.getInputStream());
            fp = (FilePath) ois.readObject();
            System.out.println("Objeto recibido: " + fp.toString());
            ArrayList <Pair<String, String>> arr1 = fp.getPaths();
            
            tf1 = new TreeFiles();
            for(Pair <String, String> it: arr1) {
                tf1.createNode(it.getKey(), it.getValue());
               //System.out.println(it.getKey() + " "+it.getValue());
                //for(String sp: it.getKey().split("/"))
                    //System.out.println("" + sp);
            }
            tf1.insertNodesToTree();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tf1;
    }

    /**
     * provisional, aqui deberia recibir el nombre del archivo que selecciona de
     * la GUI, pero eso aun no esta por el momento eligire un archivo random
     */
    public void download(String path) {
        try {
            s = new Socket(host, port);
            disFromServer = new DataInputStream(s.getInputStream());
            dosToServer = new DataOutputStream(s.getOutputStream());
            dosToFile = new DataOutputStream((new FileOutputStream("C:\\Users\\Carlo\\Desktop\\myJar.jar")));
            dosToServer.writeInt(2);
            
            dosToServer.writeUTF(path);
            long r = 0;
            int n = 0, percent = 0;
            long size = disFromServer.readLong();
            String name = disFromServer.readUTF();
            while (r < size) {
                byte[] b = new byte[1500];
                n = disFromServer.read(b);
                dosToFile.write(b, 0, n);
                dosToFile.flush();
                r += n;
                percent = (int) ((r * 100) / size);
                System.out.print("\rRECEIVING: " + percent + "%");
            }
            dosToFile.close();
            disFromServer.close();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveFiles() {

        System.out.println("\nRECEIVING FILES/DIRECTORIES");
        long size;
        String name, parentDirectory;
        char type;

        try {
            s = new Socket(host, port);
            disFromServer = new DataInputStream(s.getInputStream());
            dosToServer = new DataOutputStream(s.getOutputStream());

            dosToServer.writeInt(2);

            // Provisional 
            Random rand = new Random();

            // Obtain a number between [0 - paths size].
            int nf = rand.nextInt(fp.getPaths().size());

            dosToServer.writeUTF(fp.getPaths().get(nf).getKey());
            // Termina provisional

            name = disFromServer.readUTF();
            parentDirectory = disFromServer.readUTF();
            type = disFromServer.readChar();

            if (type == 'd') {
                System.out.println("CREATING DIR: " + "NAME: " + name + " PARENT: " + parentDirectory);
                boolean bol = false;
                if (parentDirectory.equals("")) {
                    bol = new File(clientRoute + OsUtils.getSlash() + name).mkdir();
                } else {
                    bol = new File(clientRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name).mkdir();
                }
                if (bol) {
                    System.out.println("DIR CREATED: " + name);
                } else {
                    System.err.println("DIR COULDNT BE CREATED!");
                }
            } else {
                size = disFromServer.readLong();
                System.out.println("CREATING FILE: " + "NAME: " + name + " PARENT: " + parentDirectory);

                if (parentDirectory.equals("")) {
                    dosToFile = new DataOutputStream((new FileOutputStream(clientRoute + OsUtils.getSlash() + name)));

                    System.out.println("DESTINATARY: " + clientRoute + OsUtils.getSlash() + name);
                } else {
                    dosToFile = new DataOutputStream((new FileOutputStream(clientRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name)));

                    System.out.println("DESTINATARY: " + clientRoute + OsUtils.getSlash() + parentDirectory + OsUtils.getSlash() + name);
                }

                long r = 0;
                int n = 0, percent = 0;
                while (r < size) {
                    byte[] b = new byte[1500];
                    n = disFromServer.read(b);
                    dosToFile.write(b, 0, n);
                    dosToFile.flush();
                    r += n;
                    percent = (int) ((r * 100) / size);
                    System.out.print("\rRECEIVING: " + percent + "%");
                }
                dosToFile.close();
            }
            disFromServer.close();
            dosToServer.close();
        } catch (Exception e) {
            System.err.println("RECEIVE ERROR:\n");
            e.printStackTrace();
        }
    }

    public void setClientRoute(String clientRoute) {
        this.clientRoute = clientRoute;
    }

}
