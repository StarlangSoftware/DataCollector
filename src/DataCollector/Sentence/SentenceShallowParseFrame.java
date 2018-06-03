package DataCollector.Sentence;

public class SentenceShallowParseFrame extends AnnotatorFrame{

    public SentenceShallowParseFrame(){
        super("shallowparse");
    }

    @Override
    protected AnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceShallowParsePanel(currentPath, rawFileName);
    }
}
