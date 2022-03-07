package DataCollector.ParseTree.Dependency;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import ParseTree.ParseNode;
import DependencyParser.Turkish.TurkishDependencyRelation;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.LayerAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class TreeDependencyPanel extends TreeEditorPanel {

    private ParseNodeDrawable draggedNode = null;
    private int dragX, dragY, selectedIndex = -1;
    private ParseNodeDrawable fromNode = null;
    private boolean dragged = false;
    private JList list;
    private JScrollPane pane;

    public TreeDependencyPanel(String path, String fileName) {
        super(path, fileName, ViewLayerType.INFLECTIONAL_GROUP);
        nodeWidth = 200;
        widthDecrease = 30;
        heightDecrease = 120;
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("---NO DEPENDENCY---");
        for (String dependencyType: TurkishDependencyRelation.turkishDependencyTypes){
            listModel.addElement(dependencyType);
        }
        list = new JList(listModel);
        list.setVisible(false);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (list.getSelectedIndex() != -1 && fromNode != null && draggedNode != null) {
                        String relation;
                        if (list.getSelectedIndex() == 0){
                            relation = null;
                        } else {
                            relation = draggedNode.getLeafIndex() + "," + (selectedIndex + 1) + "," + TurkishDependencyRelation.turkishDependencyTypes[list.getSelectedIndex() - 1];
                        }
                        LayerAction action = new LayerAction(((TreeDependencyPanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), fromNode.getLayerInfo(), relation, ViewLayerType.DEPENDENCY);
                        actionList.add(action);
                        action.execute();
                        list.clearSelection();
                        list.setVisible(false);
                        pane.setVisible(false);
                        repaint();
                    }
                }
            }
        });
        list.setFocusTraversalKeysEnabled(false);
        pane = new JScrollPane(list);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        ParseNode node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node == previousNode && previousNode != null && node.numberOfChildren() == 0 && !((ParseNodeDrawable)node).getLayerData(ViewLayerType.TURKISH_WORD).contains("*")){
            fromNode = previousNode;
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (fromNode != null && node != null && fromNode != node && dragged && fromNode.numberOfChildren() == 0 && node.numberOfChildren() == 0 && !node.getLayerData(ViewLayerType.TURKISH_WORD).contains("*")){
            draggedNode.setDragged(false);
            list.setVisible(true);
            pane.setVisible(true);
            list.clearSelection();
            pane.getVerticalScrollBar().setValue(0);
            pane.setBounds((fromNode.getArea().getX() + draggedNode.getArea().getX()) / 2, (fromNode.getArea().getY() + draggedNode.getArea().getY()) / 2, 200, 90);
        }
        dragged = false;
        this.repaint();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        dragged = true;
        if (node != null && node != previousNode && node.numberOfChildren() == 0 && !node.getLayerData(ViewLayerType.TURKISH_WORD).contains("*")){
            selectedIndex = currentTree.getSubItemAt(mouseEvent.getX(), mouseEvent.getY());
            draggedNode = node;
            if (selectedIndex != -1){
                draggedNode.setDragged(true, selectedIndex);
            } else {
                draggedNode.setDragged(true);
            }
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
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (!dragged){
            if (node != null && !node.getLayerData(ViewLayerType.TURKISH_WORD).contains("*")){
                node.setSelected(true);
                if (node != previousNode){
                    if (previousNode != null)
                        previousNode.setSelected(false);
                }
                previousNode = node;
                this.repaint();
            } else {
                if (previousNode != null){
                    previousNode.setSelected(false);
                    previousNode = null;
                    this.repaint();
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    protected void paintComponent(Graphics g){
        int startX, startY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        super.paintComponent(g);
        if (dragged && previousNode != null){
            startX = previousNode.getArea().getX() + previousNode.getArea().getWidth() / 2;
            startY = previousNode.getArea().getY() + 20;
            pointStart = new Point2D.Double(startX, startY);
            pointEnd = new Point2D.Double(dragX, dragY);
            if (dragY > startY){
                pointCtrl1 = new Point2D.Double(startX, (startY + dragY) / 2 + 40);
                pointCtrl2 = new Point2D.Double((startX + dragX) / 2, dragY + 50);
            } else {
                pointCtrl1 = new Point2D.Double((startX + dragX) / 2, startY + 30);
                pointCtrl2 = new Point2D.Double(dragX, (startY + dragY) / 2 + 40);
            }
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.MAGENTA);
            g2.draw(cubicCurve);
        }
    }

    protected void drawDependency(ParseNodeDrawable parseNode, Graphics g, ParseTreeDrawable tree){
        ParseNodeDrawable toNode;
        int toIG;
        String dependency;
        int startX, startY, dragX, dragY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        if (parseNode.numberOfChildren() == 0 && parseNode.getLayerInfo() != null && parseNode.getLayerData(ViewLayerType.DEPENDENCY) != null){
            String[] words = parseNode.getLayerData(ViewLayerType.DEPENDENCY).split(",");
            toNode = tree.getLeafWithIndex(Integer.parseInt(words[0]));
            toIG = Integer.parseInt(words[1]);
            dependency = words[2];
            startX = parseNode.getArea().getX() + parseNode.getArea().getWidth() / 2;
            startY = parseNode.getArea().getY() + 20;
            dragX = toNode.getArea().getX() + toNode.getArea().getWidth() / 2;
            dragY = toNode.getArea().getY() + 20 * toIG;
            pointStart = new Point2D.Double(startX, startY);
            pointEnd = new Point2D.Double(dragX, dragY);
            if (dragY > startY){
                pointCtrl1 = new Point2D.Double(startX, (startY + dragY) / 2 + 40);
                pointCtrl2 = new Point2D.Double((startX + dragX) / 2, dragY + 50);
            } else {
                pointCtrl1 = new Point2D.Double((startX + dragX) / 2, startY + 30);
                pointCtrl2 = new Point2D.Double(dragX, (startY + dragY) / 2 + 40);
            }
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.RED);
            g.drawString(dependency, (startX + dragX) / 2, Math.max(startY, dragY) + 50);
            g2.draw(cubicCurve);
            g2.setColor(Color.BLACK);
        } else {
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable aChild = (ParseNodeDrawable) parseNode.getChild(i);
                drawDependency(aChild, g, tree);
            }
        }
    }

    protected void paint(ParseTreeDrawable parseTree, Graphics g, int nodeWidth, int nodeHeight, ViewLayerType viewLayer){
        paint(((ParseNodeDrawable)parseTree.getRoot()), g, nodeWidth, nodeHeight, parseTree.maxDepth(), viewLayer);
        drawDependency((ParseNodeDrawable)parseTree.getRoot(), g, parseTree);
    }

    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g) {
        int i, stringSize = 0;
        if (parseNode.numberOfChildren() == 0) {
            if (parseNode.getLayerInfo().getLayerSize(ViewLayerType.INFLECTIONAL_GROUP) == 0){
                return g.getFontMetrics().stringWidth(parseNode.getLayerData(ViewLayerType.TURKISH_WORD));
            }
            for (i = 0; i < parseNode.getLayerInfo().getLayerSize(ViewLayerType.INFLECTIONAL_GROUP); i++)
                try {
                    if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getLayerInfoAt(ViewLayerType.INFLECTIONAL_GROUP, i)) > stringSize){
                        stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getLayerInfoAt(ViewLayerType.INFLECTIONAL_GROUP, i));
                    }
                } catch (LayerNotExistsException | LayerItemNotExistsException | WordNotExistsException e) {
                    return g.getFontMetrics().stringWidth(parseNode.getData().getName());
                }
            return stringSize;
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    protected void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y){
        int i;
        if (parseNode.numberOfChildren() == 0){
            if (parseNode.getLayerInfo().getLayerSize(ViewLayerType.INFLECTIONAL_GROUP) == 0){
                g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
            }
            for (i = 0; i < parseNode.getLayerInfo().getLayerSize(ViewLayerType.INFLECTIONAL_GROUP); i++){
                if (i > 0 && !parseNode.isGuessed()){
                    g.setColor(Color.RED);
                }
                try {
                    g.drawString(parseNode.getLayerInfo().getLayerInfoAt(ViewLayerType.INFLECTIONAL_GROUP, i), x, y);
                    y += 20;
                } catch (LayerNotExistsException | LayerItemNotExistsException | WordNotExistsException e) {
                    g.drawString(parseNode.getData().getName(), x, y);
                }
            }
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize){
        if (parseNode.numberOfChildren() == 0){
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getLayerSize(ViewLayerType.PART_OF_SPEECH) + 1));
        } else {
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
        }
    }

}
