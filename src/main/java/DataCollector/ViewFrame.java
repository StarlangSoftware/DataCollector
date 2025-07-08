package DataCollector;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewFrame extends JFrame implements ActionListener {

    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected int selectedRow = -1;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * Implements the AbstractTableModel, so that the table could be displayed accordingly.
     */
    public class TableRawDataModel extends AbstractTableModel {

        /**
         * Number of columns displayed in the table.
         * @return data.get(0).size();
         */
        public int getColumnCount() {
            return data.get(0).size();
        }

        /**
         * Number of rows displayed in the table.
         * @return Number of rows in the table.
         */
        public int getRowCount() {
            return data.size();
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        /**
         * Returns the value displayed in the cell on the row and column.
         * @param row        the row whose value is to be queried
         * @param col     the column whose value is to be queried
         * @return Value to be displayed in the cell addressed (row, column)
         */
        public Object getValueAt(int row, int col) {
            if (col < data.get(row).size()) {
                return data.get(row).get(col);
            } else {
                return "";
            }
        }

    }

}
