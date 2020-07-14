package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.TreeEditAction;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class TreeEditorPanel extends TreeViewerPanel implements MouseListener, MouseMotionListener {

    protected ParseNodeDrawable previousNode = null;
    static public String treePath = "../Turkish/";
    static public String treePath2 = "../Turkish2/";
    static public String phrasePath = "../Turkish-Phrase/";
    protected ArrayList<TreeEditAction> actionList;
    protected boolean isEditing;

    public TreeEditorPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
        actionList = new ArrayList<TreeEditAction>();
    }

    private void clear(){
        actionList.clear();
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = null;
    }

    protected void nextTree(int count){
        clear();
        super.nextTree(count);
    }

    protected void previousTree(int count){
        clear();
        super.previousTree(count);
    }

    public void undo(){
        if (actionList.size() != 0){
            actionList.get(actionList.size() - 1).undo();
            actionList.remove(actionList.size() - 1);
            repaint();
        }
    }

    public void save(){
        currentTree.save();
    }

    protected void populateLeaf(ParseNodeDrawable node){
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            populateLeaf(node);
        }
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null && node != previousNode && !isEditing){
            if (previousNode != null) {
                previousNode.setSelected(false);
            }
            node.setSelected(true);
            previousNode = node;
            this.repaint();
        } else {
            if (node == null && previousNode != null && !isEditing){
                previousNode.setSelected(false);
                previousNode = null;
                this.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

}
