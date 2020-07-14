package DataCollector.ParseTree;

import WordNet.WordNet;

public class TestPropbankArgumentFrame {

    public static void main(String[] args){
        WordNet turkish = new WordNet();
        new TreePropbankArgumentFrame(turkish);
    }
}
