package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.EditorPanel;

public class MoveSubtreeAction extends TreeEditAction{

    private ParseTreeDrawable tree;
    private ParseNodeDrawable fromNode;
    private ParseNodeDrawable toNode;
    private ParseNodeDrawable oldParent;
    private int oldChildIndex;

    public MoveSubtreeAction(EditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable fromNode, ParseNodeDrawable toNode){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public void execute() {
        oldParent = (ParseNodeDrawable)fromNode.getParent();
        for (int i = 0; i < oldParent.numberOfChildren(); i++){
            if (oldParent.getChild(i).equals(fromNode)){
                oldChildIndex = i;
                break;
            }
        }
        tree.moveNode(fromNode, toNode);
        associatedPanel.save();
    }

    public void undo() {
        tree.moveNode(fromNode, oldParent, oldChildIndex);
        associatedPanel.save();
    }
}
