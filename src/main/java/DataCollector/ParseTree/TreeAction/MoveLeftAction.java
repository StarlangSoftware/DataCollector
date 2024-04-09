package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MoveLeftAction extends TreeEditAction{

    private final ParseTreeDrawable tree;
    private final ParseNodeDrawable node;

    /**
     * Constructor for swapping selected node with its left sibling.
     * @param associatedPanel Panel associated with the action.
     * @param tree Tree associated with the action.
     * @param node Swapped node.
     */
    public MoveLeftAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.node = node;
    }

    /**
     * Calls moveLeft for the node.
     */
    public void execute() {
        tree.moveLeft(node);
        tree.save();
        associatedPanel.save();
    }

    /**
     * Undoes the operation. Calls moveRight for the node.
     */
    public void undo() {
        tree.moveRight(node);
        tree.save();
        associatedPanel.save();
    }
}
