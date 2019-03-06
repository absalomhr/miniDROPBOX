
package minidropbox;

import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;  

/**
 *
 * @author Carlo
 */
public class TreeFiles {
    JTree mytree;
    ArrayList <DefaultMutableTreeNode> nodes;
    public TreeFiles() {
        nodes = new ArrayList<DefaultMutableTreeNode>();
    }
    public DefaultMutableTreeNode createNode(String name, String type) {
        boolean val = true;
        DefaultMutableTreeNode current;
        if(!type.equals("d"))
            val = false;
        current = new DefaultMutableTreeNode(name, val);
        nodes.add(current);
        return current;
    }
    public void insertNodesToTree() {
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        
        for(DefaultMutableTreeNode c: nodes)
            root.add(c);
        mytree = new JTree(root);
    }
    public JTree getTree() {
        return mytree;
    }
    
}
