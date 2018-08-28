package DataCollector.Sentence;

import AnnotatedSentence.*;
import AnnotatedSentence.AutoProcessor.AutoPredicate.TurkishSentenceAutoPredicate;
import PropBank.Argument;
import PropBank.FramesetList;

import java.awt.*;

public class SentencePropbankPredicatePanel extends AnnotatorPanel{
    private FramesetList xmlParser;
    private TurkishSentenceAutoPredicate turkishSentenceAutoPredicate;

    public SentencePropbankPredicatePanel(String currentPath, String fileName){
        super(currentPath, fileName, ViewLayerType.PROPBANK, null);
        setLayout(new BorderLayout());
        xmlParser = new FramesetList("frameset.xml");
        turkishSentenceAutoPredicate = new TurkishSentenceAutoPredicate(xmlParser);
    }

    public void autoDetect(){
        if (turkishSentenceAutoPredicate.autoPredicate(sentence)){
            sentence.save();
            this.repaint();
        }
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        listModel.addElement(new Argument("NONE", null));
        listModel.addElement(new Argument("PREDICATE", word.getSemantic()));
        if (word.getArgument() != null && word.getArgument().getArgumentType().equals("NONE")){
            return 0;
        }
        if (word.getArgument() != null && word.getArgument().getArgumentType().equals("PREDICATE")){
            return 1;
        }
        return -1;
    }

}
