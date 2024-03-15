package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MoveRightAction extends TreeEditAction{

    private final ParseTreeDrawable tree;
    private final ParseNodeDrawable node;

    public MoveRightAction(TreeEditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable node){
        this.associatedPanel = associatedPanel;
        this.tree = tree;
        this.node = node;
    }

    public void execute() {
        tree.moveRight(node);
        tree.save();
        associatedPanel.save();
    }

    public void undo() {
        tree.moveLeft(node);
        tree.save();
        associatedPanel.save();
    }
}
