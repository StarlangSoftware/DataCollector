package DataCollector;

import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import Util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ViewComparisonFrame extends ViewFrame implements ActionListener {

    protected ArrayList<String> columns;

    public class ComparisonTableDataModel extends TableRawDataModel {

        /**
         * Returns the name of the given column.
         *
         * @param col the column being queried
         * @return Name of the given column
         */
        public String getColumnName(int col) {
            return columns.get(col);
        }

    }

    protected void prepareData(String fileName) {
        data = new ArrayList<>();
        columns = new ArrayList<>();
        String line;
        boolean firstLine = true;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.getInputStream(fileName), StandardCharsets.UTF_8));
            line = br.readLine();
            while (line != null) {
                String[] items = line.split("\\t");
                if (firstLine) {
                    columns.addAll(Arrays.asList(items));
                    firstLine = false;
                } else {
                    ArrayList<String> dataLine = new ArrayList<>(Arrays.asList(items));
                    data.add(dataLine);
                }
                line = br.readLine();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Constructor for the view comparison frame. This frame displays comparison data in the text file selected in
     * a table.
     */
    public ViewComparisonFrame(String fileName, SentenceAnnotatorFrame sentenceAnnotatorFrame) {
        JToolBar toolBar = new JToolBar("ToolBox");
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        prepareData(fileName);
        dataTable = new JTable(new ComparisonTableDataModel());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        sentenceAnnotatorFrame.addPanelToFrame(sentenceAnnotatorFrame.generatePanel(TreeEditorPanel.phrasePath, fileName), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
