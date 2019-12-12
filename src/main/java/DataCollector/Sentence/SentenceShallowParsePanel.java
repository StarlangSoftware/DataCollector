package DataCollector.Sentence;

import AnnotatedSentence.*;
import DataGenerator.InstanceGenerator.FeaturedShallowParseInstanceGenerator;

import java.awt.*;

public class SentenceShallowParsePanel extends AnnotatorPanel{
    private String[] shallowParseList = {"YÜKLEM", "ÖZNE", "NESNE", "ZARF_TÜMLECİ", "DOLAYLI_TÜMLEÇ", "HİÇBİRİ"};

    public SentenceShallowParsePanel(String currentPath, String fileName){
        super(currentPath, fileName, ViewLayerType.SHALLOW_PARSE);
        setLayout(new BorderLayout());
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        for (int i = 0; i < shallowParseList.length; i++){
            if (word.getShallowParse() != null && word.getShallowParse().equals(shallowParseList[i])){
                selectedIndex = i;
            }
            listModel.addElement(shallowParseList[i]);
        }
        return selectedIndex;
    }

}
