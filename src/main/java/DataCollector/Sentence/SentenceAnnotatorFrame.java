package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.*;
import DataCollector.ParseTree.TreeEditorPanel;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public abstract class SentenceAnnotatorFrame extends DataCollector {

    protected JSlider widthSlider;

    protected abstract SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName);

    /**
     * The {@link SentenceAnnotatorFrame} constructor takes a prefix as input and starts to creates items of the menu. I.e fastbackward, fast_backward,
     * fastfastbackward, and fastfastforward. It then gets the list of files and loops through them, if there has been some
     * trained models it adds them to the model {@link JComboBox}. Then, adds actions to this {@link JComboBox} in order to
     * select project and annotation files.
     */
    public SentenceAnnotatorFrame() {
        Properties properties;
        properties = new Properties();
        try {
            properties.load(Files.newInputStream(new File("config.properties").toPath()));
            TreeEditorPanel.treePath = properties.getProperty("treePath", TreeEditorPanel.treePath);
            TreeEditorPanel.phrasePath = properties.getProperty("phrasePath", TreeEditorPanel.phrasePath);
        } catch (IOException ignored) {
        }
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
        widthSlider = new JSlider(SwingConstants.HORIZONTAL, 5, 80, 60);
        widthSlider.setMinorTickSpacing(1);
        widthSlider.setMajorTickSpacing(5);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintLabels(true);
        widthSlider.setMaximumSize(new Dimension(250, 35));
        toolBar.add(widthSlider);
        widthSlider.addChangeListener(e -> setWordSpace(widthSlider.getValue()));
        File[] listOfFiles = new File(".").listFiles();
        if (listOfFiles != null){
            Arrays.sort(listOfFiles);
        }
        projectPane.addChangeListener(e -> {
            SentenceAnnotatorPanel current;
            if (projectPane.getTabCount() > 0) {
                current = (SentenceAnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
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
            fcinput.setCurrentDirectory(new File(TreeEditorPanel.phrasePath));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                SentenceAnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fcinput.getSelectedFile().getName());
                addPanelToFrame(annotatorPanel, fcinput.getSelectedFile().getName());
            }
        });
        itemGoToFile.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(null, "Annotated sentence file name:", "",
                    JOptionPane.PLAIN_MESSAGE);
            if (result != null) {
                SentenceAnnotatorPanel annotatorPanel = generatePanel(TreeEditorPanel.phrasePath, result);
                addPanelToFrame(annotatorPanel, result);
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
                    SentenceAnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fileItem.getFileName());
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

    protected AnnotatedCorpus readCorpus(String subFolder){
        AnnotatedCorpus corpus;
        if (subFolder.equals("false")){
            corpus = new AnnotatedCorpus(new File(TreeEditorPanel.phrasePath));
        } else {
            corpus = new AnnotatedCorpus();
            File[] listOfFiles = new File(TreeEditorPanel.phrasePath).listFiles();
            if (listOfFiles != null){
                Arrays.sort(listOfFiles);
                for (File file:listOfFiles){
                    if (file.isDirectory() && !file.isHidden()){
                        corpus.combine(new AnnotatedCorpus(file));
                    }
                }
            }
        }
        return corpus;
    }

    /**
     * The actionPerformed method takes an {@link ActionEvent} as an input and calls previous or next methods according to
     * this {@link ActionEvent}. If the {@link ActionEvent} is;
     * BACKWARD, it goes to previous {@link SentenceAnnotatorPanel},
     * FORWARD, it goes to next {@link SentenceAnnotatorPanel};
     * FAST_BACKWARD it goes 10 before,
     * FAST_FAST_BACKWARD it goes 100 before,
     * FAST_FORWARD it goes 10 after,
     * FAST_FAST_FORWARD it goes 100 after the current {@link SentenceAnnotatorPanel}.
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

    protected void setWordSpace(int wordSpace){
        SentenceAnnotatorPanel current = (SentenceAnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setWordSpace(wordSpace);
    }

    /**
     * The updateInfo method takes an {@link SentenceAnnotatorPanel} as input and sets the title of the current {@link JTabbedPane},
     * as the file name. Then sets the infoTop and infoBottom {@link JLabel}s.
     *
     * @param current {@link SentenceAnnotatorPanel} input.
     */
    public void updateInfo(SentenceAnnotatorPanel current) {
        projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        infoTop.setText("<html>" + current.getSourceSentence() + "</html>");
        infoBottom.setText("<html>" /*+ current.getOriginalSentence()*/ + "</html>");
    }

    /**
     * The addPanelToFrame method takes an {@link SentenceAnnotatorPanel} and a file name as inputs. It creates a new {@link JScrollPane}
     * and adds given {@link SentenceAnnotatorPanel} which can be scrolled. It then adds to {@link JTabbedPane}, updates top and bottom info.
     *
     * @param annotatorPanel {@link SentenceAnnotatorPanel} input.
     * @param fileName       File name input.
     */
    public void addPanelToFrame(SentenceAnnotatorPanel annotatorPanel, String fileName) {
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(annotatorPanel);
        projectPane.add(treePane, fileName);
        projectPane.setSelectedIndex(projectPane.getTabCount() - 1);
        updateInfo(annotatorPanel);
        annotatorPanel.setFocusable(true);
        enableMenu();
    }

    /**
     * The next method takes an int count as input and moves forward along the {@link SentenceAnnotatorPanel}s as much as the count.
     * At the end updates the top and bottom info.
     *
     * @param count Integer count is used to move.
     */
    public void next(int count) {
        SentenceAnnotatorPanel current;
        current = (SentenceAnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.next(count);
        updateInfo(current);
    }

    /**
     * The next method takes an int count as input and moves backward along the {@link SentenceAnnotatorPanel}s as much as the count.
     * At the end updates the top and bottom info.
     *
     * @param count Integer count is used to move.
     */
    public void previous(int count) {
        SentenceAnnotatorPanel current;
        current = (SentenceAnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.previous(count);
        updateInfo(current);
    }

}
