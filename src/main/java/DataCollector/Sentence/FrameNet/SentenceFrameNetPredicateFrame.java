package DataCollector.Sentence.FrameNet;

import DataCollector.Sentence.Propbank.SentencePropbankPredicatePanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import FrameNet.FrameNet;
import PropBank.FramesetList;
import WordNet.WordNet;

import javax.swing.*;

public class SentenceFrameNetPredicateFrame extends SentenceAnnotatorFrame {
    private JCheckBox autoPredicateDetectionOption;
    private FrameNet frameNet;
    private WordNet wordNet;

    public SentenceFrameNetPredicateFrame() {
        super();
        autoPredicateDetectionOption = new JCheckBox("Auto Predicate Detection", false);
        toolBar.add(autoPredicateDetectionOption);
        wordNet = new WordNet();
        frameNet = new FrameNet();
        JOptionPane.showMessageDialog(this, "WordNet and frameNet are loaded!", "Predicate Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceFrameNetPredicatePanel(currentPath, rawFileName, frameNet, wordNet);
    }

    public void next(int count){
        super.next(count);
        SentenceFrameNetPredicatePanel current;
        current = (SentenceFrameNetPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsFramePredicate()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentenceFrameNetPredicatePanel current;
        current = (SentenceFrameNetPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsFramePredicate()){
            current.autoDetect();
        }
    }


}
