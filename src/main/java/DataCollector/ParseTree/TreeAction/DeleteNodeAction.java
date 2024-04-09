package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.TreeEditorPanel;

public class DeleteNodeAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private int nodeIndex, childCount;

    /**
     * Constructor for deleting a given node.
     * @param associatedPanel Panel associated with the action.
     * @param node The node which will be deleted.
     */
    public DeleteNodeAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    /**
     * The old parent, the index of this child in the old parent children array, and the number of children of the
     * deleted node are saved for undo operation. The function deletes the node and adds all of its children to the
     * parent.
     */
    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        nodeIndex = oldParent.getChildIndex(node);
        oldParent.removeChild(node);
        childCount = node.numberOfChildren();
        for (int i = node.numberOfChildren() - 1; i >= 0; i--){
            oldParent.addChild(nodeIndex, node.getChild(i));
        }
        associatedPanel.save();
    }

    /**
     * Undoes the action. Removes the extra added children from the old parent. Adds the node to the removed position.
     * The function does not add the original children of the deleted node.
     */
    @Override
    public void undo() {
        for (int i = 0; i < childCount; i++){
            oldParent.removeChild(oldParent.getChild(nodeIndex));
        }
        oldParent.addChild(nodeIndex, node);
        associatedPanel.save();
    }
}
