package DataCollector.ParseTree;

import WordNet.WordNet;

public class TreePropbankArgumentFrame extends TreeEditorFrame {
    private WordNet wordNet;

    public TreePropbankArgumentFrame(WordNet wordNet){
        this.setTitle("Propbank Argument Editor");
        this.wordNet = wordNet;
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreePropbankArgumentPanel(currentPath, rawFileName, wordNet);
    }
}
