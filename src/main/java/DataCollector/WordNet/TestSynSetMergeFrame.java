package DataCollector.WordNet;

import WordNet.WordNet;

public class TestSynSetMergeFrame {

    public static void main(String[] args){
         WordNet turkishWordNet = new WordNet();
         new SynSetMergeFrame(turkishWordNet);
    }

}
