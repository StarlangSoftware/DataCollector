package DataCollector.ParseTree;

import WordNet.WordNet;

public class PropbankPredicateFrame extends EditorFrame{
    private WordNet wordNet;

    public PropbankPredicateFrame(WordNet wordNet){
        this.setTitle("Propbank Predicate Editor");
        this.wordNet = wordNet;
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new PropbankPredicatePanel(currentPath, rawFileName, wordNet);
    }
}
