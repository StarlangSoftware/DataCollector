package DataCollector.ParseTree;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

public class TurkishSemanticFrame extends EditorFrame{
    private WordNet wordNet;
    private FsmMorphologicalAnalyzer fsm;

    public TurkishSemanticFrame(final WordNet wordNet, final FsmMorphologicalAnalyzer fsm){
        this.setTitle("Turkish Semantic Editor");
        this.wordNet = wordNet;
        this.fsm = fsm;
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TurkishSemanticPanel(currentPath, rawFileName, wordNet, fsm, true);
    }
}
