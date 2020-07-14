package DataCollector.ParseTree;

import WordNet.WordNet;

public class TreePropbankPredicateFrame extends TreeEditorFrame {
    private WordNet wordNet;

    public TreePropbankPredicateFrame(WordNet wordNet){
        this.setTitle("Propbank Predicate Editor");
        this.wordNet = wordNet;
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreePropbankPredicatePanel(currentPath, rawFileName, wordNet);
    }
}
