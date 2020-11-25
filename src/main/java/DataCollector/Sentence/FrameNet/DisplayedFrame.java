package DataCollector.Sentence.FrameNet;

import FrameNet.Frame;
import FrameNet.LexicalUnit;

import java.util.Objects;

public class DisplayedFrame {
    private Frame frame;
    private LexicalUnit lexicalUnit;

    public DisplayedFrame(Frame frame, LexicalUnit lexicalUnit) {
        this.frame = frame;
        this.lexicalUnit = lexicalUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplayedFrame that = (DisplayedFrame) o;
        return frame.getName().equals(that.frame.getName()) &&
                lexicalUnit.getSynSetId().equals(that.lexicalUnit.getSynSetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(frame, lexicalUnit);
    }

    public Frame getFrame() {
        return frame;
    }

    public LexicalUnit getLexicalUnit() {
        return lexicalUnit;
    }

    @Override
    public String toString() {
        return frame.getName();
    }
}
