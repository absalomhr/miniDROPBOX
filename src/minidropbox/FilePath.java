package minidropbox;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author elpat
 */
public class FilePath implements Serializable {

    public FilePath() {
        paths = new ArrayList<Pair<String, String>>();
    }

    public ArrayList<Pair<String, String>> paths;

    public ArrayList<Pair<String, String>> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Pair<String, String>> paths) {
        this.paths = paths;
    }

    public void insert(String p, String t) {
        paths.add(new Pair(p, t));
    }

    @Override
    public String toString() {
        return "FilePath{" + "paths=" + paths + '}';
    }

}
