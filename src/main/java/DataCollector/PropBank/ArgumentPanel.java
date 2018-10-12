package DataCollector.PropBank;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.LeafEditorPanel;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.ParseTree.TreeAction.LayerAction;
import PropBank.ArgumentType;
import PropBank.FramesetList;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

public class ArgumentPanel extends LeafEditorPanel {

    private JList list;
    private DefaultListModel<ArgumentType> listModel;
    private String synsetId;

    public ArgumentPanel(String path, String fileName, ViewLayerType viewLayerType, String synsetId, ParseTreeDrawable currentTree) {
        super(path, fileName, viewLayerType, false);
        this.synsetId = synsetId;
        nodeWidth = 80;
        if (currentTree != null){
            currentTree.copyInfo(this.currentTree);
            this.currentTree = currentTree;
        }
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setVisible(false);
        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (list.getSelectedIndex() != -1 && previousNode != null) {
                    previousNode.setSelected(false);
                    if (previousNode.isLeaf()){
                        LayerAction action = new LayerAction(((ArgumentPanel) ((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), ArgumentType.valueOf(list.getSelectedValue().toString()).toString() + "$" + synsetId, ViewLayerType.PROPBANK);
                        actionList.add(action);
                        action.execute();
                    } else {
                        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector(previousNode, new IsTurkishLeafNode());
                        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
                        for (ParseNodeDrawable leafNode : leafList){
                            LayerAction action = new LayerAction(((ArgumentPanel) ((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), leafNode.getLayerInfo(), ArgumentType.valueOf(list.getSelectedValue().toString()).toString() + "$" + synsetId, ViewLayerType.PROPBANK);
                            actionList.add(action);
                            action.execute();
                        }
                    }
                    list.setVisible(false);
                    pane.setVisible(false);
                    isEditing = false;
                    repaint();
                }
            }
        });
        list.setFocusTraversalKeysEnabled(false);
        pane = new JScrollPane(list);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            populateLeaf(node);
        }
    }

    public void setViewLayerType(ViewLayerType viewLayerType){
        if (viewLayerType.equals(ViewLayerType.PROPBANK) || viewLayerType.equals(ViewLayerType.ENGLISH_PROPBANK)){
            viewerLayer = viewLayerType;
            this.repaint();
        }
    }

    public void populateLeaf(ParseNodeDrawable node){
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        listModel.clear();
        try {
           FramesetList xmlParser = new FramesetList();
            Map<ArgumentType, String> map = xmlParser.readFromXML(synsetId);
            for (int i = 0; i < map.size(); i++){
                listModel.addElement((ArgumentType) map.keySet().toArray()[i]);
            }
            listModel.addElement(ArgumentType.NONE);
            listModel.addElement(ArgumentType.PREDICATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 30, 200, 90);
        this.repaint();
        isEditing = true;
    }

}




