package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

public class ViewDependencyAnnotationFrame extends ViewAnnotationFrame implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        int groupCount;
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort((o1, o2) -> {
                    if (o1.get(4).equals(o2.get(4))){
                        if (o1.get(2).equals(o2.get(2))){
                            return collator.compare(o1.get(0), o2.get(0));
                        } else {
                            return collator.compare(o1.get(2), o2.get(2));
                        }
                    } else {
                        return collator.compare(o1.get(4), o2.get(4));
                    }
                });
                groupCount = 0;
                data.get(0).set(COLOR_COLUMN_INDEX, "0");
                for (int i = 1; i < data.size(); i++){
                    if (!data.get(i).get(4).equals(data.get(i - 1).get(4))){
                        groupCount++;
                    }
                    data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
                }
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case WORD_SORT:
                data.sort((o1, o2) -> {
                    if (o1.get(2).equals(o2.get(2))){
                        if (o1.get(4).equals(o2.get(4))){
                            return collator.compare(o1.get(0), o2.get(0));
                        } else {
                            return collator.compare(o1.get(4), o2.get(4));
                        }
                    } else {
                        return collator.compare(o1.get(2), o2.get(2));
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
                        updateDependency(rowNo, Integer.parseInt(data.get(selectedRow).get(3)), data.get(selectedRow).get(4));
                    }
                }
                break;
        }
        dataTable.invalidate();
    }

    public class TableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return COLOR_COLUMN_INDEX - 1;
        }

        public int getRowCount() {
            return data.size();
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
                    return "Dependent Word Index";
                case 4:
                    return "Dependency Type";
                case 5:
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
            return col == 3 || col == 4;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 3 && !data.get(row).get(3).equals(value)) {
                updateDependency(row, Integer.parseInt((String)value), data.get(row).get(4));
            }
            if (col == 4 && !data.get(row).get(4).equals(value)) {
                updateDependency(row, Integer.parseInt(data.get(row).get(3)), (String) value);
            }
        }
    }

    private void updateDependency(int row, int to, String newDependency){
        data.get(row).set(4, newDependency);
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX - 1)));
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(Integer.parseInt(data.get(row).get(1)) - 1);
        word.setUniversalDependency(to, newDependency);
        sentence.save();
    }

    protected void prepareData(AnnotatedCorpus corpus){
        data = new ArrayList<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                ArrayList<String> row = new ArrayList<>();
                row.add(sentence.getFileName());
                row.add("" + (j + 1));
                row.add(word.getName());
                if (word.getUniversalDependency() != null){
                    row.add("" + word.getUniversalDependency().to());
                    row.add(word.getUniversalDependency().toString());
                } else {
                    row.add("-");
                    row.add("-");
                }
                row.add(sentence.toWords());
                row.add("" + i);
                row.add("0");
                data.add(row);
            }
        }
    }
    public ViewDependencyAnnotationFrame(AnnotatedCorpus corpus){
        super(corpus);
        COLOR_COLUMN_INDEX = 7;
        prepareData(corpus);
        dataTable = new JTable(new ViewDependencyAnnotationFrame.TableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(60);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(2).setMinWidth(200);
        dataTable.getColumnModel().getColumn(2).setMaxWidth(200);
        dataTable.getColumnModel().getColumn(3).setMinWidth(60);
        dataTable.getColumnModel().getColumn(3).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(4).setMinWidth(100);
        dataTable.getColumnModel().getColumn(4).setMaxWidth(100);
        dataTable.getColumnModel().getColumn(5).setWidth(300);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
