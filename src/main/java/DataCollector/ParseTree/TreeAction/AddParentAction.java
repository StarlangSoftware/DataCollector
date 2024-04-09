package DataCollector.ParseTree.TreeAction;

import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class AddParentAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private ParseNodeDrawable oldParent;
    private ParseNodeDrawable newParent;

    /**
     * Constructor for adding a new empty parent to the given node.
     * @param associatedPanel Panel associated with the action.
     * @param node The node to which a new parent will be added.
     */
    public AddParentAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
    }

    /**
     * The old parent is saved for undo. The new parent is named -XXX- for further modification.
     */
    @Override
    public void execute() {
        oldParent = (ParseNodeDrawable)node.getParent();
        if (oldParent != null){
            newParent = new ParseNodeDrawable(oldParent, node, "-XXX-");
            associatedPanel.save();
        }
    }

    /**
     * Undoes the action. Node's new parent will be old parent.
     */
    @Override
    public void undo() {
        if (oldParent != null){
            oldParent.replaceChild(newParent, node);
            associatedPanel.save();
        }
    }
}
