package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.MoveLeftAction;
import DataCollector.ParseTree.TreeAction.MoveRightAction;

public class TreeStructureEditorPanel extends TreeEditorPanel {
    protected ParseNodeDrawable editableNode = null;

    public TreeStructureEditorPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
    }

    protected void moveRight(){
        if (editableNode != null){
            MoveRightAction action = new MoveRightAction(this, currentTree, editableNode);
            action.execute();
            actionList.add(action);
            repaint();
        }
    }

    protected void moveLeft(){
        if (editableNode != null){
            MoveLeftAction action = new MoveLeftAction(this, currentTree, editableNode);
            action.execute();
            actionList.add(action);
            repaint();
        }
    }


}
