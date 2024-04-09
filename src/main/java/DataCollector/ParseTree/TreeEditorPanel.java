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

    /**
     * Constructor for the base class of tree editing panels. Displays the tree with the given filename in the given
     * path. The editing will be done in the given layer type.
     * @param path File path of the current tree.
     * @param fileName Raw file name of the current tree.
     * @param viewLayer Name of the layer that will be edited for the current tree.
     */
    public TreeEditorPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
        actionList = new ArrayList<>();
    }

    /**
     * Clears the undo action list for the new tree displayed.
     */
    private void clear(){
        actionList.clear();
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = null;
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
    }

    /**
     * If the action list is not empty, undoes the last action read from the action list.
     */
    public void undo(){
        if (!actionList.isEmpty()){
            actionList.get(actionList.size() - 1).undo();
            actionList.remove(actionList.size() - 1);
            repaint();
        }
    }

    /**
     * Saves the current tree.
     */
    public void save(){
        currentTree.save();
    }

    /**
     * Base method for filling the options in the list or tree.
     * @param node Selected node for which options will be displayed.
     */
    protected void populateLeaf(ParseNodeDrawable node){
    }

    /**
     * When the user clicks on a node, this function identifies the node clicked and populates the list or tree
     * displayed for editing.
     * @param mouseEvent Mouse click event to handle.
     */
    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            populateLeaf(node);
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

    /**
     * When the user moves the mouse, two thing may happen, either selected node wil be deselected, or a new node will
     * be selected. This function handles those selection and deselection events.
     * @param mouseEvent Mouse move event to handle.
     */
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

}
