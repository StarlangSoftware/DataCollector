package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.TreeEditorPanel;

public class DeleteSubtreeAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private int nodeIndex;

    /**
     * Constructor for deleting the subtree rooted at a given node.
     * @param associatedPanel Panel associated with the action.
     * @param node The root node of the subtree which will be deleted.
     */
    public DeleteSubtreeAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    /**
     * The old parent, the index of this child in the old parent children array are saved for undo operation.
     * The function deletes the node and therefore all the subtree rooted at that node.
     */
    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        nodeIndex = oldParent.getChildIndex(node);
        oldParent.removeChild(node);
        associatedPanel.save();
    }

    /**
     * Undoes the action. Restores the subtree by adding the node (the subtree deleted) to the removed position.
     */
    @Override
    public void undo() {
        oldParent.addChild(nodeIndex, node);
        associatedPanel.save();
    }

}
