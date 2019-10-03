package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.EditorPanel;

public class DeleteNodeAction extends TreeEditAction{
    private ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private int nodeIndex, childCount;

    public DeleteNodeAction(EditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        for (int i = 0; i < oldParent.numberOfChildren(); i++){
            if (oldParent.getChild(i) == node){
                nodeIndex = i;
                break;
            }
        }
        oldParent.removeChild(node);
        childCount = node.numberOfChildren();
        for (int i = node.numberOfChildren() - 1; i >= 0; i--){
            oldParent.addChild(nodeIndex, node.getChild(i));
        }
        associatedPanel.save();
    }

    @Override
    public void undo() {
        for (int i = 0; i < childCount; i++){
            oldParent.removeChild(oldParent.getChild(nodeIndex));
        }
        oldParent.addChild(nodeIndex, node);
        associatedPanel.save();
    }
}
