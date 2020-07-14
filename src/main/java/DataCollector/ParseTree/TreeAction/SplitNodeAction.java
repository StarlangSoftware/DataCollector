package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class SplitNodeAction extends TreeEditAction{
    private ParseNodeDrawable node;
    private ParseNodeDrawable parent;
    private ParseTreeDrawable parseTree;

    public SplitNodeAction(TreeEditorPanel associatedPanel, ParseTreeDrawable parseTree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.node = node;
        this.parseTree = parseTree;
        parent = (ParseNodeDrawable) node.getParent();
    }

    @Override
    public void execute() {
        parseTree.divideIntoWords(node);
        associatedPanel.save();
    }

    @Override
    public void undo() {
        parseTree.combineWords(parent, node);
        associatedPanel.save();
    }
}
