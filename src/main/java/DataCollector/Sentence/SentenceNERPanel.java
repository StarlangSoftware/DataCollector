package DataCollector.Sentence;

import AnnotatedSentence.*;
import AnnotatedSentence.AutoProcessor.AutoNER.TurkishSentenceAutoNER;
import NamedEntityRecognition.NamedEntityType;

import java.awt.*;

public class SentenceNERPanel extends SentenceAnnotatorPanel {
    private TurkishSentenceAutoNER turkishSentenceAutoNER;

    public SentenceNERPanel(String currentPath, String fileName){
        super(currentPath, fileName, ViewLayerType.NER);
        turkishSentenceAutoNER = new TurkishSentenceAutoNER();
        setLayout(new BorderLayout());
    }

    public void autoDetect(){
        turkishSentenceAutoNER.autoNER(sentence);
        sentence.save();
        this.repaint();
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        for (int i = 0; i < NamedEntityType.values().length; i++){
            if (word.getNamedEntityType() != null && word.getNamedEntityType().equals(NamedEntityType.values()[i])){
                selectedIndex = i;
            }
            listModel.addElement(NamedEntityType.values()[i].toString());
        }
        return selectedIndex;
    }

}
