package DataCollector.Sentence.FrameNet;

import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import FrameNet.FrameNet;
import WordNet.WordNet;

import javax.swing.*;

public class SentenceFrameNetElementFrame extends SentenceAnnotatorFrame {

    private FrameNet frameNet;
    private WordNet wordNet;

    public SentenceFrameNetElementFrame(final WordNet wordNet) {
        super();
        this.wordNet = wordNet;
        frameNet = new FrameNet();
        JOptionPane.showMessageDialog(this, "WordNet and frameNet are loaded!", "Frame Element Selection", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceFrameNetElementPanel(currentPath, rawFileName, wordNet, frameNet);
    }

}
