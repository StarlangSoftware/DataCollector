package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MoveSubtreeAction extends TreeEditAction{

    private final ParseTreeDrawable tree;
    private final ParseNodeDrawable fromNode;
    private final ParseNodeDrawable toNode;
    private ParseNodeDrawable oldParent;
    private int oldChildIndex;
    private final int newChildIndex;

    /**
     * Constructor for moving a subtree rooted at a source node as a specific indexed child to a target node.
     * @param associatedPanel Panel associated with the action.
     * @param tree Tree associated with the action.
     * @param fromNode Root of the subtree to be moved.
     * @param toNode New parent of the moved subtree.
     * @param draggedIndex New child index of the moved subtree.
     */
    public MoveSubtreeAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable fromNode, ParseNodeDrawable toNode, int draggedIndex){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.newChildIndex = draggedIndex;
    }

    /**
     * Stores the old parent and child index of the moved subtree for undo operation. Moves the subtree as the given
     * child index to the target node.
     */
    public void execute() {
        oldParent = (ParseNodeDrawable)fromNode.getParent();
        oldChildIndex = oldParent.getChildIndex(fromNode);
        tree.moveNode(fromNode, toNode, newChildIndex);
        associatedPanel.save();
    }

    /**
     * Undoes the operation. Moves the subtree from the target to the old parent of the source node.
     */
    public void undo() {
        tree.moveNode(fromNode, oldParent, oldChildIndex);
        associatedPanel.save();
    }
}
