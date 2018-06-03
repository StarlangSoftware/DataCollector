package DataCollector.Sentence;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

public class TestSentenceMorphologicalAnalyzerFrame {

    public static void main(String[] args){
         FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
         new SentenceMorphologicalAnalyzerFrame(fsm);
    }

}
