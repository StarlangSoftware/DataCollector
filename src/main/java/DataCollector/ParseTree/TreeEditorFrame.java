package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.*;
import Translation.AutomaticTranslationDictionary;
import Translation.BilingualDictionary;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public abstract class TreeEditorFrame extends DataCollector{

    protected abstract TreeEditorPanel generatePanel(String currentPath, String rawFileName);
    protected JSlider widthSlider;
    protected JSlider heightSlider;
    /**
     * Menu to display the basic tree operations, such as next tree, previous tree, etc.
     */
    protected JMenu treeMenu;
    /**
     * Menu items responsible for displaying the next and previous trees and saving trees.
     */
    protected JMenuItem itemNext, itemPrevious, itemSaveTree;
    /**
     * In NER, Morphological analyzer and semantic frames, the system displays maximum likelihood estimate for each word.
     * The counts for each word with its NER label, morphological analysis or semantic WordNet id is stored in a dictionary.
     * The system checks for each word, if it's NER label, morphological analysis or semantic WordNet id exists, and put that
     * maximum occurring label as a default label.
     */
    protected AutomaticTranslationDictionary dictionary;
    /**
     * In translator panel, the system automatically fills the list box for a candidate English word with its possible translations
     * retrieved from a bilingual dictionary.
     */
    protected BilingualDictionary bilingualDictionary;

    /**
     * Constructor for the Editor Frame. This Frame will be automatically inherited from Translator, NER, MorphologicalAnalyzer, Dependency
     * and Semantic frames. The constructor simply adds next and previous tree menu items and connects corresponding ActionListeners
     * to those menu items. The method also checks if the active tab changed in the TabbedPane. When the active tab changes in the TabbedPane,
     * the method should display source and target language sentence in the bottom info labels. Another function is adding undo ActionListener
     * to undo menu item.
     */
    public TreeEditorFrame(){
        Properties properties;
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("config.properties")));
            TreeEditorPanel.treePath = properties.getProperty("treePath", TreeEditorPanel.treePath);
            TreeEditorPanel.phrasePath = properties.getProperty("phrasePath", TreeEditorPanel.phrasePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JButton button;
        button = new DrawingButton(DataCollector.class, this, "fastfastbackward", FAST_FAST_BACKWARD, "Previous 100 Tree");
        button.setVisible(true);
        toolBar.add(button, 0);
        button = new DrawingButton(DataCollector.class, this, "fastbackward", FAST_BACKWARD, "Previous 10 Tree");
        button.setVisible(true);
        toolBar.add(button, 1);
        button = new DrawingButton(DataCollector.class, this, "fastforward", FAST_FORWARD, "Next 10 Tree");
        button.setVisible(true);
        toolBar.add(button, 4);
        button = new DrawingButton(DataCollector.class, this, "fastfastforward", FAST_FAST_FORWARD, "Next 100 Tree");
        button.setVisible(true);
        toolBar.add(button, 5);
        treeMenu = new JMenu("Tree");
        menu.add(treeMenu);
        itemNext = addMenuItem(treeMenu, "Next Tree", KeyStroke.getKeyStroke('s'));
        itemPrevious = addMenuItem(treeMenu, "Previous Tree", KeyStroke.getKeyStroke('w'));
        itemSaveTree = addMenuItem(treeMenu, "Save Tree In Svg Format", KeyStroke.getKeyStroke('t'));
        treeMenu.addSeparator();
        JMenuItem itemUndo = addMenuItem(treeMenu, "Undo", KeyStroke.getKeyStroke("control Z"));
        itemSave.setVisible(false);
        toolBar.addSeparator();
        widthSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 25, 12);
        widthSlider.setMinorTickSpacing(1);
        widthSlider.setMajorTickSpacing(5);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintLabels(true);
        widthSlider.setMaximumSize(new Dimension(250, 35));
        toolBar.add(widthSlider);
        widthSlider.addChangeListener(e -> setNodeWidth(widthSlider.getValue() * 5));
        heightSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 8);
        heightSlider.setMinorTickSpacing(1);
        heightSlider.setMajorTickSpacing(1);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);
        heightSlider.setMaximumSize(new Dimension(150, 35));
        toolBar.add(heightSlider);
        heightSlider.addChangeListener(e -> setNodeHeight(40 + heightSlider.getValue() * 5));
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
            fcinput.setDialogTitle("Select project file");
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
                    if (editorPanel != null){
                        addPanelToFrame(editorPanel, fileItem.getFileName());
                    }
                }
            }
        });
        itemNext.addActionListener(e -> nextTree(1));
        itemPrevious.addActionListener(e -> previousTree(1));
        itemSaveTree.addActionListener(e -> saveTree());
    }

    /**
     * If the current tree displayed in the panel is changed, this function will display source and target sentences in the
     * labels shown in the bottom. In some parse trees, the tree is so large that, the display does not fit in a single line.
     * For that purpose, the labels 's text are set with html tags.
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

    public void loadAutomaticTranslationDictionary(AutomaticTranslationDictionary dictionary){
        this.dictionary = dictionary;
    }

    public void loadBilingualDictionary(BilingualDictionary bilingualDictionary){
        this.bilingualDictionary = bilingualDictionary;
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

    protected void setNodeWidth(int nodeWidth){
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeWidth(nodeWidth);
    }

    protected void setNodeHeight(int nodeHeight){
        TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeHeight(nodeHeight);
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
     * Saves current tree in the panel of this frame.
     */
    protected void saveTree(){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.saveTree();
        }
    }

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
