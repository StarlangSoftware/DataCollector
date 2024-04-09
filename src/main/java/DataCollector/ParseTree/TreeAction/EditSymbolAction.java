package DataCollector.ParseTree.TreeAction;

import ParseTree.Symbol;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseNodeDrawable;

public class EditSymbolAction extends TreeEditAction {
    private final ParseNodeDrawable node;
    private Symbol oldSymbol;
    private final Symbol newSymbol;

    /**
     * Constructor for editing the symbol at a give node.
     * @param associatedPanel Panel associated with the action.
     * @param node The node for which the symbol will be edited.
     * @param newSymbol New symbol.
     */
    public EditSymbolAction(TreeEditorPanel associatedPanel, ParseNodeDrawable node, Symbol newSymbol){
        this.associatedPanel = associatedPanel;
        this.node = node;
        this.newSymbol = newSymbol;
    }

    /**
     * Stores the old symbol for undo operation. Sets the new symbol.
     */
    @Override
    public void execute() {
        oldSymbol = node.getData();
        node.setData(newSymbol);
        associatedPanel.save();
    }

    /**
     * Undoes the action. Restores the old symbol.
     */
    @Override
    public void undo() {
        node.setData(oldSymbol);
        associatedPanel.save();
    }
}
