package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MoveRightAction extends TreeEditAction{

    private final ParseTreeDrawable tree;
    private final ParseNodeDrawable node;

    /**
     * Constructor for swapping selected node with its right sibling.
     * @param associatedPanel Panel associated with the action.
     * @param tree Tree associated with the action.
     * @param node Swapped node.
     */
    public MoveRightAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.node = node;
    }

    /**
     * Calls moveRight for the node.
     */
    public void execute() {
        tree.moveRight(node);
        tree.save();
        associatedPanel.save();
    }

    /**
     * Undoes the operation. Calls moveLeft for the node.
     */
    public void undo() {
        tree.moveLeft(node);
        tree.save();
        associatedPanel.save();
    }
}
