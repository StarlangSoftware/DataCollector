package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import Util.DrawingButton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class ViewAnnotationFrame extends JFrame implements ActionListener {
    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected AnnotatedCorpus corpus;
    protected int selectedRow = -1;

    protected static final String ID_SORT = "sortid";
    protected static final String WORD_SORT = "sortword";
    protected static final String COPY = "copy";
    protected static final String PASTE = "paste";
    protected int COLOR_COLUMN_INDEX;

    public class CellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int groupCount = Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX));
            if (groupCount % 2 == 0){
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(Color.LIGHT_GRAY);
            }
            if (isSelected){
                c.setBackground(Color.BLUE);
            }
            return c;
        }
    }

    public ViewAnnotationFrame(AnnotatedCorpus corpus){
        this.corpus = corpus;
        JToolBar toolBar = new JToolBar("ToolBox");
        JButton idSort = new DrawingButton(ViewAnnotationFrame.class, this, "sortnumbers", ID_SORT, "");
        toolBar.add(idSort);
        JButton textSort = new DrawingButton(ViewAnnotationFrame.class, this, "sorttext", WORD_SORT, "");
        toolBar.add(textSort);
        JButton copy = new DrawingButton(ViewAnnotationFrame.class, this, "copy", COPY, "Copy Id");
        toolBar.add(copy);
        JButton paste = new DrawingButton(ViewAnnotationFrame.class, this, "paste", PASTE, "Paste Id");
        toolBar.add(paste);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
