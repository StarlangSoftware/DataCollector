package DataCollector.Sentence.Dependency;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.DependencyError.DependencyError;
import DataCollector.DataCollector;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.FrameNet.SentenceFrameNetPredicatePanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import DependencyParser.Universal.UniversalDependencyRelation;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

public class SentenceDependencyFrame extends SentenceAnnotatorFrame {

    static final protected String DELETEWORD = "deleteword";
    JList<String> errorList;
    private JCheckBox autoDependencyDetectionOption;

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceDependencyPanel(currentPath, rawFileName);
    }

    public SentenceDependencyFrame(){
        super();
        autoDependencyDetectionOption = new JCheckBox("Auto Dependency Detection", false);
        toolBar.add(autoDependencyDetectionOption);
        JPanel errorPanel = new JPanel(new BorderLayout(50, 0));
        errorList = new JList<>();
        errorPanel.add(errorList);
        bottom.add(errorPanel, BorderLayout.SOUTH);
        errorList.setVisible(false);
        JButton button = new DrawingButton(DataCollector.class, this, "delete", DELETEWORD, "Delete Word");
        button.setVisible(true);
        toolBar.add(button);
        projectPane.addChangeListener(e -> {
            if (projectPane.getTabCount() > 0) {
                showErrors((SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView());
            }
        });
        AnnotatedCorpus corpus;
        corpus = new AnnotatedCorpus(new File(TreeEditorPanel.phrasePath));
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
                    UniversalDependencyRelation universalDependencyRelation = word.getUniversalDependency();
                    if (word.getName() != null && universalDependencyRelation == null){
                        SentenceAnnotatorPanel annotatorPanel = generatePanel(TreeEditorPanel.phrasePath, sentence.getFileName());
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
            new ViewSentenceDependencyAnnotationFrame(corpus, this);
        });
        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "Dependency Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void deleteWord(){
        SentenceDependencyPanel current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.deleteWord();
        }
    }

    protected void showErrors(SentenceDependencyPanel current){
        if (current != null){
            ArrayList<DependencyError> errors = current.sentence.getDependencyErrors();
            if (errors.size() > 0){
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (DependencyError dependencyError : errors){
                    listModel.addElement(dependencyError.toString());
                }
                errorList.setModel(listModel);
                errorList.setVisible(true);
            } else {
                errorList.setVisible(false);
            }
        }
    }

    public void next(int count){
        super.next(count);
        SentenceDependencyPanel current;
        current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoDependencyDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void previous(int count){
        super.previous(count);
        SentenceDependencyPanel current;
        current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoDependencyDetectionOption.isSelected()){
            current.autoDetect();
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()) {
            case DELETEWORD:
                deleteWord();
                break;
            case BACKWARD:
            case FORWARD:
            case FAST_BACKWARD:
            case FAST_FORWARD:
            case FAST_FAST_BACKWARD:
            case FAST_FAST_FORWARD:
                showErrors((SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView());
                break;
        }
    }

}
