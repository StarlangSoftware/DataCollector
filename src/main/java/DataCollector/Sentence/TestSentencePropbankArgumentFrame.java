package DataCollector.Sentence;
import WordNet.WordNet;

public class TestSentencePropbankArgumentFrame {

    public static void main(String[] args){
         WordNet turkishWordNet = new WordNet();
         new SentencePropbankArgumentFrame(turkishWordNet);
    }
}
