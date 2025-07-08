package DataCollector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ViewAnnotationFrame extends ViewFrame implements ActionListener {

    protected static final String ID_SORT = "sortid";
    protected static final String WORD_SORT = "sortword";
    protected static final String COPY = "copy";
    protected static final String PASTE = "paste";
    protected int TAG_INDEX;
    protected static final int WORD_INDEX = 2;
    protected int COLOR_COLUMN_INDEX;
    protected int WORD_POS_INDEX;

    /**
     * Implements the AbstractTableModel, so that the table could be displayed accordingly.
     */
    public class TableDataModel extends TableRawDataModel {

        /**
         * Number of columns displayed in the table.
         * @return COLOR_COLUMN_INDEX - 1
         */
        public int getColumnCount() {
            return COLOR_COLUMN_INDEX - 1;
        }

        /**
         * Returns if the cell is editable or not. If the cell shows the annotation layer value, it is editable,
         * otherwise not.
         * @param row  the row being queried
         * @param col the column being queried
         * @return If the cell shows the annotation layer value, returns true, otherwise false.
         */
        public boolean isCellEditable(int row, int col) {
            return col == TAG_INDEX;
        }

    }

    /**
     * Draws a single cell in the table. Alternating groups are displayed with white and light gray background colors.
     * If the cell is selected, the background color is blue. The color index is taken from the column with the
     * index of COLOR_COLUMN_INDEX.
     */
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

    /**
     * Updates the color index of the groups based on the values in the column groupIndex. The system checks successive
     * values, if they are different, the group color is changed. Otherwise, it stays the same. The system also considers
     * the null values, that is if one of the values of the successive elements is null but not the other, the group
     * color is also changed.
     * @param groupIndex The column index for which colors are alternated.
     */
    protected void updateGroupColors(int groupIndex){
        int groupCount = 0;
        data.get(0).set(COLOR_COLUMN_INDEX, "0");
        for (int i = 1; i < data.size(); i++){
            if ((data.get(i - 1).get(groupIndex) == null && data.get(i).get(groupIndex) != null) ||
                    (data.get(i - 1).get(groupIndex) != null && data.get(i).get(groupIndex) == null)){
                groupCount++;
            } else {
                if (data.get(i - 1).get(groupIndex) != null && data.get(i).get(groupIndex) != null &&
                        !data.get(i).get(groupIndex).equals(data.get(i - 1).get(groupIndex))){
                    groupCount++;
                }
            }
            data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case COPY:
                if (dataTable.getSelectedRows().length == 1){
                    selectedRow = dataTable.getSelectedRow();
                } else {
                    selectedRow = -1;
                }
                break;
        }
    }
}
