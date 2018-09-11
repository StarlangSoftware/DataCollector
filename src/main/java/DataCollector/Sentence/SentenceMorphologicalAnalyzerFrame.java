package DataCollector.Sentence;

import AnnotatedSentence.AutoProcessor.AutoDisambiguation.TurkishSentenceAutoDisambiguator;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.RootWordStatistics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SentenceMorphologicalAnalyzerFrame extends AnnotatorFrame{
    private JCheckBox autoAnalysisDetectionOption;
    private FsmMorphologicalAnalyzer fsm;
    private TurkishSentenceAutoDisambiguator turkishSentenceAutoDisambiguator;

    public SentenceMorphologicalAnalyzerFrame(final FsmMorphologicalAnalyzer fsm){
        super("mor");
        JMenuItem itemUpdateDictionary = addMenuItem(projectMenu, "Update Analyzer", KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        itemUpdateDictionary.addActionListener(e -> {
            this.fsm = new FsmMorphologicalAnalyzer("turkish_finite_state_machine.xml", new TxtDictionary("turkish_dictionary.txt", new TurkishWordComparator()));
            for (int i = 0; i < projectPane.getTabCount(); i++){
                SentenceMorphologicalAnalyzerPanel current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getComponentAt(i)).getViewport().getView();
                current.setFsm(this.fsm);
            }
        });
        autoAnalysisDetectionOption = new JCheckBox("Auto Morphological Disambiguation", false);
        toolBar.add(autoAnalysisDetectionOption);
        this.fsm = fsm;
        turkishSentenceAutoDisambiguator = new TurkishSentenceAutoDisambiguator(new RootWordStatistics("rootwordstatistics.bin"));
    }

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceMorphologicalAnalyzerPanel(currentPath, rawFileName, fsm, turkishSentenceAutoDisambiguator);
    }

    public void next(){
        super.next();
        SentenceMorphologicalAnalyzerPanel current;
        current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoAnalysisDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void previous(){
        super.previous();
        SentenceMorphologicalAnalyzerPanel current;
        current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoAnalysisDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

}
