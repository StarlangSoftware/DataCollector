package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.ParseTree.TreeAction.TreeEditAction;

import javax.swing.*;
import java.util.ArrayList;

public class TreeLeafEditorPanel extends TreeEditorPanel {
    protected JScrollPane pane;
    protected boolean defaultFillEnabled;

    /**
     * Constructor for the base class of leaf editing classes. Displays the tree with the given filename in the given
     * path. The editing will be done in the given layer type.
     * @param path File path of the current tree.
     * @param fileName Raw file name of the current tree.
     * @param viewLayer Name of the layer that will be edited for the current tree.
     * @param defaultFillEnabled If defaultFillEnabled, the panel automatically calls the default fill.
     */
    public TreeLeafEditorPanel(String path, String fileName, ViewLayerType viewLayer, boolean defaultFillEnabled) {
        super(path, fileName, viewLayer);
        this.defaultFillEnabled = defaultFillEnabled;
        widthDecrease = 185;
        heightDecrease = 120;
        defaultFill();
    }

    /**
     * Implements the action. It also adds the action to the action list, so that undo for that action is possible.
     * @param action  Action to be implemented.
     */
    protected void setAction(TreeEditAction action){
        previousNode.setSelected(false);
        actionList.add(action);
        action.execute();
        pane.setVisible(false);
        isEditing = false;
        repaint();
    }

    /**
     * Makes the leaf editing item (list or tree) invisible.
     */
    protected void clear(){
        pane.setVisible(false);
    }

    /**
     * Overloaded function that displays the next tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of nextTree(3), ViewerPanel will display 0126.train. If the next tree
     * does not exist, nothing will happen.
     * @param count Number of trees to go forward
     */
    protected void nextTree(int count){
        clear();
        super.nextTree(count);
        if (defaultFillEnabled){
            defaultFill();
        }
    }

    /**
     * Overloaded function that displays the previous tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of previousTree(4), ViewerPanel will display 0119.train. If the
     * previous tree does not exist, nothing will happen.
     * @param count Number of trees to go backward
     */
    protected void previousTree(int count){
        clear();
        super.previousTree(count);
        if (defaultFillEnabled){
            defaultFill();
        }
    }

    /**
     * The function automatically fills the annotation layer of the leave nodes with the overloaded defaultFill
     * function. If any node is changed, the function also saves the tree.
     */
    protected void defaultFill(){
        boolean needToSave = false;
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) currentTree.getRoot(), new IsLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (ParseNodeDrawable leafNode : leafList){
            if (defaultFill(leafNode)){
                needToSave = true;
            }
        }
        if (needToSave){
            save();
            repaint();
        }
    }

    /**
     * Base function to automatically fill the annotation layer of the node.
     * @param node Parse Node
     * @return True if it filled automatically, false otherwise.
     */
    protected boolean defaultFill(ParseNodeDrawable node){
        return false;
    }

}
