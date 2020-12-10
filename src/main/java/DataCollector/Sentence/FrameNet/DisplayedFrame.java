package DataCollector.Sentence.FrameNet;

import FrameNet.Frame;

import java.util.Objects;

public class DisplayedFrame {
    private Frame frame;
    private String lexicalUnit;

    public DisplayedFrame(Frame frame, String lexicalUnit) {
        this.frame = frame;
        this.lexicalUnit = lexicalUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplayedFrame that = (DisplayedFrame) o;
        return frame.getName().equals(that.frame.getName()) &&
                lexicalUnit.equals(that.lexicalUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frame, lexicalUnit);
    }

    public Frame getFrame() {
        return frame;
    }

    public String getLexicalUnit() {
        return lexicalUnit;
    }

    @Override
    public String toString() {
        return frame.getName();
    }
}
