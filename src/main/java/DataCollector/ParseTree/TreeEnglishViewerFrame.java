package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class TreeEnglishViewerFrame extends DataCollector{
    protected JSlider widthSlider;
    protected JSlider heightSlider;

    /**
     * Menu to display the basic tree operations, such as next tree, previous tree, etc.
     */
    protected JMenu treeMenu;
    /**
     * Menu items responsible for displaying the next and previous trees and saving trees.
     */
    protected JMenuItem itemNext, itemPrevious;

    /**
     * Constructor for the Editor Frame. This Frame will be automatically inherited from Translator, NER, MorphologicalAnalyzer, Dependency
     * and Semantic frames. The constructor simply adds next and previous tree menu items and connects corresponding ActionListeners
     * to those menu items. The method also checks if the active tab changed in the TabbedPane. When the active tab changes in the TabbedPane,
     * the method should display source and target language sentence in the bottom info labels. Another function is adding undo ActionListener
     * to undo menu item.
     */
    public TreeEnglishViewerFrame(){
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
        treeMenu.addSeparator();
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
        itemOpenMultiple.setVisible(false);
        itemSave.setVisible(false);
        heightSlider.addChangeListener(e -> setNodeHeight(40 + heightSlider.getValue() * 5));
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(TreeEditorPanel.englishPath));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                TreeViewerPanel viewerPanel = new TreeViewerPanel(fcinput.getSelectedFile().getParent(), fcinput.getSelectedFile().getName(), ViewLayerType.WORD);
                addPanelToFrame(viewerPanel, fcinput.getSelectedFile().getName());
            }
        });
        itemNext.addActionListener(e -> nextTree(1));
        itemPrevious.addActionListener(e -> previousTree(1));
    }

    protected void setNodeWidth(int nodeWidth){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeWidth(nodeWidth);
    }

    protected void setNodeHeight(int nodeHeight){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeHeight(nodeHeight);
    }
    /**
     * Adds the given editor panel to this frame.
     * @param viewerPanel Viewer panel to be added.
     * @param fileName Name of the file that is displayed on the tab.
     */
    protected void addPanelToFrame(TreeViewerPanel viewerPanel, String fileName){
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(viewerPanel);
        projectPane.add(treePane, fileName);
        enableMenu();
        viewerPanel.setFocusable(true);
    }

    protected void nextTree(int count){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.nextTree(count);
            current.nodeWidth = widthSlider.getValue() * 5;
            current.repaint();
            projectPane.setTitleAt(projectPane.getSelectedIndex(), current.getRawFileName());
        }
    }

    protected void previousTree(int count){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.previousTree(count);
            current.nodeWidth = widthSlider.getValue() * 5;
            current.repaint();
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
