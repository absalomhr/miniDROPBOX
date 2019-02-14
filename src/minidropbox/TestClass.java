package minidropbox;

import java.io.File;
import javax.swing.JFileChooser;
//probando cambios
/**
 *
 * @author elpat
 */
public class TestClass {

    public static void main(String[] args) {
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
}
