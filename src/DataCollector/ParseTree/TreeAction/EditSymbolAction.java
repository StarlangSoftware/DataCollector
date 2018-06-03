package DataCollector.ParseTree.TreeAction;

import ParseTree.Symbol;
import DataCollector.ParseTree.EditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class EditSymbolAction extends TreeEditAction {
    private ParseNodeDrawable node;
    private Symbol oldSymbol;
    private Symbol newSymbol;

    public EditSymbolAction(EditorPanel associatedPanel, ParseNodeDrawable node, Symbol newSymbol){
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
