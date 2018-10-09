package DataCollector.Sentence;

import PropBank.FramesetList;
import WordNet.WordNet;

import javax.swing.*;

public class SentencePropbankPredicateFrame extends AnnotatorFrame {
    private JCheckBox autoPredicateDetectionOption;
    private FramesetList xmlParser;
    private WordNet wordNet;

    public SentencePropbankPredicateFrame() {
        super("propbank");
        autoPredicateDetectionOption = new JCheckBox("Auto Predicate Detection", false);
        wordNet = new WordNet();
        toolBar.add(autoPredicateDetectionOption);
        xmlParser = new FramesetList("frameset.xml");
    }

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentencePropbankPredicatePanel(currentPath, rawFileName, xmlParser, wordNet);
    }

    public void next(int count){
        super.next(count);
        SentencePropbankPredicatePanel current;
        current = (SentencePropbankPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentencePropbankPredicatePanel current;
        current = (SentencePropbankPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

}
