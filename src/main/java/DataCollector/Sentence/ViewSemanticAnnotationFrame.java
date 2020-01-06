package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewSemanticAnnotationFrame extends JFrame implements ActionListener {
    private ArrayList<ArrayList<String>> data;
    private JTable dataTable;
    private AnnotatedCorpus corpus;
    private WordNet domainWordNet, turkish;
    private JToolBar toolBar;
    private int selectedRow = -1;

    private static final String ID_SORT = "sortid";
    private static final String WORD_SORT = "sortword";
    private static final String COPY = "copy";
    private static final String PASTE = "paste";

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort((o1, o2) -> {
                    if (o1.get(3).equals(o2.get(3))){
                        if (o1.get(2).equals(o2.get(2))){
                            return o1.get(0).compareTo(o2.get(0));
                        } else {
                            return o1.get(2).compareTo(o2.get(2));
                        }
                    } else {
                        return o1.get(3).compareTo(o2.get(3));
                    }
                });
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case WORD_SORT:
                data.sort((o1, o2) -> {
                    if (o1.get(2).equals(o2.get(2))){
                        if (o1.get(3).equals(o2.get(3))){
                            return o1.get(0).compareTo(o2.get(0));
                        } else {
                            return o1.get(3).compareTo(o2.get(3));
                        }
                    } else {
                        return o1.get(2).compareTo(o2.get(2));
                    }
                });
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
                        updateSemantic(rowNo, data.get(selectedRow).get(3));
                    }
                }
                break;
        }
        dataTable.invalidate();
    }

    public class TableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return 7;
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
                    return "Sense Id";
                case 4:
                    return "SynSet";
                case 5:
                    return "Sense Definition";
                case 6:
                    return "Sentence";
                default:
                    return "";
            }
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 5:
                case 6:
                    return data.get(row).get(col);
                case 3:
                    return data.get(row).get(col);
                default:
                    return "";
            }
        }

        public boolean isCellEditable(int row, int col) {
            return col == 3;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 3 && !data.get(row).get(3).equals(value)) {
                updateSemantic(row, (String) value);
            }
        }
    }

    public void updateSemantic(int row, String newValue){
        data.get(row).set(3, newValue);
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(7)));
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(Integer.parseInt(data.get(row).get(1)) - 1);
        word.setSemantic(newValue);
        sentence.save();
        SynSet synSet = domainWordNet.getSynSetWithId(word.getSemantic());
        if (synSet == null){
            synSet = turkish.getSynSetWithId(word.getSemantic());
        }
        if (synSet != null){
            data.get(row).set(4, synSet.getSynonym().toString());
            data.get(row).set(5, synSet.getLongDefinition());
        }
    }

    private void prepareData(AnnotatedCorpus corpus){
        data = new ArrayList<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                ArrayList<String> row = new ArrayList<String>();
                row.add(sentence.getFileName());
                row.add("" + (j + 1));
                row.add(word.getName());
                if (word.getSemantic() != null){
                    row.add(word.getSemantic());
                    SynSet synSet = domainWordNet.getSynSetWithId(word.getSemantic());
                    if (synSet == null){
                        synSet = turkish.getSynSetWithId(word.getSemantic());
                    }
                    if (synSet != null){
                        row.add(synSet.getSynonym().toString());
                        row.add(synSet.getLongDefinition());
                    } else {
                        row.add("-");
                        row.add("-");
                    }
                } else {
                    row.add("-");
                    row.add("-");
                    row.add("-");
                }
                row.add(sentence.toWords());
                row.add("" + i);
                data.add(row);
            }
        }
    }

    public ViewSemanticAnnotationFrame(AnnotatedCorpus corpus, WordNet domainWordNet, WordNet turkish){
        this.domainWordNet = domainWordNet;
        this.turkish = turkish;
        this.corpus = corpus;
        prepareData(corpus);
        toolBar = new JToolBar("ToolBox");
        JButton idSort = new DrawingButton(ViewSemanticAnnotationFrame.class, this, "sortnumbers", ID_SORT, "Sort by WordNet Id");
        toolBar.add(idSort);
        JButton textSort = new DrawingButton(ViewSemanticAnnotationFrame.class, this, "sorttext", WORD_SORT, "Sort by Word");
        toolBar.add(textSort);
        JButton copy = new DrawingButton(ViewSemanticAnnotationFrame.class, this, "copy", COPY, "Copy Id");
        toolBar.add(copy);
        JButton paste = new DrawingButton(ViewSemanticAnnotationFrame.class, this, "paste", PASTE, "Paste Id");
        toolBar.add(paste);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        dataTable = new JTable(new TableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(60);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(2).setMinWidth(200);
        dataTable.getColumnModel().getColumn(2).setMaxWidth(200);
        dataTable.getColumnModel().getColumn(3).setMinWidth(150);
        dataTable.getColumnModel().getColumn(3).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(4).setMinWidth(200);
        dataTable.getColumnModel().getColumn(4).setMaxWidth(200);
        dataTable.getColumnModel().getColumn(5).setMinWidth(300);
        dataTable.getColumnModel().getColumn(5).setMaxWidth(300);
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
