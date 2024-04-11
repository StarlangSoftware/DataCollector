package DataCollector.WordNet;

import WordNet.WordNet;

public class TestSynSetSplitFrame {

    public static void main(String[] args){
        WordNet turkishWordNet = new WordNet();
        new SynSetSplitFrame(turkishWordNet);
    }
}
