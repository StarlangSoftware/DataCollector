package DataCollector.Sentence.FrameNet;

import WordNet.WordNet;

public class TestSentenceFrameNetElementFrame {

    public static void main(String[] args){
        WordNet turkishWordNet = new WordNet();
        new SentenceFrameNetElementFrame(turkishWordNet);
    }

}
