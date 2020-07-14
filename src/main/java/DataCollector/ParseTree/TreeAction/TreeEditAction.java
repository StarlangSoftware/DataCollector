package DataCollector.ParseTree.TreeAction;

import DataCollector.ParseTree.TreeEditorPanel;

public abstract class TreeEditAction {

    protected TreeEditorPanel associatedPanel;

    public abstract void execute();
    public abstract void undo();

}
