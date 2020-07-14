package DataCollector.ParseTree;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.RootWordStatistics;
import AnnotatedTree.AutoProcessor.AutoDisambiguation.TreeAutoDisambiguator;
import AnnotatedTree.AutoProcessor.AutoDisambiguation.TurkishTreeAutoDisambiguator;

import javax.swing.*;

public class TreeMorphologicalAnalyzerFrame extends TreeEditorFrame {
    private JCheckBox autoDisambiguation;
    private RootWordStatistics rootWordStatistics;
    private FsmMorphologicalAnalyzer fsm;

    public TreeMorphologicalAnalyzerFrame(final FsmMorphologicalAnalyzer fsm, final RootWordStatistics rootWordStatistics){
        this.setTitle("Morphological Analyzer");
        this.rootWordStatistics = rootWordStatistics;
        this.fsm = fsm;
        autoDisambiguation = new JCheckBox("AutoDisambiguation", true);
        toolBar.add(autoDisambiguation);
    }

    private void autoDisambiguate(){
        TreeAutoDisambiguator treeAutoDisambiguator;
        if (autoDisambiguation.isSelected()){
            TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            treeAutoDisambiguator = new TurkishTreeAutoDisambiguator(rootWordStatistics);
            treeAutoDisambiguator.autoDisambiguate(current.currentTree);
            current.currentTree.reload();
            current.repaint();
        }
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeMorphologicalAnalyzerPanel(currentPath, rawFileName, fsm, !autoDisambiguation.isSelected());
    }

    protected void nextTree(int count){
        super.nextTree(count);
        autoDisambiguate();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        autoDisambiguate();
    }

}
