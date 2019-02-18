package minidropbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 *
 * @author Absalom Herrera
 */
public class ClientDB {

    private Socket s;
    private String host;
    private int port;
    private DataOutputStream dosToServer;
    private DataInputStream disFromFile;

    public ClientDB() {
        try {
            host = "127.0.0.1";
            port = 9999;
            dosToServer = null;
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
            dosToServer.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
