package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.Processor.NodeModification.ConvertToLayeredFormat;
import AnnotatedTree.Processor.TreeModifier;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.LayerAction;
import Translation.*;

import javax.swing.*;
import java.awt.event.*;

public class TranslatorPanel extends StructureEditorPanel {

    private JTextField editText;
    private JList list;
    private JScrollPane pane;
    private AutomaticTranslationDictionary dictionary;
    private BilingualDictionary bilingualDictionary;
    private DefaultListModel<TargetPhrase> listModel;
    private String secondLanguagePath;

    public TranslatorPanel(AutomaticTranslationDictionary dictionary, BilingualDictionary bilingualDictionary, String path, String fileName, final ViewLayerType secondLanguage) {
        super(path, fileName, secondLanguage);
        secondLanguagePath = path;
        widthDecrease = 85;
        heightDecrease = 120;
        this.dictionary = dictionary;
        this.bilingualDictionary = bilingualDictionary;
        editText = new JTextField();
        editText.setVisible(false);
        editText.addActionListener(actionEvent -> {
            if (previousNode != null) {
                previousNode.setSelected(false);
                LayerAction action = new LayerAction(((TranslatorPanel)((JTextField) actionEvent.getSource()).getParent()), previousNode.getLayerInfo(), editText.getText(), secondLanguage);
                actionList.add(action);
                action.execute();
                editText.setVisible(false);
                list.setVisible(false);
                pane.setVisible(false);
                isEditing = false;
                repaint();
            }
        });
        add(editText);
        editText.setFocusTraversalKeysEnabled(false);
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setVisible(false);
        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (list.getSelectedIndex() != -1 && previousNode != null) {
                    LayerAction action;
                    previousNode.setSelected(false);
                    if (list.getSelectedValue() instanceof WordTranslationCandidate){
                        WordTranslationCandidate word = (WordTranslationCandidate) list.getSelectedValue();
                        action = new LayerAction(((TranslatorPanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), word.getTranslation(), secondLanguage);
                    } else {
                        if (list.getSelectedValue() instanceof TargetPhrase){
                            TargetPhrase word = (TargetPhrase) list.getSelectedValue();
                            action = new LayerAction(((TranslatorPanel)((JList) listSelectionEvent.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), word.getTranslation(), secondLanguage);
                        } else {
                            action = null;
                        }
                    }
                    actionList.add(action);
                    action.execute();
                    editText.setVisible(false);
                    list.setVisible(false);
                    pane.setVisible(false);
                    isEditing = false;
                    repaint();
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
        editText.setVisible(false);
        list.setVisible(false);
        pane.setVisible(false);
        if (editableNode != null){
            editableNode.setEditable(false);
        }
        editableNode = null;
    }

    protected void nextTree(int count){
        clear();
        if (!currentTree.getFileDescription().nextFileExists(count) && currentTree.getFileDescription().nextFileExists(ENGLISH_PATH, count)){
            ParseTreeDrawable parseTree = new ParseTreeDrawable(ENGLISH_PATH, currentTree.getFileDescription().getExtension(), currentTree.getFileDescription().getIndex() + 1);
            TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
            treeModifier.modify();
            parseTree.saveWithPath(secondLanguagePath);
        }
        super.nextTree(count);
    }

    protected void previousTree(int count){
        clear();
        if (!currentTree.getFileDescription().previousFileExists(count) && currentTree.getFileDescription().previousFileExists(ENGLISH_PATH, count)){
            ParseTreeDrawable parseTree = new ParseTreeDrawable(ENGLISH_PATH, currentTree.getFileDescription().getExtension(), currentTree.getFileDescription().getIndex() - 1);
            TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
            treeModifier.modify();
            parseTree.saveWithPath(secondLanguagePath);
        }
        super.previousTree(count);
    }

    public void populateLeaf(ParseNodeDrawable node){
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        listModel.clear();
        if (dictionary != null && node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD) != null && dictionary.getWord(node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD).toLowerCase()) != null){
            WordTranslations word = (WordTranslations) dictionary.getWord(node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD).toLowerCase());
            word.sortTranslations();
            for (TargetPhrase targetPhrase:word.getTranslations()){
                WordTranslationCandidate translation = (WordTranslationCandidate) targetPhrase;
                listModel.addElement(translation);
            }
        }
        if (bilingualDictionary != null && node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD) != null){
            String english = node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD).toLowerCase();
            SourceWord word = (SourceWord) bilingualDictionary.getWord(english);
            if (word == null) {
                switch (node.getParent().getData().getName()) {
                    case "NNS":
                        word = bilingualDictionary.inPluralForm(english);
                        break;
                    case "VBG":
                        word = bilingualDictionary.inIngForm(english);
                        break;
                    case "VBN":
                    case "VBD":
                        word = bilingualDictionary.inPastForm(english);
                        break;
                    case "VBZ":
                        word = bilingualDictionary.inThirdPersonForm(english);
                        break;
                }
            }
            if (word != null){
                for (TargetPhrase targetPhrase:word.getTranslations()){
                    listModel.addElement(targetPhrase);
                }
            }
        }
        list.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        editText.setToolTipText(node.getLayerInfo().getLayerData(ViewLayerType.ENGLISH_WORD));
        editText.setText(node.getLayerInfo().getLayerData(ViewLayerType.TURKISH_WORD));
        editText.setBounds(node.getArea().x - 5, node.getArea().y + 20, 100, 30);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 50, 100, 90);
        editText.setVisible(true);
        editText.requestFocus();
        isEditing = true;
        this.repaint();
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
                editText.setVisible(false);
                list.setVisible(false);
                pane.setVisible(false);
                isEditing = false;
                this.repaint();
                this.setFocusable(true);
            }
        }
    }

}
