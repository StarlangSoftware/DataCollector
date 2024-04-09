package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import DataCollector.RowComparator3;
import DataCollector.ViewAnnotationFrame;
import Util.DrawingButton;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ViewSentenceAnnotationFrame extends ViewAnnotationFrame implements ActionListener {

    protected AnnotatedCorpus corpus;
    protected static final int FILENAME_INDEX = 0;
    protected static final int WORD_POS_INDEX = 1;
    protected int sortIndex;
    protected JTextField search;

    /**
     * Does specific functions for different buttons. If the command is
     *
     * <p>ID_SORT: The data are sorted based on annotation layer first, then the word, then the file name</p>
     * <p>WORD_SORT: The data are sorted based on word first, then the annotation layer, then the file name</p>
     * <p>COPY: A row is selected for pasting its annotation layer value.</p>
     * @param e Action event to be processed.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()) {
            case ID_SORT:
                sortIndex = TAG_INDEX;
                data.sort(new RowComparator3(TAG_INDEX, WORD_INDEX, FILENAME_INDEX));
                updateGroupColors(sortIndex);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case WORD_SORT:
                sortIndex = WORD_INDEX;
                data.sort(new RowComparator3(WORD_INDEX, TAG_INDEX, FILENAME_INDEX));
                updateGroupColors(sortIndex);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    public void scrollToText(){
        for (int i = 1; i < data.size() - 50; i++){
            if (data.get(i).get(sortIndex) != null && data.get(i).get(sortIndex).compareToIgnoreCase(search.getText()) >= 0){
                dataTable.scrollRectToVisible(dataTable.getCellRect(i + 50, 0, false));
                break;
            }
        }
    }

    /**
     * Constructor for the base sentence annotation frame. This frame displays annotated data in the annotated corpus in
     * a table. Depending on the annotation type, derived classes will appropriate columns to the table. Base class
     * only adds two sort buttons, one copy and one paste buttons to the toolbar.
     * @param corpus Annotated corpus.
     */
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
        search = new JTextField();
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scrollToText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scrollToText();
            }
        });
        search.setMaximumSize(new Dimension(100, 25));
        toolBar.add(search);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
