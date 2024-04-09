package DataCollector.ParseTree;

import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class TreeStructureEditorFrame extends TreeEditorFrame {
    static final protected String MOVELEFT = "moveleft";
    static final protected String MOVERIGHT = "moveright";

    /**
     * Base constructor for the tree structure modifier frames. It adds moveLeft and moveRight buttons and corresponding
     * action listeners.
     */
    public TreeStructureEditorFrame(){
        JButton button;
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "moveleft", MOVELEFT, "Move Subtree Left");
        button.setVisible(true);
        toolBar.add(button);
        button = new DrawingButton(DataCollector.class, this, "moveright", MOVERIGHT, "Move Subtree Right");
        button.setVisible(true);
        toolBar.add(button);
        treeMenu.addSeparator();
        JMenuItem itemLeft = addMenuItem(treeMenu, "Move Subtree Left", KeyStroke.getKeyStroke('a'));
        JMenuItem itemRight = addMenuItem(treeMenu, "Move Subtree Right", KeyStroke.getKeyStroke('d'));
        itemLeft.addActionListener(e -> moveLeft());
        itemRight.addActionListener(e -> moveRight());
    }

    /**
     * Swaps selected node with its left sibling.
     */
    protected void moveLeft(){
        TreeStructureEditorPanel current = (TreeStructureEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.moveLeft();
            updateInfo(current);
        }
    }

    /**
     * Swaps selected node with its right sibling.
     */
    protected void moveRight(){
        TreeStructureEditorPanel current = (TreeStructureEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.moveRight();
            updateInfo(current);
        }
    }

    /**
     * Depending on the action command of the event, the method calls moveLeft or moveRight.
     * @param e Action event to be responded.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case MOVELEFT:
                moveLeft();
                break;
            case MOVERIGHT:
                moveRight();
                break;
        }
    }

}
