package DataCollector.Sentence;

import AnnotatedSentence.AutoProcessor.AutoDisambiguation.TurkishSentenceAutoDisambiguator;
import AnnotatedSentence.AutoProcessor.AutoSemantic.TurkishSentenceAutoSemantic;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.RootWordStatistics;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class SentenceMorphologicalAnalyzerFrame extends AnnotatorFrame{
    private JCheckBox autoAnalysisDetectionOption;
    private FsmMorphologicalAnalyzer fsm;
    private TurkishSentenceAutoDisambiguator turkishSentenceAutoDisambiguator;

    public SentenceMorphologicalAnalyzerFrame(final FsmMorphologicalAnalyzer fsm){
        super("mor");
        JMenuItem itemUpdateDictionary = addMenuItem(projectMenu, "Update Analyzer", KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        itemUpdateDictionary.addActionListener(e -> {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(new File("config.properties")));
                String domainPrefix = properties.getProperty("domainPrefix");
                String domainDictionaryFileName = domainPrefix + "_dictionary.txt";
                String rootWordStatisticsFileName = domainPrefix + "_statistics.bin";
                this.fsm = new FsmMorphologicalAnalyzer(domainDictionaryFileName);
                turkishSentenceAutoDisambiguator = new TurkishSentenceAutoDisambiguator(this.fsm, new RootWordStatistics(new FileInputStream(rootWordStatisticsFileName)));
            } catch (IOException f) {
            }
            for (int i = 0; i < projectPane.getTabCount(); i++){
                SentenceMorphologicalAnalyzerPanel current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getComponentAt(i)).getViewport().getView();
                current.setFsm(this.fsm);
                current.setTurkishSentenceAutoDisambiguator(turkishSentenceAutoDisambiguator);
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

    public void next(int count){
        super.next(count);
        SentenceMorphologicalAnalyzerPanel current;
        current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoAnalysisDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentenceMorphologicalAnalyzerPanel current;
        current = (SentenceMorphologicalAnalyzerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoAnalysisDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

}
