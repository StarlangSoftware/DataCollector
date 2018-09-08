package DataCollector.WordNet;

import WordNet.WordNet;

public class TestSynSetMatcherFrame {

    public static void main(String[] args){
        WordNet turkishWordNet = new WordNet();
        new SynSetMatcherFrame(turkishWordNet);
    }
}
