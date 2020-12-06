package DataCollector.Sentence.NER;

import AnnotatedSentence.AnnotatedCorpus;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class SentenceNERFrame extends SentenceAnnotatorFrame {
    private JCheckBox autoNERDetectionOption;

    public SentenceNERFrame(){
        super();
        AnnotatedCorpus corpus;
        corpus = new AnnotatedCorpus(new File(TreeEditorPanel.phrasePath));
        autoNERDetectionOption = new JCheckBox("Auto Named Entity Recognition", false);
        toolBar.add(autoNERDetectionOption);
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> {
            new ViewSentenceNERAnnotationFrame(corpus, this);
        });
        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "Named Entity Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceNERPanel(currentPath, rawFileName);
    }

    public void next(int count){
        super.next(count);
        SentenceNERPanel current;
        current = (SentenceNERPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoNERDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentenceNERPanel current;
        current = (SentenceNERPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoNERDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

}
