package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.MoveLeftAction;
import DataCollector.ParseTree.TreeAction.MoveRightAction;

public class TreeStructureEditorPanel extends TreeEditorPanel {
    protected ParseNodeDrawable editableNode = null;

    /**
     * Constructor for the base class of structure editing classes. Displays the tree with the given filename in the
     * given path. The editing will be done in the given layer type.
     * @param path File path of the current tree.
     * @param fileName Raw file name of the current tree.
     * @param viewLayer Name of the layer that will be edited for the current tree.
     */
    public TreeStructureEditorPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
    }

    /**
     * Swaps editable node with its right sibling.
     */
    protected void moveRight(){
        if (editableNode != null){
            MoveRightAction action = new MoveRightAction(this, currentTree, editableNode);
            action.execute();
            actionList.add(action);
            repaint();
        }
    }

    /**
     * Swaps editable node with its left sibling.
     */
    protected void moveLeft(){
        if (editableNode != null){
            MoveLeftAction action = new MoveLeftAction(this, currentTree, editableNode);
            action.execute();
            actionList.add(action);
            repaint();
        }
    }


}
