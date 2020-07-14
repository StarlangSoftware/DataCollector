package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.ParseTree.EditorPanel;
import DataCollector.ParseTree.SyntacticPanel;
import Dictionary.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewDependencyAnnotationFrame extends ViewAnnotationFrame implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (PASTE.equals(e.getActionCommand())) {
            if (selectedRow != -1) {
                for (int rowNo : dataTable.getSelectedRows()) {
                    if (!updateDependencyTag(rowNo, data.get(selectedRow).get(TAG_INDEX))){
                        JOptionPane.showMessageDialog(this, "No Dependency Defined Before", "Warning", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
        dataTable.invalidate();
    }

    public class DependencyTableDataModel extends TableDataModel {

        public String getColumnName(int col) {
            switch (col) {
                case FILENAME_INDEX:
                    return "FileName";
                case WORD_POS_INDEX:
                    return "Index";
                case WORD_INDEX:
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

        public boolean isCellEditable(int row, int col) {
            return col == 3 || col == TAG_INDEX;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 3 && !data.get(row).get(3).equals(value)) {
                updateDependencyTo(row, Integer.parseInt((String)value));
                dataTable.invalidate();
            }
            if (col == TAG_INDEX && !data.get(row).get(TAG_INDEX).equals(value)) {
                updateDependencyTag(row, (String) value);
            }
        }
    }

    private void updateDependencyTo(int row, int to){
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX - 1)));
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(Integer.parseInt(data.get(row).get(WORD_POS_INDEX)) - 1);
        if (word.getUniversalDependency() != null){
            data.get(row).set(3, to + "");
            word.setUniversalDependency(to, word.getUniversalDependency().toString());
            sentence.save();
        }
    }

    private boolean updateDependencyTag(int row, String newDependency){
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX - 1)));
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(Integer.parseInt(data.get(row).get(WORD_POS_INDEX)) - 1);
        if (word.getUniversalDependency() != null){
            data.get(row).set(TAG_INDEX, newDependency);
            word.setUniversalDependency(word.getUniversalDependency().to(), newDependency);
            sentence.save();
            return true;
        }
        return false;
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
                    String sentenceString = "<html>";
                    ArrayList<Word> wordList = sentence.getWords();
                    for (int k = 0; k < wordList.size(); k++){
                        if (j == k){
                            sentenceString += " <b><font color=\"red\">" + wordList.get(k).getName() + "</font></b>";
                        } else {
                            if (k + 1 == word.getUniversalDependency().to()){
                                sentenceString += " <b><font color=\"blue\">" + wordList.get(k).getName() + "</font></b>";
                            } else {
                                sentenceString += " " + wordList.get(k).getName();
                            }
                        }
                    }
                    row.add(sentenceString + "</html>");
                } else {
                    row.add("-");
                    row.add("-");
                    row.add(sentence.toWords());
                }
                row.add("" + i);
                row.add("0");
                data.add(row);
            }
        }
    }

    public ViewDependencyAnnotationFrame(AnnotatedCorpus corpus, SentenceDependencyFrame sentenceDependencyFrame){
        super(corpus);
        COLOR_COLUMN_INDEX = 7;
        TAG_INDEX = 4;
        prepareData(corpus);
        dataTable = new JTable(new DependencyTableDataModel());
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMinWidth(150);
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMinWidth(60);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(WORD_INDEX).setWidth(200);
        dataTable.getColumnModel().getColumn(3).setMinWidth(60);
        dataTable.getColumnModel().getColumn(3).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setMinWidth(100);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setMaxWidth(100);
        dataTable.getColumnModel().getColumn(5).setWidth(300);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2){
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        sentenceDependencyFrame.addPanelToFrame(sentenceDependencyFrame.generatePanel(EditorPanel.phrasePath, fileName), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
