package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;

public abstract class TreeEditorFrame extends TreeViewerFrame{

    protected abstract TreeEditorPanel generatePanel(String currentPath, String rawFileName);

    /**
     * <p>The constructor first reads the properties file which gets the tree and sentence
     * paths for tree and sentence corpora respectively.</p>
     *
     * <p>This Frame will be automatically inherited from Translator, NER, MorphologicalAnalyzer, Dependency
     * and Semantic frames.</p>
     * The menu consists of:
     * <p>itemOpen: The user will select a tree file, and a new panel depending on the task will be
     * generated via the abstract method generatePanel. The new panel will be inserted into this frame.</p>
     * <p>itemGoToFile: The user will enter a file name, then the system will get that tree file,
     * and a new panel depending on the task will be generated via the abstract method generatePanel. The new panel
     * will be inserted into this frame.</p>
     * <p>itemOpenMultiple: The user will select a file which contains the filenames and also possibly words in that
     * trees residing in those files, then the system will generate that many panels via the abstract
     * method generatePanel. The words will also be selected (done with selected attribute) in those panels. All those
     * panels will be inserted into this frame.</p>
     */
    public TreeEditorFrame(){
        Properties properties;
        properties = new Properties();
        try {
            properties.load(Files.newInputStream(new File("config.properties").toPath()));
            TreeEditorPanel.treePath = properties.getProperty("treePath", TreeEditorPanel.treePath);
            TreeEditorPanel.phrasePath = properties.getProperty("phrasePath", TreeEditorPanel.phrasePath);
        } catch (IOException ignored) {
        }
        JMenuItem itemUndo = addMenuItem(treeMenu, "Undo", KeyStroke.getKeyStroke("control Z"));
        itemUndo.addActionListener(e -> undo());
        projectPane.addChangeListener(c -> {
            if (projectPane.getSelectedIndex() != -1) {
                TreeViewerPanel viewerPanel;
                viewerPanel = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
                if (viewerPanel != null) {
                    viewerPanel.setFocusable(true);
                    infoBottom.setText("<html>" + viewerPanel.getTargetSentence() + "</html>");
                    infoTop.setText("<html>" + viewerPanel.getSourceSentence() + "</html>");
                }
            }
        });
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select annotated tree file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(TreeEditorPanel.treePath));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                TreeEditorPanel editorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fcinput.getSelectedFile().getName());
                if (editorPanel != null){
                    addPanelToFrame(editorPanel, fcinput.getSelectedFile().getName());
                }
            }
        });
        itemGoToFile.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(null, "Annotated tree file name:", "",
                    JOptionPane.PLAIN_MESSAGE);
            if (result != null) {
                TreeEditorPanel editorPanel = generatePanel(TreeEditorPanel.treePath, result);
                if (editorPanel != null){
                    addPanelToFrame(editorPanel, result);
                }
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
                    TreeEditorPanel editorPanel = generatePanel(fcinput.getSelectedFile().getParent(), fileItem.getFileName());
                    NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) editorPanel.currentTree.getRoot(), new IsTurkishLeafNode());
                    ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
                    for (int i = 0; i < fileItem.size(); i++){
                        for (ParseNodeDrawable parseNode : leafList){
                            if (fileItem.getWord(i).equals(parseNode.getLayerData(ViewLayerType.TURKISH_WORD))){
                                parseNode.setSelected(true);
                                break;
                            }
                        }
                    }
                    addPanelToFrame(editorPanel, fileItem.getFileName());
                }
            }
        });
        itemNext.addActionListener(e -> nextTree(1));
        itemPrevious.addActionListener(e -> previousTree(1));
    }

    /**
     * If the current tree displayed in the panel is changed, this function will display source and target sentences in the
     * labels shown in the bottom. In some parse trees, the tree is so large that, the display does not fit in a single line.
     * For that purpose, the label's text are set with html tags.
     * @param current Current panel that displays the current tree.
     */
    protected void updateInfo(TreeEditorPanel current){
        if (current.currentTree.layerAll(ViewLayerType.TURKISH_WORD)){
            if (current.currentTree.layerAll(ViewLayerType.PART_OF_SPEECH)){
                infoTop.setForeground(Color.MAGENTA);
            } else {
                infoTop.setForeground(new Color(0, 128, 0));
            }
        } else {
            infoTop.setForeground(Color.BLUE);
        }
        infoTop.setText("<html>" + current.getTargetSentence() + "</html>");
        infoBottom.setText("<html>" + current.getSourceSentence() + "</html>");
    }

    /**
     * Undo method for the current panel displayed in this frame. Undo depends on the information displayed in the current
     * panel. Undo corresponds: For Translator panel, to remove the translation and going back to the original
     * (if exists) translation. Or, unswapping the last swapped subtrees. For NER panel, to remove the NER label and going
     * back to the original (if exists) label. For MorphologicalAnalyzer panel, to remove the morphological analysis and
     * going back to the original (if exists) morphological analysis.
     */
    protected void undo(){
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.undo();
        }
    }
    
    /**
     * Adds the given editor panel to this frame.
     * @param editorPanel Editor panel to be added.
     * @param fileName Name of the file that is displayed on the tab.
     */
    public void addPanelToFrame(TreeEditorPanel editorPanel, String fileName){
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(editorPanel);
        projectPane.add(treePane, fileName);
        updateInfo(editorPanel);
        enableMenu();
        editorPanel.setFocusable(true);
    }

    /**
     * The function displays the next tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of nextTree(3), ViewerPanel will display 0126.train. If the next tree
     * does not exist, nothing will happen.
     * @param count Number of trees to go forward
     */
    protected void nextTree(int count){
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.nextTree(count);
            current.nodeWidth = widthSlider.getValue() * 5;
            current.repaint();
            updateInfo(current);
            projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        }
    }

    /**
     * Overloaded function that displays the previous tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of previousTree(4), ViewerPanel will display 0119.train. If the
     * previous tree does not exist, nothing will happen.
     * @param count Number of trees to go backward
     */
    protected void previousTree(int count){
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.previousTree(count);
            current.nodeWidth = widthSlider.getValue() * 5;
            current.repaint();
            updateInfo(current);
            projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        }
    }

    /**
     * Each frame has different Action buttons. EditorFrame, since it is the uppermost parent class of all tree based frames,
     * this method calls appropriate methods for going forward and backward one tree.
     * @param e Action event to be responded. Depending on the action command of the event, the method calls previousTree
     *          or nextTree.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case BACKWARD:
                previousTree(1);
                break;
            case FORWARD:
                nextTree(1);
                break;
            case FAST_BACKWARD:
                previousTree(10);
                break;
            case FAST_FORWARD:
                nextTree(10);
                break;
            case FAST_FAST_BACKWARD:
                previousTree(100);
                break;
            case FAST_FAST_FORWARD:
                nextTree(100);
                break;
        }
    }


}
