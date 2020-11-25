package DataCollector.Sentence.FrameNet;

import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import FrameNet.FrameNet;
import WordNet.WordNet;

public class SentenceFrameNetElementFrame extends SentenceAnnotatorFrame {

    private FrameNet frameNet;
    private WordNet wordNet;

    public SentenceFrameNetElementFrame(final WordNet wordNet) {
        super();
        this.wordNet = wordNet;
        frameNet = new FrameNet();
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceFrameNetElementPanel(currentPath, rawFileName, wordNet, frameNet);
    }

}
