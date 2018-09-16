package DataCollector.Sentence;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import javax.swing.*;

public class SentenceSemanticFrame extends AnnotatorFrame {
    private JCheckBox autoSemanticDetectionOption;
    private FsmMorphologicalAnalyzer fsm;
    private WordNet wordNet;

    public SentenceSemanticFrame(final FsmMorphologicalAnalyzer fsm, final WordNet wordNet){
        super("semantic");
        this.fsm = fsm;
        this.wordNet = wordNet;
        autoSemanticDetectionOption = new JCheckBox("Auto Semantic Detection", false);
        toolBar.add(autoSemanticDetectionOption);
    }

    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceSemanticPanel(currentPath, rawFileName, fsm, wordNet);
    }

    public void next(int count){
        super.next(count);
        SentenceSemanticPanel current;
        current = (SentenceSemanticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoSemanticDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentenceSemanticPanel current;
        current = (SentenceSemanticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoSemanticDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

}
