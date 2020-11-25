package DataCollector.ParseTree.Semantic;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

public class TestTurkishSemanticFrame {

    public static void main(String[] args){
        WordNet turkishWordNet = new WordNet();
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        new TreeTurkishSemanticFrame(turkishWordNet, fsm);
    }
}
