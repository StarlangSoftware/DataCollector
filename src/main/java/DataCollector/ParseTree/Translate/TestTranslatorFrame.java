package DataCollector.ParseTree.Translate;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import Translation.Phrase.AutomaticTranslationDictionary;
import Translation.Phrase.BilingualDictionary;

public class TestTranslatorFrame {

    public static void main(String[] args){
        TreeTranslatorFrame frame = new TreeTranslatorFrame(TreeEditorPanel.treePath, ViewLayerType.TURKISH_WORD);
        frame.loadAutomaticTranslationDictionary(new AutomaticTranslationDictionary());
        frame.loadBilingualDictionary(new BilingualDictionary());
    }
}
