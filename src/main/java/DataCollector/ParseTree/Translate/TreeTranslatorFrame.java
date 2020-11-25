package DataCollector.ParseTree.Translate;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.*;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.LayerExist.NotContainsLayerInformation;
import AnnotatedTree.Processor.NodeModification.ConvertToLayeredFormat;
import AnnotatedTree.AutoProcessor.AutoTranslation.AutoTranslator;
import AnnotatedTree.AutoProcessor.AutoTranslation.PersianAutoTranslator;
import AnnotatedTree.AutoProcessor.AutoTranslation.TurkishAutoTranslator;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.ParseTree.TreeStructureEditorFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class TreeTranslatorFrame extends TreeStructureEditorFrame {
    private JCheckBox autoTranslationOption;
    private ViewLayerType secondLanguage;
    private String secondLanguagePath;

    public TreeTranslatorFrame(final String secondLanguagePath, final ViewLayerType secondLanguage){
        this.setTitle("Translator");
        this.secondLanguage = secondLanguage;
        this.secondLanguagePath = secondLanguagePath;
        autoTranslationOption = new JCheckBox("AutoTranslator", false);
        toolBar.add(autoTranslationOption);
        itemOpen.removeActionListener(itemOpen.getActionListeners()[itemOpen.getActionListeners().length - 1]);
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(TreeEditorPanel.englishPath));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = new File(secondLanguagePath + "/" + fcinput.getSelectedFile().getName());
                if (!f.exists()) {
                    ParseTreeDrawable parseTree = new ParseTreeDrawable(TreeEditorPanel.englishPath, fcinput.getSelectedFile().getName());
                    TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
                    treeModifier.modify();
                    parseTree.saveWithPath(secondLanguagePath);
                }
                TreeTranslatorPanel translatorPanel = new TreeTranslatorPanel(dictionary, bilingualDictionary, secondLanguagePath, fcinput.getSelectedFile().getName(), secondLanguage);
                addPanelToFrame(translatorPanel, fcinput.getSelectedFile().getName());
            }
        });
    }

    private void autoTranslate(){
        AutoTranslator autoTranslator;
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            if (autoTranslationOption.isSelected()){
                NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) current.currentTree.getRoot(), new IsLeafNode());
                ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
                switch (secondLanguage){
                    case PERSIAN_WORD:
                        if (!new NotContainsLayerInformation(ViewLayerType.PERSIAN_WORD).satisfies(leafList)){
                            return;
                        }
                        autoTranslator = new PersianAutoTranslator(dictionary, bilingualDictionary);
                        break;
                    case TURKISH_WORD:
                    default:
                        if (!new NotContainsLayerInformation(ViewLayerType.TURKISH_WORD).satisfies(leafList)){
                            return;
                        }
                        autoTranslator = new TurkishAutoTranslator(dictionary, bilingualDictionary);
                        break;
                }
                autoTranslator.autoTranslate(current.currentTree);
                current.currentTree.save();
                current.currentTree.reload();
                current.repaint();
            }
        }
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeTranslatorPanel(dictionary, bilingualDictionary, secondLanguagePath, rawFileName, secondLanguage);
    }

    protected void nextTree(int count){
        super.nextTree(count);
        autoTranslate();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        autoTranslate();
    }

}
