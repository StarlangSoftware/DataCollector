package DataCollector.Sentence;

import MorphologicalAnalysis.FsmParse;

import javax.swing.*;
import java.awt.*;

public class FsmParseListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof FsmParse){
            FsmParse currentParse = (FsmParse) value;
            ((JComponent) cell).setToolTipText(currentParse.withList());
        }
        return this;
    }
}
