package DataCollector.ParseTree;

import WordNet.WordNet;

public class PropbankArgumentFrame extends EditorFrame{
    private WordNet wordNet;

    public PropbankArgumentFrame(WordNet wordNet){
        this.setTitle("Propbank Argument Editor");
        this.wordNet = wordNet;
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new PropbankArgumentPanel(currentPath, rawFileName, wordNet);
    }
}
