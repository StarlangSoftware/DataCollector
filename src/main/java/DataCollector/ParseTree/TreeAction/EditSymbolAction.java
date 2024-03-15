package DataCollector.ParseTree.TreeAction;

import ParseTree.Symbol;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class EditSymbolAction extends TreeEditAction {
    private final ParseNodeDrawable node;
    private Symbol oldSymbol;
    private final Symbol newSymbol;

    public EditSymbolAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node, Symbol newSymbol){
        this.associatedPanel = associatedPanel;
        this.node = node;
        this.newSymbol = newSymbol;
    }

    @Override
    public void execute() {
        oldSymbol = node.getData();
        node.setData(newSymbol);
        associatedPanel.save();
    }

    @Override
    public void undo() {
        node.setData(oldSymbol);
        associatedPanel.save();
    }
}
