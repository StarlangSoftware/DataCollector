package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class TreeEnglishViewerFrame extends TreeViewerFrame{

    /**
     * Constructor for the TreeEnglishViewerFrame Frame. When the user selects open item in the menu,
     * the user will select a parse tree file, and a new TreeViewerPanel will be generated. The new panel will be
     * inserted into this frame.
     */
    public TreeEnglishViewerFrame(){
        itemOpenMultiple.setVisible(false);
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select English tree file");
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

    /**
     * The function displays the next tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of nextTree(3), ViewerPanel will display 0126.train. If the next tree
     * does not exist, nothing will happen.
     * @param count Number of trees to go forward
     */
    protected void nextTree(int count){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.nextTree(count);
            current.nodeWidth = widthSlider.getValue() * 5;
            current.repaint();
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
