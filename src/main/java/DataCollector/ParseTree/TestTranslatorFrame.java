package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import Translation.AutomaticTranslationDictionary;
import Translation.BilingualDictionary;

public class TestTranslatorFrame {

    public static void main(String[] args){
        TreeTranslatorFrame frame = new TreeTranslatorFrame(TreeEditorPanel.treePath, ViewLayerType.TURKISH_WORD);
        frame.loadAutomaticTranslationDictionary(new AutomaticTranslationDictionary());
        frame.loadBilingualDictionary(new BilingualDictionary());
    }
}
