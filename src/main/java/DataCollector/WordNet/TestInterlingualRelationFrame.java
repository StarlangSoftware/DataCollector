package DataCollector.WordNet;

import WordNet.WordNet;

import java.util.Locale;

public class TestInterlingualRelationFrame {
    public static void main(String[] args){
         WordNet turkishWordNet = new WordNet();
         WordNet englishWordNet = new WordNet("Data/Wordnet/english_wordnet_version_31.xml", "Data/Wordnet/english_exception.xml", new Locale("en"));
         new InterlingualRelationFrame(englishWordNet, turkishWordNet);
    }

}
