package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import DataCollector.ParseTree.EditorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class SentenceDependencyFrame extends AnnotatorFrame {

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceDependencyPanel(currentPath, rawFileName);
    }

    public SentenceDependencyFrame(){
        super("dependency");
        AnnotatedCorpus corpus;
        corpus = new AnnotatedCorpus(new File(EditorPanel.TURKISH_PHRASE_PATH));
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> {
            new ViewDependencyAnnotationFrame(corpus);
        });
    }

}
