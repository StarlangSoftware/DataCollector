package DataCollector.ParseTree;

import java.util.Locale;

import WordNet.WordNet;

public class TestEnglishSemanticFrame {

    public static void main(String[] args){
        WordNet englishWordNet = new WordNet("english_wordnet_version_31.xml");
        WordNet turkishWordNet = new WordNet();
        new EnglishSemanticFrame(englishWordNet, turkishWordNet);
    }

}
