package DataCollector.Sentence;

import AnnotatedSentence.AutoProcessor.AutoSemantic.TurkishSentenceAutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class SentenceSemanticFrame extends AnnotatorFrame {
    private JCheckBox autoSemanticDetectionOption;
    private FsmMorphologicalAnalyzer fsm;
    private WordNet wordNet;

    public SentenceSemanticFrame(final FsmMorphologicalAnalyzer fsm, final WordNet wordNet){
        super("semantic");
        JMenuItem itemUpdateDictionary = addMenuItem(projectMenu, "Update Wordnet", KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        itemUpdateDictionary.addActionListener(e -> {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(new File("config.properties")));
                String domainPrefix = properties.getProperty("domainPrefix");
                String domainWordNetFileName = domainPrefix + "_wordnet.xml";
                String domainDictionaryFileName = domainPrefix + "_dictionary.txt";
                this.fsm = new FsmMorphologicalAnalyzer(domainDictionaryFileName);
                this.wordNet = new WordNet(domainWordNetFileName, new Locale("tr"));
                TurkishSentenceAutoSemantic turkishSentenceAutoSemantic = new TurkishSentenceAutoSemantic(this.wordNet, this.fsm);
                for (int i = 0; i < projectPane.getTabCount(); i++){
                    SentenceSemanticPanel current = (SentenceSemanticPanel) ((JScrollPane) projectPane.getComponentAt(i)).getViewport().getView();
                    current.setFsm(this.fsm);
                    current.setWordnet(this.wordNet);
                    current.setTurkishSentenceAutoSemantic(turkishSentenceAutoSemantic);
                }
            } catch (IOException f) {
            }
        });
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
