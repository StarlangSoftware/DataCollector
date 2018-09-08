package DataCollector.ParseTree.TreeAction;

import AnnotatedTree.*;
import DataCollector.ParseTree.EditorPanel;

public class MoveRightAction extends TreeEditAction{

    private ParseTreeDrawable tree;
    private ParseNodeDrawable node;

    public MoveRightAction(EditorPanel associatedPanel, ParseTreeDrawable tree, ParseNodeDrawable node){
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
