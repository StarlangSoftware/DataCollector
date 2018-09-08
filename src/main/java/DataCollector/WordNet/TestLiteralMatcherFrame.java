package DataCollector.WordNet;

import WordNet.WordNet;

public class TestLiteralMatcherFrame {

    public static void main(String[] args){
         WordNet turkishWordNet = new WordNet();
         new LiteralMatcherFrame(turkishWordNet);
    }

}
