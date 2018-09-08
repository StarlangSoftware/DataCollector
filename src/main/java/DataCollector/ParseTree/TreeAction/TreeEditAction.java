package DataCollector.ParseTree.TreeAction;

import DataCollector.ParseTree.EditorPanel;

public abstract class TreeEditAction {

    protected EditorPanel associatedPanel;

    public abstract void execute();
    public abstract void undo();

}
