package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import ParseTree.ParseNode;
import Dictionary.Pos;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.LayerAction;
import WordNet.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class TreeEnglishSemanticPanel extends TreeLeafEditorPanel {

    private WordNet englishWordNet, turkishWordNet;
    private JList list;
    private DefaultListModel listModel;
    private ArrayList<Integer> translatedSideCandidateList;

    public TreeEnglishSemanticPanel(String path, String fileName, WordNet englishWordNet, WordNet turkishWordNet) {
        super(path, fileName, ViewLayerType.ENGLISH_SEMANTICS, false);
        heightDecrease = 280;
        this.turkishWordNet = turkishWordNet;
        this.englishWordNet = englishWordNet;
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setVisible(false);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (list.getSelectedIndex() != -1 && previousNode != null) {
                        previousNode.setSelected(false);
                        LayerAction action = new LayerAction(((TreeEnglishSemanticPanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), ((SynSet) list.getSelectedValue()).getId(), ViewLayerType.ENGLISH_SEMANTICS);
                        actionList.add(action);
                        action.execute();
                        list.setVisible(false);
                        pane.setVisible(false);
                        isEditing = false;
                        repaint();
                    }
                }
            }
        });
        list.setCellRenderer(new ListRenderer());
        list.setFocusTraversalKeysEnabled(false);
        pane = new JScrollPane(list);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (translatedSideCandidateList.contains(index)){
                if (isSelected){
                    setBackground(Color.BLUE);
                } else {
                    setBackground(Color.RED);
                }
            }
            return this;
        }
    }

    public void populateLeaf(ParseNodeDrawable node){
        SynSet selected = null;
        ArrayList<SynSet> synSets, synonymList = null;
        int selectedIndex = -1;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        translatedSideCandidateList = new ArrayList<>();
        listModel.clear();
        String englishWord = node.getLayerData(ViewLayerType.ENGLISH_WORD);
        String englishSemantics = node.getLayerData(ViewLayerType.ENGLISH_SEMANTICS);
        String turkishSemantics = node.getLayerData(ViewLayerType.SEMANTICS);
        if (englishSemantics != null){
            selected = englishWordNet.getSynSetWithId(englishSemantics);
        }
        if (turkishSemantics != null){
            SynSet turkish = turkishWordNet.getSynSetWithId(turkishSemantics);
            if (turkish != null){
                synonymList = turkish.getInterlingual(englishWordNet);
            }
        }
        if (node.getParent().getData().getName().startsWith("V")){
            synSets = englishWordNet.getSynSetsWithPossiblyModifiedLiteral(englishWord, Pos.VERB);
        } else {
            if (node.getParent().getData().getName().startsWith("N")){
                synSets = englishWordNet.getSynSetsWithPossiblyModifiedLiteral(englishWord, Pos.NOUN);
            } else {
                if (node.getParent().getData().getName().startsWith("ADJ") || node.getParent().getData().getName().startsWith("JJ")){
                    synSets = englishWordNet.getSynSetsWithPossiblyModifiedLiteral(englishWord, Pos.ADJECTIVE);
                } else {
                    if (node.getParent().getData().getName().startsWith("RB")){
                        synSets = englishWordNet.getSynSetsWithPossiblyModifiedLiteral(englishWord, Pos.ADVERB);
                    } else {
                        synSets = englishWordNet.getSynSetsWithLiteral(englishWord);
                    }
                }
            }
        }
        ParseTreeDrawable englishTree = new ParseTreeDrawable(englishPath, currentTree.getFileDescription());
        HashMap<ParseNode, ParseNodeDrawable> mapping = englishTree.mapTree(currentTree);
        for (ParseNode parseNode : mapping.keySet()){
            if (mapping.get(parseNode).equals(node)){
                ParseNode previousNode = englishTree.previousLeafNode(parseNode);
                if (previousNode != null){
                    ParseNodeDrawable previous = mapping.get(previousNode);
                    if (previous != null){
                        ArrayList<String> modifiedLiterals = englishWordNet.getLiteralsWithPossibleModifiedLiteral(previous.getLayerData(ViewLayerType.ENGLISH_WORD));
                        for (String modifiedLiteral : modifiedLiterals){
                            synSets.addAll(englishWordNet.getSynSetsWithLiteral(modifiedLiteral + " " + englishWord));
                        }
                    }
                }
                ParseNode nextNode = englishTree.nextLeafNode(parseNode);
                if (nextNode != null){
                    ParseNodeDrawable next = mapping.get(nextNode);
                    if (next != null){
                        ArrayList<String> modifiedLiterals = englishWordNet.getLiteralsWithPossibleModifiedLiteral(englishWord);
                        for (String modifiedLiteral : modifiedLiterals){
                            synSets.addAll(englishWordNet.getSynSetsWithLiteral(modifiedLiteral + " " + next.getLayerData(ViewLayerType.ENGLISH_WORD)));
                        }
                    }
                }
                break;
            }
        }
        for (int i = 0; i < synSets.size(); i++){
            listModel.addElement(synSets.get(i));
            if (synSets.get(i).equals(selected)){
                selectedIndex = i;
            }
        }
        if (synonymList != null){
            for (int i = 0; i < synSets.size(); i++){
                if (synonymList.contains(synSets.get(i))){
                    translatedSideCandidateList.add(i);
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
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 30, 300, 30 + Math.max(3, Math.min(15, listModel.getSize() + 1)) * 18);
        this.repaint();
        isEditing = true;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            if (mouseEvent.isControlDown()){
                node.getLayerInfo().englishSemanticClear();
                save();
            } else {
                populateLeaf(node);
            }
        }
    }


}
