package DataCollector.Sentence;

import AnnotatedSentence.ViewLayerType;

public class SentenceDependencyPanel extends AnnotatorPanel {

    public SentenceDependencyPanel(String currentPath, String rawFileName) {
        super(currentPath, rawFileName, ViewLayerType.DEPENDENCY);
    }

}
