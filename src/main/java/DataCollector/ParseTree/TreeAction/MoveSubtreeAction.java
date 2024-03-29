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

    public MoveSubtreeAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable fromNode, ParseNodeDrawable toNode, int draggedIndex){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.newChildIndex = draggedIndex;
    }

    public void execute() {
        oldParent = (ParseNodeDrawable)fromNode.getParent();
        oldChildIndex = oldParent.getChildIndex(fromNode);
        tree.moveNode(fromNode, toNode, newChildIndex);
        associatedPanel.save();
    }

    public void undo() {
        tree.moveNode(fromNode, oldParent, oldChildIndex);
        associatedPanel.save();
    }
}
