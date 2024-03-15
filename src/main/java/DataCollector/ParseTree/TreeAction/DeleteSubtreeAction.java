package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.TreeEditorPanel;

public class DeleteSubtreeAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private int nodeIndex;

    public DeleteSubtreeAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        nodeIndex = oldParent.getChildIndex(node);
        oldParent.removeChild(node);
        associatedPanel.save();
    }

    @Override
    public void undo() {
        oldParent.addChild(nodeIndex, node);
        associatedPanel.save();
    }

}
