package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.EditorPanel;

public class MoveSubtreeAction extends TreeEditAction{

    private ParseTreeDrawable tree;
    private ParseNodeDrawable fromNode;
    private ParseNodeDrawable toNode;
    private ParseNodeDrawable oldParent;

    public MoveSubtreeAction(EditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable fromNode, ParseNodeDrawable toNode){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public void execute() {
        oldParent = (ParseNodeDrawable)fromNode.getParent();
        tree.moveNode(fromNode, toNode);
        associatedPanel.save();
    }

    public void undo() {
        tree.moveNode(fromNode, oldParent);
        associatedPanel.save();
    }
}
