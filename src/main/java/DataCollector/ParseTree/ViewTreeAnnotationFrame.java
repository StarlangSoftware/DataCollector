package DataCollector.ParseTree;

import AnnotatedTree.TreeBankDrawable;
import DataCollector.Sentence.ViewSentenceAnnotationFrame;
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

public abstract class ViewTreeAnnotationFrame extends JFrame implements ActionListener {

    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected TreeBankDrawable treeBank;
    protected int COLOR_COLUMN_INDEX;
    protected int TAG_INDEX;
    protected int WORD_INDEX;
    protected int WORD_POS_INDEX;
    protected int selectedRow = -1;
    protected String tagName;

    protected static final String ID_SORT = "sortid";
    protected static final String WORD_SORT = "sortword";
    protected static final String COPY = "copy";
    protected static final String PASTE = "paste";

    protected abstract void updateData(int row, String newValue);

    protected class RowComparatorAccordingToWord implements Comparator<ArrayList<String>> {

        Collator collator;

        public RowComparatorAccordingToWord(){
            Locale locale = new Locale("tr");
            collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
            if (o1.get(WORD_INDEX).equals(o2.get(WORD_INDEX))) {
                if (o1.get(TAG_INDEX).equals(o2.get(TAG_INDEX))) {
                    return collator.compare(o1.get(0), o2.get(0));
                } else {
                    return collator.compare(o1.get(TAG_INDEX), o2.get(TAG_INDEX));
                }
            } else {
                return collator.compare(o1.get(WORD_INDEX), o2.get(WORD_INDEX));
            }
        }
    }

    protected class RowComparatorAccordingToTag implements Comparator<ArrayList<String>> {

        Collator collator;

        public RowComparatorAccordingToTag(){
            Locale locale = new Locale("tr");
            collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
            if (o1.get(TAG_INDEX).equals(o2.get(TAG_INDEX))) {
                if (o1.get(WORD_INDEX).equals(o2.get(WORD_INDEX))) {
                    return collator.compare(o1.get(0), o2.get(0));
                } else {
                    return collator.compare(o1.get(WORD_INDEX), o2.get(WORD_INDEX));
                }
            } else {
                return collator.compare(o1.get(TAG_INDEX), o2.get(TAG_INDEX));
            }
        }
    }

    protected void updateGroupColors(int index){
        int groupCount = 0;
        data.get(0).set(COLOR_COLUMN_INDEX, "0");
        for (int i = 1; i < data.size(); i++){
            if (!data.get(i).get(index).equals(data.get(i - 1).get(index))){
                groupCount++;
            }
            data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case WORD_SORT:
                data.sort(new RowComparatorAccordingToWord());
                updateGroupColors(WORD_INDEX);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case ID_SORT:
                data.sort(new RowComparatorAccordingToTag());
                updateGroupColors(TAG_INDEX);
                JOptionPane.showMessageDialog(this, "Tags Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case COPY:
                if (dataTable.getSelectedRows().length == 1){
                    selectedRow = dataTable.getSelectedRow();
                } else {
                    selectedRow = -1;
                }
                break;
            case PASTE:
                if (selectedRow != -1) {
                    for (int rowNo : dataTable.getSelectedRows()) {
                        updateData(rowNo, data.get(selectedRow).get(TAG_INDEX));
                    }
                }
                dataTable.invalidate();
                break;
        }
    }

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

    public class TreeDataModel extends AbstractTableModel {

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

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "FileName";
                case 1:
                    return "Index";
                case 2:
                    return "Word";
                case 3:
                    return tagName;
                case 4:
                    return "Sentence";
                default:
                    return "";
            }
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == TAG_INDEX && !data.get(row).get(TAG_INDEX).equals(value)) {
                updateData(row, (String) value);
            }
        }

    }

    public ViewTreeAnnotationFrame(TreeBankDrawable treeBank, String tagName){
        this.treeBank = treeBank;
        this.tagName = tagName;
        COLOR_COLUMN_INDEX = 6;
        WORD_POS_INDEX = 1;
        WORD_INDEX = 2;
        TAG_INDEX = 3;
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
        dataTable = new JTable(new TreeDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(60);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(2).setWidth(200);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setWidth(200);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
    }

}
