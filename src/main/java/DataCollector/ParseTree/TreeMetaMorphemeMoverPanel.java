package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import ParseTree.ParseNode;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.MetaMorphemeMoveAction;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class TreeMetaMorphemeMoverPanel extends TreeStructureEditorPanel {

    private ParseNodeDrawable draggedNode = null;
    private int selectedIndex, dragX, dragY;
    private ParseNodeDrawable fromNode = null;
    private boolean dragged = false;

    public TreeMetaMorphemeMoverPanel(String path, String fileName) {
        super(path, fileName, ViewLayerType.META_MORPHEME_MOVED);
        widthDecrease = 30;
        heightDecrease = 120;
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void clear(){
        selectedIndex = -1;
    }

    protected void nextTree(int count){
        clear();
        super.nextTree(count);
    }

    protected void previousTree(int count){
        clear();
        super.previousTree(count);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            if (editableNode != null)
                editableNode.setEditable(false);
            editableNode = node;
            editableNode.setEditable(true);
            isEditing = false;
            this.repaint();
            this.setFocusable(true);
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        ParseNode node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node == previousNode && previousNode != null){
            fromNode = previousNode;
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (fromNode != null && node != null && fromNode != node && dragged && fromNode.numberOfChildren() == 0 && node.numberOfChildren() == 0 && node.getLayerData(ViewLayerType.TURKISH_WORD).equalsIgnoreCase("*NONE*")){
            draggedNode.setDragged(false);
            MetaMorphemeMoveAction action = new MetaMorphemeMoveAction(this, fromNode.getLayerInfo(), node.getLayerInfo(), selectedIndex);
            action.execute();
            actionList.add(action);
        }
        dragged = false;
        fromNode = null;
        this.repaint();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        dragged = true;
        if (node != null && node != previousNode && node.numberOfChildren() == 0 && node.getLayerData(ViewLayerType.TURKISH_WORD).equalsIgnoreCase("*NONE*")){
            draggedNode = node;
            draggedNode.setDragged(true);
            this.repaint();
        } else {
            if (node == null && draggedNode != null){
                draggedNode.setDragged(false);
                draggedNode = null;
                this.repaint();
            } else {
                if (previousNode != null){
                    dragX = mouseEvent.getX();
                    dragY = mouseEvent.getY();
                    this.repaint();
                }
            }
        }
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node.isLeaf()){
            if (!dragged){
                if (node != null){
                    selectedIndex = currentTree.getSubItemAt(mouseEvent.getX(), mouseEvent.getY());
                    if (selectedIndex != 0){
                        node.setSelected(true, selectedIndex);
                        if (node != previousNode){
                            if (previousNode != null)
                                previousNode.setSelected(false);
                        }
                        previousNode = node;
                        this.repaint();
                    }
                } else {
                    if (previousNode != null){
                        previousNode.setSelected(false);
                        previousNode = null;
                        this.repaint();
                    }
                }
            }
        } else {
            super.mouseMoved(mouseEvent);
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    protected void paintComponent(Graphics g){
        int startX, startY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        super.paintComponent(g);
        if (dragged && previousNode != null){
            startX = previousNode.getArea().x + previousNode.getArea().width / 2;
            startY = previousNode.getArea().y + 20 * (previousNode.getSelectedIndex() + 1);
            pointStart = new Point2D.Double(startX, startY);
            pointEnd = new Point2D.Double(dragX, dragY);
            if (dragY > startY){
                pointCtrl1 = new Point2D.Double(startX, (startY + dragY) / 2);
                pointCtrl2 = new Point2D.Double((startX + dragX) / 2, dragY);
            } else {
                pointCtrl1 = new Point2D.Double((startX + dragX) / 2, startY);
                pointCtrl2 = new Point2D.Double(dragX, (startY + dragY) / 2);
            }
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.MAGENTA);
            g2.draw(cubicCurve);
        }
    }


}
