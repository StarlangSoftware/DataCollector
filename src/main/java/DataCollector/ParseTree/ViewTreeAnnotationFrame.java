package DataCollector.ParseTree;

import AnnotatedTree.TreeBankDrawable;
import DataCollector.RowComparator2;
import DataCollector.Sentence.ViewSentenceAnnotationFrame;
import DataCollector.ViewAnnotationFrame;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ViewTreeAnnotationFrame extends ViewAnnotationFrame implements ActionListener {

    protected TreeBankDrawable treeBank;
    protected String tagName;

    protected abstract void updateData(int row, String newValue);

    /**
     * Does specific functions for different buttons. If the command is
     *
     * <p>ID_SORT: The data are sorted based on annotation layer first, then the word, then the file name</p>
     * <p>WORD_SORT: The data are sorted based on word first, then the annotation layer, then the file name</p>
     * <p>COPY: A row is selected for pasting its annotation layer value.</p>
     * @param e Action event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()) {
            case WORD_SORT:
                data.sort(new RowComparator2(WORD_INDEX, TAG_INDEX));
                updateGroupColors(WORD_INDEX);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case ID_SORT:
                data.sort(new RowComparator2(TAG_INDEX, WORD_INDEX));
                updateGroupColors(TAG_INDEX);
                JOptionPane.showMessageDialog(this, "Tags Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
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

    public class TreeDataModel extends TableDataModel {

        /**
         * Sets the column name of the table
         * @param col  the column being queried
         * @return The column name
         */
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

        /**
         * Sets the value for the annotation layer depending on the layer using abstract updateData method.
         * @param value   value to assign to cell
         * @param row   row of cell
         * @param col  column of cell
         */
        public void setValueAt(Object value, int row, int col) {
            if (col == TAG_INDEX && !data.get(row).get(TAG_INDEX).equals(value)) {
                updateData(row, (String) value);
            }
        }

    }

    /**
     * Constructor for the base tree annotation frame. This frame displays annotated data in the annotated treebank in
     * a table. Depending on the annotation type, derived classes will appropriate columns to the table. Base class
     * only adds two sort buttons, one copy and one paste buttons to the toolbar.
     * @param treeBank Annotated treeBank.
     * @param tagName Tag name.
     */
    public ViewTreeAnnotationFrame(TreeBankDrawable treeBank, String tagName){
        this.treeBank = treeBank;
        this.tagName = tagName;
        COLOR_COLUMN_INDEX = 6;
        WORD_POS_INDEX = 1;
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
