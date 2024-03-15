package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.TreeEditorPanel;

public class DeleteNodeAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private int nodeIndex, childCount;

    public DeleteNodeAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

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

    @Override
    public void undo() {
        for (int i = 0; i < childCount; i++){
            oldParent.removeChild(oldParent.getChild(nodeIndex));
        }
        oldParent.addChild(nodeIndex, node);
        associatedPanel.save();
    }
}
