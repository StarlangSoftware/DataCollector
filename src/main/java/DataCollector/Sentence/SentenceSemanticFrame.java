package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.AutoProcessor.AutoSemantic.TurkishSentenceAutoSemantic;
import DataCollector.ParseTree.EditorPanel;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

public class SentenceSemanticFrame extends AnnotatorFrame {
    private JCheckBox autoSemanticDetectionOption;
    private FsmMorphologicalAnalyzer fsm;
    private WordNet wordNet;
    private HashMap<String, HashSet<String>> exampleSentences;

    public SentenceSemanticFrame(final FsmMorphologicalAnalyzer fsm, final WordNet wordNet){
        super();
        exampleSentences = new HashMap<>();
        AnnotatedCorpus corpus;
        corpus = new AnnotatedCorpus(new File(EditorPanel.phrasePath));
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                String semantic = word.getSemantic();
                if (semantic != null){
                    HashSet<String> sentences;
                    if (exampleSentences.containsKey(semantic)){
                        sentences = exampleSentences.get(semantic);
                    } else {
                        sentences = new HashSet<>();
                    }
                    if (sentences.size() < 20){
                        sentences.add(sentence.toWords());
                    }
                    exampleSentences.put(semantic, sentences);
                }
            }
        }
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
        JMenuItem itemAutoAnnotate = addMenuItem(projectMenu, "Annotate Every Word With Last Sense", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        itemAutoAnnotate.addActionListener(e -> {
            SentenceSemanticPanel current;
            int wordCount = 0, fileCount = 0;
            current = (SentenceSemanticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            AnnotatedWord clickedWord = current.getClickedWord();
            for (int i = 0; i < corpus.sentenceCount(); i++){
                AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
                boolean modified = false;
                for (int j = 0; j < sentence.wordCount(); j++){
                    AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                    String semantic = word.getSemantic();
                    if (word.getName() != null && word.getName().equals(clickedWord.getName()) && semantic == null){
                        wordCount++;
                        modified = true;
                    }
                }
                if (modified){
                    fileCount++;
                }
            }
            int result = JOptionPane.showConfirmDialog(null,
                    wordCount + " words in " + fileCount + " files with text (" + clickedWord.getName() + ") will be modified. Are you sure?",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION){
                for (int i = 0; i < corpus.sentenceCount(); i++){
                    AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
                    boolean modified = false;
                    for (int j = 0; j < sentence.wordCount(); j++){
                        AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                        String semantic = word.getSemantic();
                        if (word.getName() != null && word.getName().equals(clickedWord.getName()) && semantic == null){
                            word.setSemantic(clickedWord.getSemantic());
                            modified = true;
                        }
                    }
                    if (modified){
                        sentence.save();
                    }
                }
            }
        });
        JMenuItem itemShowUnannotated = addMenuItem(projectMenu, "Show Unannotated Files", KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        itemShowUnannotated.addActionListener(e -> {
            int count = 0;
            String result = JOptionPane.showInputDialog(null, "How many sentences you want to see:", "",
                    JOptionPane.PLAIN_MESSAGE);
            int numberOfSentences = Integer.parseInt(result);
            for (int i = 0; i < corpus.sentenceCount(); i++){
                AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
                for (int j = 0; j < sentence.wordCount(); j++){
                    AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                    String semantic = word.getSemantic();
                    if (word.getName() != null && semantic == null){
                        AnnotatorPanel annotatorPanel = generatePanel(EditorPanel.phrasePath, sentence.getFileName());
                        addPanelToFrame(annotatorPanel, sentence.getFileName());
                        count++;
                        if (count == numberOfSentences){
                            return;
                        }
                        break;
                    }
                }
            }
        });
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> {
            new ViewSemanticAnnotationFrame(corpus, this.wordNet, wordNet, this);
        });
        this.fsm = fsm;
        this.wordNet = wordNet;
        autoSemanticDetectionOption = new JCheckBox("Auto Semantic Detection", false);
        toolBar.add(autoSemanticDetectionOption);
    }

    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceSemanticPanel(currentPath, rawFileName, fsm, wordNet, exampleSentences);
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
