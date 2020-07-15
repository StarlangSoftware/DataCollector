package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import Util.DrawingButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public abstract class ViewSentenceAnnotationFrame extends JFrame implements ActionListener {
    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected AnnotatedCorpus corpus;
    protected int selectedRow = -1;

    protected static final String ID_SORT = "sortid";
    protected static final String WORD_SORT = "sortword";
    protected static final String COPY = "copy";
    protected static final String PASTE = "paste";
    protected static final int FILENAME_INDEX = 0;
    protected static final int WORD_POS_INDEX = 1;
    protected static final int WORD_INDEX = 2;
    protected int TAG_INDEX;
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

    protected void updateGroupColors(int groupIndex){
        int groupCount = 0;
        data.get(0).set(COLOR_COLUMN_INDEX, "0");
        for (int i = 1; i < data.size(); i++){
            if (!data.get(i).get(groupIndex).equals(data.get(i - 1).get(groupIndex))){
                groupCount++;
            }
            data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
        }
    }

    protected class RowComparator implements Comparator<ArrayList<String>> {

        int firstIndex, secondIndex, thirdIndex;
        Collator collator;

        public RowComparator(int firstIndex, int secondIndex, int thirdIndex){
            this.firstIndex = firstIndex;
            this.secondIndex = secondIndex;
            this.thirdIndex = thirdIndex;
            Locale locale = new Locale("tr");
            collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
            if (o1.get(firstIndex).equals(o2.get(firstIndex))) {
                if (o1.get(secondIndex).equals(o2.get(secondIndex))) {
                    return collator.compare(o1.get(thirdIndex), o2.get(thirdIndex));
                } else {
                    return collator.compare(o1.get(secondIndex), o2.get(secondIndex));
                }
            } else {
                return collator.compare(o1.get(firstIndex), o2.get(firstIndex));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort(new RowComparator(TAG_INDEX, WORD_INDEX, FILENAME_INDEX));
                updateGroupColors(TAG_INDEX);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case WORD_SORT:
                data.sort(new RowComparator(WORD_INDEX, TAG_INDEX, FILENAME_INDEX));
                updateGroupColors(WORD_INDEX);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case COPY:
                if (dataTable.getSelectedRows().length == 1){
                    selectedRow = dataTable.getSelectedRow();
                } else {
                    selectedRow = -1;
                }
                break;
        }
    }

    public class TableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return COLOR_COLUMN_INDEX - 1;
        }

        public int getRowCount() {
            return data.size();
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        public boolean isCellEditable(int row, int col) {
            return col == TAG_INDEX;
        }

    }

    public ViewSentenceAnnotationFrame(AnnotatedCorpus corpus){
        this.corpus = corpus;
        JToolBar toolBar = new JToolBar("ToolBox");
        JButton idSort = new DrawingButton(ViewSentenceAnnotationFrame.class, this, "sortnumbers", ID_SORT, "");
        toolBar.add(idSort);
        JButton textSort = new DrawingButton(ViewSentenceAnnotationFrame.class, this, "sorttext", WORD_SORT, "");
        toolBar.add(textSort);
        JButton copy = new DrawingButton(ViewSentenceAnnotationFrame.class, this, "copy", COPY, "Copy Id");
        toolBar.add(copy);
        JButton paste = new DrawingButton(ViewSentenceAnnotationFrame.class, this, "paste", PASTE, "Paste Id");
        toolBar.add(paste);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
