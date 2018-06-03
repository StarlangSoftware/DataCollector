package DataCollector.ParseTree;

import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class StructureEditorFrame extends EditorFrame{
    static final protected String MOVELEFT = "moveleft";
    static final protected String MOVERIGHT = "moveright";

    public StructureEditorFrame(){
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

    protected void moveLeft(){
        StructureEditorPanel current = (StructureEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.moveLeft();
            updateInfo(current);
        }
    }

    protected void moveRight(){
        StructureEditorPanel current = (StructureEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.moveRight();
            updateInfo(current);
        }
    }

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
