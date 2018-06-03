package DataCollector.WordNet;

import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ExampleTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component cell = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
        if (currentNode.getUserObject() instanceof SynSet && ((SynSet)currentNode.getUserObject()).getExample() != null){
            ((JComponent) cell).setToolTipText(((SynSet)currentNode.getUserObject()).getExample());
        } else {
            ((JComponent) cell).setToolTipText("");
        }
        return this;
    }
}
