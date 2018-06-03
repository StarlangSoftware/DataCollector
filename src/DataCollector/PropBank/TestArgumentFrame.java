package DataCollector.PropBank;

import AnnotatedSentence.LayerNotExistsException;
import DataCollector.ParseTree.TranslatorPanel;
import AnnotatedTree.WordNotExistsException;
import AnnotatedTree.TreeBankDrawable;
import WordNet.WordNet;

import java.io.File;

public class TestArgumentFrame {

    public static void main(String[] args) {
        WordNet turkishWordNet = new WordNet();
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TranslatorPanel.TURKISH_PATH));
        try {
            new ArgumentFrame(turkishWordNet, treeBank);
        } catch (WordNotExistsException | LayerNotExistsException e) {
            e.printStackTrace();
        }
    }

}
