package DataCollector.Sentence;

import AnnotatedSentence.*;
import PropBank.Argument;
import PropBank.FramesetList;

import java.awt.*;
import java.util.ArrayList;

public class SentencePropbankPredicatePanel extends AnnotatorPanel{
    private FramesetList xmlParser;

    public SentencePropbankPredicatePanel(String currentPath, String fileName){
        super(currentPath, fileName, ViewLayerType.PROPBANK, null);
        setLayout(new BorderLayout());
        xmlParser = new FramesetList("frameset.xml");
    }

    public void autoDetect(){
        ArrayList<AnnotatedWord> candidateList = sentence.predicateCandidates(xmlParser);
        for (AnnotatedWord word : candidateList){
            word.setArgument("PREDICATE$" + word.getSemantic());
        }
        if (candidateList.size() > 0){
            sentence.save();
        }
        this.repaint();
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
