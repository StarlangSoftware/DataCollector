package DataCollector.Sentence;

import Classification.Model.Model;
import DataCollector.DataCollector;
import DataCollector.ParseTree.EditorPanel;

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
                ArrayList<String> fileList = loadMultipleFileNames(fcinput.getSelectedFile().getParent() + "/" + fcinput.getSelectedFile().getName());
                for (String fileName : fileList){
                    AnnotatorPanel annotatorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fileName);
                    addPanelToFrame(annotatorPanel, fileName);
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case BACKWARD:
                previous();
                break;
            case FORWARD:
                next();
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

    public void next(){
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.next();
        updateInfo(current);
    }

    public void previous(){
        AnnotatorPanel current;
        current = (AnnotatorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current == null)
            return;
        current.previous();
        updateInfo(current);
    }

}
