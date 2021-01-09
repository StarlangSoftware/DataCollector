package DataCollector.WordNet;

import WordNet.WordNet;

import java.util.Locale;

public class TestInterlingualRelationFrame {
    public static void main(String[] args){
         WordNet turkishWordNet = new WordNet();
         WordNet englishWordNet = new WordNet("english_wordnet_version_31.xml", "english_exception.xml", new Locale("en"));
         new InterlingualRelationFrame(englishWordNet, turkishWordNet);
    }

}
