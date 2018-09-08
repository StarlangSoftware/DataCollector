package DataCollector.ParseTree;

import WordNet.WordNet;

public class PropbankFrame extends EditorFrame{
    private WordNet wordNet;

    public PropbankFrame(WordNet wordNet){
        this.setTitle("Propbank Predicate/Argument Editor");
        this.wordNet = wordNet;
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new PropbankPanel(currentPath, rawFileName, wordNet);
    }
}
