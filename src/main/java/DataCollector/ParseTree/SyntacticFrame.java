package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import AnnotatedTree.*;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SyntacticFrame extends StructureEditorFrame{
    static final protected String ADDPARENT = "addparent";
    static final protected String EDITSYMBOL = "editsymbol";
    static final protected String DELETESYMBOL = "deletesymbol";
    static final protected String SPLITNODE = "splitnode";

    public SyntacticFrame(){
        this.setTitle("Syntactic");
        JButton button;
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "addparent", ADDPARENT, "Add Parent");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "edit", EDITSYMBOL, "Edit Symbol");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "delete", DELETESYMBOL, "Delete Symbol");
        button.setVisible(true);
        toolBar.add(button);
        treeMenu.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "split", SPLITNODE, "Split Node");
        button.setVisible(true);
        toolBar.add(button);
        treeMenu.addSeparator();
        JMenuItem itemAddParent = addMenuItem(treeMenu, "Add Parent", KeyStroke.getKeyStroke('p'));
        JMenuItem itemEditSymbol = addMenuItem(treeMenu, "Edit Symbol", KeyStroke.getKeyStroke('e'));
        JMenuItem itemDeleteSymbol = addMenuItem(treeMenu, "Delete Symbol", KeyStroke.getKeyStroke('l'));
        JMenuItem itemSplitNode = addMenuItem(treeMenu, "Split Node", KeyStroke.getKeyStroke('t'));
        itemAddParent.addActionListener(e -> addParent());
        itemEditSymbol.addActionListener(e -> editSymbol());
        itemDeleteSymbol.addActionListener(e -> deleteSymbol());
        itemSplitNode.addActionListener(e -> splitNode());
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new SyntacticPanel(currentPath, rawFileName, ViewLayerType.TURKISH_WORD);
    }

    protected void addParent(){
        SyntacticPanel current = (SyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.addParent();
        }
    }

    protected void editSymbol(){
        SyntacticPanel current = (SyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.editSymbol();
        }
    }

    protected void deleteSymbol(){
        SyntacticPanel current = (SyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.deleteSymbol();
        }
    }

    protected void splitNode(){
        SyntacticPanel current = (SyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.splitNode();
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case ADDPARENT:
                addParent();
                break;
            case EDITSYMBOL:
                editSymbol();
                break;
            case DELETESYMBOL:
                deleteSymbol();
                break;
            case SPLITNODE:
                splitNode();
                break;
        }
    }
}
