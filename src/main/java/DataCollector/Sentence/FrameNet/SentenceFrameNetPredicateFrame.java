package DataCollector.Sentence.FrameNet;

import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import FrameNet.FrameNet;
import WordNet.WordNet;

public class SentenceFrameNetPredicateFrame extends SentenceAnnotatorFrame {

    private FrameNet frameNet;
    private WordNet wordNet;

    public SentenceFrameNetPredicateFrame() {
        super();
        wordNet = new WordNet();
        frameNet = new FrameNet();
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceFrameNetPredicatePanel(currentPath, rawFileName, frameNet, wordNet);
    }

}
