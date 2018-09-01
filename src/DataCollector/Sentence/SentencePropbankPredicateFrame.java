package DataCollector.Sentence;

import PropBank.FramesetList;

import javax.swing.*;

public class SentencePropbankPredicateFrame extends AnnotatorFrame {
    private JCheckBox autoPredicateDetectionOption;
    private FramesetList xmlParser;

    public SentencePropbankPredicateFrame() {
        super("propbank");
        autoPredicateDetectionOption = new JCheckBox("Auto Predicate Detection", false);
        xmlParser = new FramesetList("frameset.xml");
        toolBar.add(autoPredicateDetectionOption);
    }

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentencePropbankPredicatePanel(currentPath, rawFileName, xmlParser);
    }

    public void next(){
        super.next();
        SentencePropbankPredicatePanel current;
        current = (SentencePropbankPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

    public void previous(){
        super.previous();
        SentencePropbankPredicatePanel current;
        current = (SentencePropbankPredicatePanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoPredicateDetectionOption.isSelected() && !current.sentence.containsPredicate()){
            current.autoDetect();
        }
    }

}
