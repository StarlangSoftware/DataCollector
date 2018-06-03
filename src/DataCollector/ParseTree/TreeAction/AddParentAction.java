package DataCollector.ParseTree.TreeAction;

import DataCollector.ParseTree.EditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class AddParentAction extends TreeEditAction{
    private ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private ParseNodeDrawable newParent;

    public AddParentAction(EditorPanel associatedPanel, ParseNodeDrawable node){
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
