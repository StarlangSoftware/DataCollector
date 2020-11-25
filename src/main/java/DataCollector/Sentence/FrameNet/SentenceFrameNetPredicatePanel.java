package DataCollector.Sentence.FrameNet;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.ViewLayerType;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import Dictionary.Pos;
import FrameNet.FrameNet;
import FrameNet.FrameElement;
import WordNet.WordNet;

import java.awt.*;

public class SentenceFrameNetPredicatePanel extends SentenceAnnotatorPanel {

    private WordNet wordNet;
    private FrameNet frameNet;

    public SentenceFrameNetPredicatePanel(String currentPath, String fileName, FrameNet frameNet, WordNet wordNet){
        super(currentPath, fileName, ViewLayerType.FRAMENET);
        setLayout(new BorderLayout());
        this.wordNet = wordNet;
        this.frameNet = frameNet;
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        listModel.addElement(new FrameElement("NONE", null));
        if (word.getSemantic() != null && wordNet.getSynSetWithId(word.getSemantic()) != null && wordNet.getSynSetWithId(word.getSemantic()).getPos().equals(Pos.VERB)){
            listModel.addElement(new FrameElement("PREDICATE", word.getSemantic()));
        }
        if (word.getFrameElement() != null && word.getFrameElement().getFrameElementType().equals("NONE")){
            return 0;
        }
        if (word.getFrameElement() != null && word.getFrameElement().getFrameElementType().equals("PREDICATE") && word.getFrameElement().getId().equals(word.getSemantic())){
            return 1;
        }
        return -1;
    }

}
