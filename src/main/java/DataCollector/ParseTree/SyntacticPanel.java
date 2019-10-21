package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeAction.AddParentAction;
import DataCollector.ParseTree.TreeAction.DeleteNodeAction;
import DataCollector.ParseTree.TreeAction.EditSymbolAction;
import DataCollector.ParseTree.TreeAction.MoveSubtreeAction;
import ParseTree.ParseNode;
import ParseTree.Symbol;
import AnnotatedTree.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SyntacticPanel extends StructureEditorPanel{
    private ParseNodeDrawable draggedNode = null;
    private ParseNodeDrawable fromNode = null;
    private boolean dragged = false;
    private int draggedIndex = -1;

    private JTextField editText;

    public SyntacticPanel(String path, String fileName, ViewLayerType viewLayer) {
        super(path, fileName, viewLayer);
        editText = new JTextField();
        editText.setHorizontalAlignment(JTextField.CENTER);
        editText.setVisible(false);
        editText.addActionListener(actionEvent -> {
            isEditing = false;
            EditSymbolAction action = new EditSymbolAction(SyntacticPanel.this, editableNode,
                    new Symbol(editText.getText()));
            actionList.add(action);
            action.execute();
            editText.setVisible(false);
            editableNode.setEditable(false);
            repaint();
        });
        add(editText);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addParent(){
        if (editableNode != null){
            AddParentAction action = new AddParentAction(this, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    public void editSymbol(){
        if (editableNode != null){
            isEditing = true;
            editText.setText(editableNode.getData().getName());
            Rectangle rect = editableNode.getArea();
            editText.setBounds(rect.x - 20, rect.y - 4, rect.width + 40, rect.height + 8);
            editText.setVisible(true);
            editText.requestFocus();
        }
    }

    public void deleteSymbol(){
        if (editableNode != null && (editableNode.numberOfChildren() != 1 || !editableNode.getChild(0).isLeaf())){
            DeleteNodeAction action = new DeleteNodeAction(this, editableNode);
            action.execute();
            actionList.add(action);
            editableNode.setEditable(false);
            repaint();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        ParseNode node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node == previousNode && previousNode != null){
            fromNode = previousNode;
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (fromNode != null){
            fromNode.setSelected(false);
            fromNode.setEditable(false);
        }
        if (draggedNode != null){
            draggedNode.setDragged(false);
            draggedNode.setSelected(false);
            draggedNode.setEditable(false);
        }
        if (fromNode != null && draggedNode != null && dragged){
            MoveSubtreeAction action = new MoveSubtreeAction(this, currentTree, fromNode, draggedNode, draggedIndex);
            action.execute();
            actionList.add(action);
        }
        fromNode = null;
        draggedNode = null;
        dragged = false;
        this.repaint();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        dragged = true;
        if (node != null && node != previousNode && node.numberOfChildren() > 0 && !node.getChild(0).isLeaf() && !fromNode.isDescendant(node)){
            draggedNode = node;
            draggedIndex = (int) (((draggedNode.numberOfChildren() + 1) * (mouseEvent.getX() - node.getArea().x)) / (node.getArea().width + 0.0));
            draggedNode.setDragged(true, draggedIndex);
            this.repaint();
        } else {
            if (node == null && draggedNode != null){
                draggedNode.setDragged(false);
                draggedNode = null;
                this.repaint();
            }
        }
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null && node != previousNode && !dragged && !isEditing){
            if (previousNode != null)
                previousNode.setSelected(false);
            node.setSelected(true);
            previousNode = node;
            this.repaint();
        } else {
            if (node == null && previousNode != null && !dragged && !isEditing){
                previousNode.setSelected(false);
                previousNode = null;
                this.repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node == null){
            node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
            if (editableNode != null) {
                editableNode.setEditable(false);
            }
            if (node != null){
                editableNode = node;
                editableNode.setEditable(true);
                editText.setVisible(false);
                isEditing = false;
                this.repaint();
                this.setFocusable(true);
            }
        }
    }

}
