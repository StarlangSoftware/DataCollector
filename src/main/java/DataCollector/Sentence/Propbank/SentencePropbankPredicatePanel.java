package DataCollector.Sentence.Propbank;

import AnnotatedSentence.*;
import AnnotatedSentence.AutoProcessor.AutoPredicate.TurkishSentenceAutoPredicate;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import Dictionary.Pos;
import PropBank.Argument;
import PropBank.FramesetList;
import WordNet.WordNet;

import java.awt.*;

public class SentencePropbankPredicatePanel extends SentenceAnnotatorPanel {
    private TurkishSentenceAutoPredicate turkishSentenceAutoPredicate;
    private WordNet wordNet;

    public SentencePropbankPredicatePanel(String currentPath, String fileName, FramesetList xmlParser, WordNet wordNet){
        super(currentPath, fileName, ViewLayerType.PROPBANK);
        setLayout(new BorderLayout());
        this.wordNet = wordNet;
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
        if (word.getSemantic() != null && wordNet.getSynSetWithId(word.getSemantic()) != null && wordNet.getSynSetWithId(word.getSemantic()).getPos().equals(Pos.VERB)){
            listModel.addElement(new Argument("PREDICATE", word.getSemantic()));
        }
        if (word.getArgument() != null && word.getArgument().getArgumentType().equals("NONE")){
            return 0;
        }
        if (word.getArgument() != null && word.getArgument().getArgumentType().equals("PREDICATE") && word.getArgument().getId().equals(word.getSemantic())){
            return 1;
        }
        return -1;
    }

}
