package DataCollector.ParseTree;

import AnnotatedTree.TreeBankDrawable;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class TreeTurkishSemanticFrame extends TreeEditorFrame {
    private WordNet wordNet;
    private FsmMorphologicalAnalyzer fsm;

    public TreeTurkishSemanticFrame(final WordNet wordNet, final FsmMorphologicalAnalyzer fsm){
        this.setTitle("Turkish Semantic Editor");
        this.wordNet = wordNet;
        this.fsm = fsm;
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TreeEditorPanel.treePath));
        JMenuItem itemViewAnnotations = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotations.addActionListener(e -> {
            new ViewTreeSemanticAnnotationFrame(treeBank, wordNet, fsm, this);
        });
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeTurkishSemanticPanel(currentPath, rawFileName, wordNet, fsm, true);
    }
}
