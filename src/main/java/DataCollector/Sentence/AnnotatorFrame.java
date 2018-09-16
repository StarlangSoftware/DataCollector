package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedWord;
import Classification.Model.Model;
import DataCollector.*;
import DataCollector.ParseTree.EditorPanel;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AnnotatorFrame extends DataCollector{
    protected JComboBox modelComboBox;
    protected ArrayList<Model> models;
    protected abstract AnnotatorPanel generatePanel(String currentPath, String rawFileName);

    public AnnotatorFrame(String prefix){
        FileInputStream inFile;
        ObjectInputStream inObject;
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
        ArrayList<String> modelNames = new ArrayList<String>();
        models = new ArrayList<>();
        models.add(null);
        for (File file : listOfFiles){
            if (file.getName().endsWith(".bin") && file.getName().startsWith(prefix)){
                modelNames.add(file.getName().substring(prefix.length() + 1, file.getName().indexOf(".bin")));
            }
        }
        String[] modelArray = new String[modelNames.size() + 1];
        modelArray[0] = "None";
        for (int i = 1; i <= modelNames.size(); i++){
            modelArray[i] = modelNames.get(i - 1);
        }
        modelComboBox = new JComboBox(modelArray);
        modelComboBox.setMaximumSize(new Dimension(250, 35));
        modelComboBox.setSelectedIndex(0);
        modelComboBox.addActionListener(e -> {
            if (modelComboBox.getSelectedIndex() < models.size()){
                for (int i = 0; i < projectPane.getTabCount(); i++){
                    AnnotatorPanel panel = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
                    panel.classificationModel = models.get(modelComboBox.getSelectedIndex());
                }
            }
        });
        toolBar.add(modelComboBox);
        for (File file : listOfFiles){
            if (file.getName().endsWith(".bin") && file.getName().startsWith(prefix)){
                try {
                    inFile = new FileInputStream(file);
                    inObject = new ObjectInputStream(inFile);
                    models.add((Model) inObject.readObject());
                } catch (ClassNotFoundException | IOException e) {
                }
            }
        }
        projectPane.addChangeListener(e -> {
            AnnotatorPanel current;
            if (projectPane.getTabCount() > 0){
                current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
                if (current != null){
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
            if (returnVal == JFileChooser.APPROVE_OPTION){
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
                for (FileWithSelectedWords fileItem : fileList){
                    AnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fileItem.getFileName());
                    for (int i = 0; i < fileItem.size(); i++){
                        for (int j = 0; j < annotatorPanel.sentence.wordCount(); j++){
                            if (fileItem.getWord(i).equals(annotatorPanel.sentence.getWord(j).getName())){
                                ((AnnotatedWord)annotatorPanel.sentence.getWord(j)).setSelected(true);
                                break;
                            }
                        }
                    }
                    addPanelToFrame(annotatorPanel, fileItem.getFileName());
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
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

    public void updateInfo(AnnotatorPanel current){
        projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        infoTop.setText("<html>" + current.getSourceSentence() + "</html>");
        infoBottom.setText("<html>" + "</html>");
    }

    protected void addPanelToFrame(AnnotatorPanel annotatorPanel, String fileName){
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(annotatorPanel);
        projectPane.add(treePane, fileName);
        projectPane.setSelectedIndex(projectPane.getTabCount() - 1);
        updateInfo(annotatorPanel);
        annotatorPanel.setFocusable(true);
        enableMenu();
    }

    public void next(int count){
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.next(count);
        updateInfo(current);
    }

    public void previous(int count){
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.previous(count);
        updateInfo(current);
    }

}
