package DataCollector.ParseTree;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

public class TreeTurkishSemanticFrame extends TreeEditorFrame {
    private WordNet wordNet;
    private FsmMorphologicalAnalyzer fsm;

    public TreeTurkishSemanticFrame(final WordNet wordNet, final FsmMorphologicalAnalyzer fsm){
        this.setTitle("Turkish Semantic Editor");
        this.wordNet = wordNet;
        this.fsm = fsm;
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeTurkishSemanticPanel(currentPath, rawFileName, wordNet, fsm, true);
    }
}
