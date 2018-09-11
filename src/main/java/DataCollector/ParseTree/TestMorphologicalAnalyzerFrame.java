package DataCollector.ParseTree;

import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.RootWordStatistics;

public class TestMorphologicalAnalyzerFrame {

    public static void main(String[] args){
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        RootWordStatistics rootWordStatistics = new RootWordStatistics("rootwordstatistics.bin");
        new MorphologicalAnalyzerFrame(fsm, rootWordStatistics);
    }
}
