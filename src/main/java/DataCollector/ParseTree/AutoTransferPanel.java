package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import Dictionary.Word;
import AnnotatedTree.*;
import AnnotatedTree.AutoProcessor.AutoTransfer.TransferredSentence;
import AnnotatedTree.AutoProcessor.AutoTransfer.TurkishAutoTransfer;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.Processor.NodeModification.ConvertToLayeredFormat;
import AnnotatedTree.Processor.TreeModifier;
import DataCollector.ParseTree.TreeAction.LayerAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class AutoTransferPanel extends StructureEditorPanel{
    private JList list;
    private JScrollPane pane;
    private DefaultListModel listModel;
    private TransferredSentence currentSentence;

    public AutoTransferPanel(String path, String fileName, final ViewLayerType secondLanguage) {
        super(path, fileName, secondLanguage);
        currentSentence = new TransferredSentence(new File(currentTree.getFileDescription().getFileName(EditorPanel.phrasePath)));
        widthDecrease = 85;
        heightDecrease = 120;
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setVisible(false);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (list.getSelectedIndex() != -1 && previousNode != null) {
                        LayerAction action;
                        previousNode.setSelected(false);
                        AutoTransferPanel currentPanel = ((AutoTransferPanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent());
                        action = new LayerAction(currentPanel, previousNode.getLayerInfo(), list.getSelectedValue().toString(), secondLanguage);
                        actionList.add(action);
                        action.execute();
                        ((AutoTransferFrame) SwingUtilities.getWindowAncestor(currentPanel)).updateInfo();
                        list.setVisible(false);
                        pane.setVisible(false);
                        isEditing = false;
                        repaint();
                    }
                }
            }
        });
        pane = new JScrollPane(list);
        add(pane);
        list.setFocusable(false);
        pane.setFocusable(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void clear(){
        list.setVisible(false);
        pane.setVisible(false);
        if (editableNode != null){
            editableNode.setEditable(false);
        }
        editableNode = null;
    }

    protected void nextTree(int count){
        clear();
        if (!currentTree.getFileDescription().nextFileExists(count) && currentTree.getFileDescription().nextFileExists(englishPath, count)){
            ParseTreeDrawable parseTree = new ParseTreeDrawable(EditorPanel.englishPath, currentTree.getFileDescription().getExtension(), currentTree.getFileDescription().getIndex() + 1);
            TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
            treeModifier.modify();
            TurkishAutoTransfer turkishAutoTransfer = new TurkishAutoTransfer();
            turkishAutoTransfer.autoTransfer(parseTree, new TransferredSentence(new File(parseTree.getFileDescription().getFileName(EditorPanel.phrasePath))));
            parseTree.saveWithPath(EditorPanel.treePath2);
        }
        super.nextTree(count);
        currentSentence = new TransferredSentence(new File(currentTree.getFileDescription().getFileName(EditorPanel.phrasePath)));
    }

    protected void previousTree(int count){
        clear();
        if (!currentTree.getFileDescription().previousFileExists(count) && currentTree.getFileDescription().previousFileExists(englishPath, count)){
            ParseTreeDrawable parseTree = new ParseTreeDrawable(EditorPanel.englishPath, currentTree.getFileDescription().getExtension(), currentTree.getFileDescription().getIndex() - 1);
            TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
            treeModifier.modify();
            TurkishAutoTransfer turkishAutoTransfer = new TurkishAutoTransfer();
            turkishAutoTransfer.autoTransfer(parseTree, new TransferredSentence(new File(parseTree.getFileDescription().getFileName(EditorPanel.phrasePath))));
            parseTree.saveWithPath(EditorPanel.treePath2);
        }
        super.previousTree(count);
        currentSentence = new TransferredSentence(new File(currentTree.getFileDescription().getFileName(EditorPanel.phrasePath)));
    }

    public void populateLeaf(ParseNodeDrawable node){
        int previousIndex = 0, nextIndex = currentSentence.wordCount() - 1;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        listModel.clear();
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) currentTree.getRoot(), new IsLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        boolean status = true;
        for (ParseNodeDrawable leafNode : leafList){
            if (leafNode.equals(node)){
                status = false;
            } else {
                if (new IsTurkishLeafNode().satisfies(leafNode)){
                    String[] wordList = leafNode.getLayerData(ViewLayerType.TURKISH_WORD).split(" ");
                    if (status){
                        previousIndex = currentSentence.getIndex(new Word(wordList[wordList.length - 1])) + 1;
                    } else {
                        nextIndex = currentSentence.getIndex(new Word(wordList[0])) - 1;
                        break;
                    }
                }
            }
        }
        listModel.addElement("*NONE*");
        while (previousIndex <= nextIndex){
            listModel.addElement(currentSentence.getWord(previousIndex).getName());
            if (previousIndex + 1 <= nextIndex){
                listModel.addElement(currentSentence.getWord(previousIndex).getName() + " " + currentSentence.getWord(previousIndex + 1).getName());
            }
            if (previousIndex + 2 <= nextIndex){
                listModel.addElement(currentSentence.getWord(previousIndex).getName() + " " + currentSentence.getWord(previousIndex + 1).getName() + " " + currentSentence.getWord(previousIndex + 2).getName());
            }
            if (previousIndex + 3 <= nextIndex){
                listModel.addElement(currentSentence.getWord(previousIndex).getName() + " " + currentSentence.getWord(previousIndex + 1).getName() + " " + currentSentence.getWord(previousIndex + 2).getName() + " " + currentSentence.getWord(previousIndex + 3).getName());
            }
            previousIndex++;
        }
        list.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 50, 100, 90);
        isEditing = true;
        this.repaint();
    }

    public TransferredSentence getCurrentSentence(){
        return currentSentence;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        ParseNodeDrawable node = currentTree.getLeafNodeAt(mouseEvent.getX(), mouseEvent.getY());
        if (node != null){
            populateLeaf(node);
        } else {
            node = currentTree.getNodeAt(mouseEvent.getX(), mouseEvent.getY());
            if (node != null){
                if (editableNode != null)
                    editableNode.setEditable(false);
                editableNode = node;
                editableNode.setEditable(true);
                isEditing = false;
                list.setVisible(false);
                pane.setVisible(false);
                this.repaint();
                this.setFocusable(true);
            }
        }
    }


}
