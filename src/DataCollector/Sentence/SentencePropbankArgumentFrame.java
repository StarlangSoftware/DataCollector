package DataCollector.Sentence;

import PropBank.FramesetList;
import WordNet.WordNet;

import javax.swing.*;

public class SentencePropbankArgumentFrame extends AnnotatorFrame{
    private JCheckBox autoArgumentDetectionOption;
    private FramesetList xmlParser;
    private WordNet wordNet;

    public SentencePropbankArgumentFrame(final WordNet wordNet) {
        super("propbank");
        this.wordNet = wordNet;
        xmlParser = new FramesetList("frameset.xml");
        autoArgumentDetectionOption = new JCheckBox("Auto Argument Detection", false);
        toolBar.add(autoArgumentDetectionOption);
    }

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentencePropbankArgumentPanel(currentPath, rawFileName, wordNet, xmlParser);
    }

    public void next(){
        super.next();
        SentencePropbankArgumentPanel current;
        current = (SentencePropbankArgumentPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoArgumentDetectionOption.isSelected() && current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

    public void previous(){
        super.previous();
        SentencePropbankArgumentPanel current;
        current = (SentencePropbankArgumentPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoArgumentDetectionOption.isSelected() && current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

}
