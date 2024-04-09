package DataCollector.ParseTree;

import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;

public class TreeViewerFrame extends DataCollector {
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
     * The constructor simply adds next and previous tree menu items and connects corresponding ActionListeners to
     * those menu items. Adds width and height sliders to arrange the horizontal and vertical distances between nodes
     * in the tree.
     */
    public TreeViewerFrame() {
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
        heightSlider.addChangeListener(e -> setNodeHeight(40 + heightSlider.getValue() * 5));
        itemSave.setVisible(false);
    }

    /**
     * Sets the horizontal distance in terms of pixels between two nodes in the tree.
     * @param nodeWidth The new horizontal distance
     */
    protected void setNodeWidth(int nodeWidth){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeWidth(nodeWidth);
    }

    /**
     * Sets the vertical distance in terms of pixels between two nodes in the tree.
     * @param nodeHeight The new vertical distance
     */
    protected void setNodeHeight(int nodeHeight){
        TreeViewerPanel current = (TreeViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setNodeHeight(nodeHeight);
    }

}
