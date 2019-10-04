package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import Translation.AutomaticTranslationDictionary;
import Translation.BilingualDictionary;
import Dictionary.EnglishWordComparator;

public class TestTranslatorFrame {

    public static void main(String[] args){
        TranslatorFrame frame = new TranslatorFrame(EditorPanel.TURKISH_PATH, ViewLayerType.TURKISH_WORD);
        frame.loadAutomaticTranslationDictionary(new AutomaticTranslationDictionary());
        frame.loadBilingualDictionary(new BilingualDictionary());
    }
}
