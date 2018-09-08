package DataCollector.ParseTree;

import WordNet.*;

public class EnglishSemanticFrame extends EditorFrame{
    private WordNet englishWordNet, turkishWordNet;

    public EnglishSemanticFrame(final WordNet englishWordNet, final WordNet turkishWordNet){
        this.setTitle("English Semantic Editor");
        this.englishWordNet = englishWordNet;
        this.turkishWordNet = turkishWordNet;
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new EnglishSemanticPanel(currentPath, rawFileName, englishWordNet, turkishWordNet);
    }
}
