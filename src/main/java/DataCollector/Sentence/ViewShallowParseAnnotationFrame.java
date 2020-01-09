package DataCollector.Sentence;

import AnnotatedSentence.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewShallowParseAnnotationFrame extends ViewAnnotationFrame implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        int groupCount;
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort((o1, o2) -> {
                    if (o1.get(2).equals(o2.get(2))){
                        String[] words1 = o1.get(1).split(" ");
                        String[] words2 = o2.get(1).split(" ");
                        if (words1[words1.length - 1].equals(words2[words2.length - 1])){
                            return o1.get(0).compareTo(o2.get(0));
                        } else {
                            return words1[words1.length - 1].compareTo(words2[words2.length - 1]);
                        }
                    } else {
                        return o1.get(2).compareTo(o2.get(2));
                    }
                });
                groupCount = 0;
                data.get(0).set(COLOR_COLUMN_INDEX, "0");
                for (int i = 1; i < data.size(); i++){
                    if (!data.get(i).get(2).equals(data.get(i - 1).get(2))){
                        groupCount++;
                    }
                    data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
                }
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case WORD_SORT:
                data.sort((o1, o2) -> {
                    String[] words1 = o1.get(1).split(" ");
                    String[] words2 = o2.get(1).split(" ");
                    if (words1[words1.length - 1].equals(words2[words2.length - 1])){
                        if (o1.get(2).equals(o2.get(2))){
                            return o1.get(0).compareTo(o2.get(0));
                        } else {
                            return o1.get(2).compareTo(o2.get(2));
                        }
                    } else {
                        return words1[words1.length - 1].compareTo(words2[words2.length - 1]);
                    }
                });
                groupCount = 0;
                data.get(0).set(COLOR_COLUMN_INDEX, "0");
                for (int i = 1; i < data.size(); i++){
                    String[] words1 = data.get(i).get(1).split(" ");
                    String[] words2 = data.get(i - 1).get(1).split(" ");
                    if (!words1[words1.length - 1].equals(words2[words2.length - 1])){
                        groupCount++;
                    }
                    data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
                }
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case COPY:
                if (dataTable.getSelectedRows().length == 1){
                    selectedRow = dataTable.getSelectedRow();
                } else {
                    selectedRow = -1;
                }
                break;
            case PASTE:
                if (selectedRow != -1){
                    for (int rowNo : dataTable.getSelectedRows()){
                        updateShallowParse(rowNo, data.get(selectedRow).get(2));
                    }
                }
                break;
        }
        dataTable.invalidate();
    }

    public class TableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "FileName";
                case 1:
                    return "Word Group";
                case 2:
                    return "Shallow Parse";
                case 3:
                    return "Sentence";
                default:
                    return "";
            }
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        public boolean isCellEditable(int row, int col) {
            return col == 2;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 2 && !data.get(row).get(2).equals(value)) {
                updateShallowParse(row, (String) value);
            }
        }
    }

    private void updateShallowParse(int row, String newValue){
        data.get(row).set(2, newValue);
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(4)));
        String[] words = data.get(row).get(1).split(" ");
        int i = 0;
        while (!sentence.getWord(i).getName().equals(words[0])){
            i++;
        }
        for (int j = 0; j < words.length; j++){
            ((AnnotatedWord)sentence.getWord(i + j)).setShallowParse(newValue);
        }
        sentence.save();
    }

    private void addRow(int i, AnnotatedSentence sentence, String previousGroup, String previousParse){
        if (previousGroup != null){
            ArrayList<String> row = new ArrayList<String>();
            row.add(sentence.getFileName());
            row.add(previousGroup);
            if (previousParse != null){
                row.add(previousParse);
            } else {
                row.add("-");
            }
            row.add(sentence.toWords());
            row.add("" + i);
            row.add("0");
            data.add(row);
        }
    }

    protected void prepareData(AnnotatedCorpus corpus){
        data = new ArrayList<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            int j = 0;
            String previousGroup = null;
            String previousParse = null;
            while (j < corpus.getSentence(i).wordCount()){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                if (word.getShallowParse() != null){
                    if (previousParse == null || !word.getShallowParse().equals(previousParse)){
                        if (previousParse != null){
                            addRow(i, sentence, previousGroup, previousParse);
                        }
                        previousGroup = word.getName();
                        previousParse = word.getShallowParse();
                    } else {
                        previousGroup = previousGroup + " " + word.getName();
                    }
                }
                j++;
            }
            addRow(i, sentence, previousGroup, previousParse);
        }
    }

    public ViewShallowParseAnnotationFrame(AnnotatedCorpus corpus){
        super(corpus);
        COLOR_COLUMN_INDEX = 5;
        prepareData(corpus);
        dataTable = new JTable(new ViewShallowParseAnnotationFrame.TableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(200);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(200);
        dataTable.getColumnModel().getColumn(2).setMinWidth(150);
        dataTable.getColumnModel().getColumn(2).setMaxWidth(150);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
