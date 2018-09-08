package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.ParseTree.TreeAction.TreeEditAction;

import javax.swing.*;
import java.util.ArrayList;

public class LeafEditorPanel extends EditorPanel {
    protected JScrollPane pane;
    protected boolean defaultFillEnabled;

    public LeafEditorPanel(String path, String fileName, ViewLayerType viewLayer, boolean defaultFillEnabled) {
        super(path, fileName, viewLayer);
        this.defaultFillEnabled = defaultFillEnabled;
        widthDecrease = 185;
        heightDecrease = 120;
        defaultFill();
    }

    protected void setAction(TreeEditAction action){
        previousNode.setSelected(false);
        actionList.add(action);
        action.execute();
        pane.setVisible(false);
        isEditing = false;
        repaint();
    }

    protected void clear(){
        pane.setVisible(false);
    }

    protected void nextTree(int count){
        clear();
        super.nextTree(count);
        if (defaultFillEnabled){
            defaultFill();
        }
    }

    protected void previousTree(int count){
        clear();
        super.previousTree(count);
        if (defaultFillEnabled){
            defaultFill();
        }
    }

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

    protected boolean defaultFill(ParseNodeDrawable node){
        return false;
    }

}
