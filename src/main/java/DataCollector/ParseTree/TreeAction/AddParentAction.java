package DataCollector.ParseTree.TreeAction;

import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class AddParentAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private ParseNodeDrawable newParent;

    public AddParentAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        if (oldParent != null){
            newParent = new ParseNodeDrawable(oldParent, node, "-XXX-");
            associatedPanel.save();
        }
    }

    @Override
    public void undo() {
        if (oldParent != null){
            oldParent.replaceChild(newParent, node);
            associatedPanel.save();
        }
    }
}
