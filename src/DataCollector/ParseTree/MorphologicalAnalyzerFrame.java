package DataCollector.ParseTree;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.RootWordStatistics;
import AnnotatedTree.AutoProcessor.AutoDisambiguation.TreeAutoDisambiguator;
import AnnotatedTree.AutoProcessor.AutoDisambiguation.TurkishTreeAutoDisambiguator;

import javax.swing.*;

public class MorphologicalAnalyzerFrame extends EditorFrame {
    private JCheckBox autoDisambiguation;
    private RootWordStatistics rootWordStatistics;
    private FsmMorphologicalAnalyzer fsm;

    public MorphologicalAnalyzerFrame(final FsmMorphologicalAnalyzer fsm, final RootWordStatistics rootWordStatistics){
        this.setTitle("Morphological Analyzer");
        this.rootWordStatistics = rootWordStatistics;
        this.fsm = fsm;
        autoDisambiguation = new JCheckBox("AutoDisambiguation", true);
        toolBar.add(autoDisambiguation);
    }

    private void autoDisambiguate(){
        TreeAutoDisambiguator treeAutoDisambiguator;
        if (autoDisambiguation.isSelected()){
            EditorPanel current = (EditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            treeAutoDisambiguator = new TurkishTreeAutoDisambiguator(rootWordStatistics);
            treeAutoDisambiguator.autoDisambiguate(current.currentTree);
            current.currentTree.reload();
            current.repaint();
        }
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new MorphologicalAnalyzerPanel(currentPath, rawFileName, fsm, !autoDisambiguation.isSelected());
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
