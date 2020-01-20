package DataCollector.Sentence;

public class SentenceDependencyFrame extends AnnotatorFrame {

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceDependencyPanel(currentPath, rawFileName);
    }

    public SentenceDependencyFrame(){
        super("dependency");
    }

}
