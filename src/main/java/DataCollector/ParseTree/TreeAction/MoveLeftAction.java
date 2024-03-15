package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MoveLeftAction extends TreeEditAction{

    private final ParseTreeDrawable tree;
    private final ParseNodeDrawable node;

    public MoveLeftAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.node = node;
    }

    public void execute() {
        tree.moveLeft(node);
        tree.save();
        associatedPanel.save();
    }

    public void undo() {
        tree.moveRight(node);
        tree.save();
        associatedPanel.save();
    }
}
