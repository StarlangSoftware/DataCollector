package DataCollector.ParseTree;

import WordNet.WordNet;

public class TestPropbankFrame {

    public static void main(String[] args){
        WordNet turkish = new WordNet();
        new PropbankFrame(turkish);
    }
}
