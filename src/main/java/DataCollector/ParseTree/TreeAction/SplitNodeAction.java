package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class SplitNodeAction extends TreeEditAction{
    private final ParseNodeDrawable node;
    private final ParseNodeDrawable parent;
    private final ParseTreeDrawable parseTree;

    /**
     * Constructor for dividing a node into multiple child nodes. The node should be a multiword expression such as
     * 'ödü patladı'.
     * @param associatedPanel Panel associated with the action.
     * @param parseTree Tree associated with the action.
     * @param node Node whose layer info will be divided.
     */
    public SplitNodeAction(TreeEditorPanel associatedPanel, ParseTreeDrawable parseTree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
        this.parseTree = parseTree;
        parent = (ParseNodeDrawable) node.getParent();
    }

    /**
     * Split the multiword expression in the node into multiple children nodes by calling divideIntoWords method.
     */
    @Override
    public void execute() {
        parseTree.divideIntoWords(node);
        associatedPanel.save();
    }

    /**
     * Undoes the operation by combining the divided words into a single multiword expression.
     */
    @Override
    public void undo() {
        parseTree.combineWords(parent, node);
        associatedPanel.save();
    }
}
