package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedWord;
import DataCollector.*;
import DataCollector.ParseTree.EditorPanel;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AnnotatorFrame extends DataCollector {

    protected abstract AnnotatorPanel generatePanel(String currentPath, String rawFileName);

    /**
     * The {@link AnnotatorFrame} constructor takes a prefix as input and starts to creates items of the menu. I.e fastbackward, fast_backward,
     * fastfastbackward, and fastfastforward. It then gets the list of files and loops through them, if there has been some
     * trained models it adds them to the model {@link JComboBox}. Then, adds actions to this {@link JComboBox} in order to
     * select project and annotation files.
     */
    public AnnotatorFrame() {
        JButton button;
        button = new DrawingButton(DataCollector.class, this, "fastfastbackward", FAST_FAST_BACKWARD, "Previous 100 Sentence");
        button.setVisible(true);
        toolBar.add(button, 0);
        button = new DrawingButton(DataCollector.class, this, "fastbackward", FAST_BACKWARD, "Previous 10 Sentence");
        button.setVisible(true);
        toolBar.add(button, 1);
        button = new DrawingButton(DataCollector.class, this, "fastforward", FAST_FORWARD, "Next 10 Sentence");
        button.setVisible(true);
        toolBar.add(button, 4);
        button = new DrawingButton(DataCollector.class, this, "fastfastforward", FAST_FAST_FORWARD, "Next 100 Sentence");
        button.setVisible(true);
        toolBar.add(button, 5);
        toolBar.addSeparator();
        File[] listOfFiles = new File(".").listFiles();
        Arrays.sort(listOfFiles);
        projectPane.addChangeListener(e -> {
            AnnotatorPanel current;
            if (projectPane.getTabCount() > 0) {
                current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
                if (current != null) {
                    updateInfo(current);
                }
            }
        });
        itemClose.setEnabled(true);
        itemCloseAll.setEnabled(true);
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select annotated sentence file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(EditorPanel.TURKISH_PHRASE_PATH));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                AnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fcinput.getSelectedFile().getName());
                addPanelToFrame(annotatorPanel, fcinput.getSelectedFile().getName());
            }
        });
        itemOpenMultiple.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file containing file names");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                ArrayList<FileWithSelectedWords> fileList = loadMultipleFileNames(fcinput.getSelectedFile().getParent() + "/" + fcinput.getSelectedFile().getName());
                for (FileWithSelectedWords fileItem : fileList) {
                    AnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fileItem.getFileName());
                    for (int i = 0; i < fileItem.size(); i++) {
                        for (int j = 0; j < annotatorPanel.sentence.wordCount(); j++) {
                            if (fileItem.getWord(i).equals(annotatorPanel.sentence.getWord(j).getName())) {
                                ((AnnotatedWord) annotatorPanel.sentence.getWord(j)).setSelected(true);
                                break;
                            }
                        }
                    }
                    addPanelToFrame(annotatorPanel, fileItem.getFileName());
                }
            }
        });
    }

    /**
     * The actionPerformed method takes an {@link ActionEvent} as an input and calls previous or next methods according to
     * this {@link ActionEvent}. If the {@link ActionEvent} is;
     * BACKWARD, it goes to previous {@link AnnotatorPanel},
     * FORWARD, it goes to next {@link AnnotatorPanel};
     * FAST_BACKWARD it goes 10 before,
     * FAST_FAST_BACKWARD it goes 100 before,
     * FAST_FORWARD it goes 10 after,
     * FAST_FAST_FORWARD it goes 100 after the current {@link AnnotatorPanel}.
     *
     * @param e {@link ActionEvent} input.
     */
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case BACKWARD:
                previous(1);
                break;
            case FORWARD:
                next(1);
                break;
            case FAST_BACKWARD:
                previous(10);
                break;
            case FAST_FORWARD:
                next(10);
                break;
            case FAST_FAST_BACKWARD:
                previous(100);
                break;
            case FAST_FAST_FORWARD:
                next(100);
                break;
        }
    }

    /**
     * The updateInfo method takes an {@link AnnotatorPanel} as input and sets the title of the current {@link JTabbedPane},
     * as the file name. Then sets the infoTop and infoBottom {@link JLabel}s.
     *
     * @param current {@link AnnotatorPanel} input.
     */
    public void updateInfo(AnnotatorPanel current) {
        projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        infoTop.setText("<html>" + current.getSourceSentence() + "</html>");
        infoBottom.setText("<html>" /*+ current.getOriginalSentence()*/ + "</html>");
    }

    /**
     * The addPanelToFrame method takes an {@link AnnotatorPanel} and a file name as inputs. It creates a new {@link JScrollPane}
     * and adds given {@link AnnotatorPanel} which can be scrolled. It then adds to {@link JTabbedPane}, updates top and bottom info.
     *
     * @param annotatorPanel {@link AnnotatorPanel} input.
     * @param fileName       File name input.
     */
    protected void addPanelToFrame(AnnotatorPanel annotatorPanel, String fileName) {
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(annotatorPanel);
        projectPane.add(treePane, fileName);
        projectPane.setSelectedIndex(projectPane.getTabCount() - 1);
        updateInfo(annotatorPanel);
        annotatorPanel.setFocusable(true);
        enableMenu();
    }

    /**
     * The next method takes an int count as input and moves forward along the {@link AnnotatorPanel}s as much as the count.
     * At the end updates the top and bottom info.
     *
     * @param count Integer count is used to move.
     */
    public void next(int count) {
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.next(count);
        updateInfo(current);
    }

    /**
     * The next method takes an int count as input and moves backward along the {@link AnnotatorPanel}s as much as the count.
     * At the end updates the top and bottom info.
     *
     * @param count Integer count is used to move.
     */
    public void previous(int count) {
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.previous(count);
        updateInfo(current);
    }

}
