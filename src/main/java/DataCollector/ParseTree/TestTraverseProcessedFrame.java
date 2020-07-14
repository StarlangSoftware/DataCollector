package DataCollector.ParseTree;

import AnnotatedTree.TreeBankDrawable;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Translation.AutomaticTranslationDictionary;
import Translation.BilingualDictionary;
import WordNet.WordNet;

import java.io.File;

public class TestTraverseProcessedFrame {

    public static void main(String[] args){
        AutomaticTranslationDictionary automaticTranslationDictionary = new AutomaticTranslationDictionary();
        BilingualDictionary bilingualDictionary = new BilingualDictionary();
        WordNet turkishWordNet = new WordNet();
        WordNet englishWordNet = new WordNet("english_wordnet_version_31.xml");
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TreeEditorPanel.treePath));
        TreeTraverseFrame frame = new TreeTraverseProcessedFrame(new FsmMorphologicalAnalyzer(), treeBank);
        frame.loadTranslationDictionary(automaticTranslationDictionary);
        frame.loadBilingualDictionary(bilingualDictionary);
        frame.loadWordNets(turkishWordNet, englishWordNet);
    }

}
