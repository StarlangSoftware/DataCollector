package DataCollector.ParseTree.Propbank;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import DataCollector.ParseTree.TreeAction.LayerAction;
import DataCollector.ParseTree.TreeLeafEditorPanel;
import Dictionary.Pos;
import PropBank.Argument;
import WordNet.WordNet;

import javax.swing.*;

public class TreePropbankPredicatePanel extends TreeLeafEditorPanel {
    private WordNet wordNet;
    private JList list;
    private DefaultListModel listModel;

    public TreePropbankPredicatePanel(String path, String fileName, WordNet wordNet) {
        super(path, fileName, ViewLayerType.PROPBANK, false);
        this.wordNet = wordNet;
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setVisible(false);
        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (list.getSelectedIndex() != -1 && previousNode != null) {
                    previousNode.setSelected(false);
                    LayerAction action = new LayerAction(((TreePropbankPredicatePanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), list.getSelectedValue().toString(), ViewLayerType.PROPBANK);
                    actionList.add(action);
                    action.execute();
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

    public void populateLeaf(ParseNodeDrawable node){
        int selectedIndex = -1;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        listModel.clear();
        listModel.addElement(new Argument("NONE", null));
        if (node.getLayerData(ViewLayerType.SEMANTICS) != null){
            String semantics = node.getLayerData(ViewLayerType.SEMANTICS);
            if (node.getLayerInfo().getNumberOfMeanings() == 1 && wordNet.getSynSetWithId(semantics) != null && wordNet.getSynSetWithId(semantics).getPos().equals(Pos.VERB)){
                listModel.addElement(new Argument("PREDICATE", semantics));
                if (node.getLayerInfo().getArgument() != null){
                    if (node.getLayerInfo().getArgument().getArgumentType().equals("PREDICATE") && node.getLayerInfo().getArgument().getId().equals(semantics)){
                        selectedIndex = 1;
                    }
                    if (node.getLayerInfo().getArgument().getArgumentType().equals("NONE")){
                        selectedIndex = 0;
                    }
                }
            }
        }
        if (selectedIndex != -1){
            list.setValueIsAdjusting(true);
            list.setSelectedIndex(selectedIndex);
        }
        list.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 30, 200, 90);
        this.repaint();
        isEditing = true;
    }

}
