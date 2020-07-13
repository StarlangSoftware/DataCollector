package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import AnnotatedTree.*;
import DataCollector.Sentence.ViewSemanticAnnotationFrame;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class SyntacticFrame extends StructureEditorFrame{
    static final protected String ADDPARENT = "addparent";
    static final protected String EDITSYMBOL = "editsymbol";
    static final protected String DELETESYMBOL = "deletesymbol";
    static final protected String SPLITNODE = "splitnode";
    static final protected String DELETESUBTREE = "deletesubtree";

    public SyntacticFrame(){
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(EditorPanel.treePath));
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
        button = new DrawingButton(DataCollector.class, this, "split", SPLITNODE, "Split Node");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        button = new DrawingButton(DataCollector.class, this, "delete", DELETESYMBOL, "Delete Symbol");
        button.setVisible(true);
        toolBar.add(button);
        button = new DrawingButton(DataCollector.class, this, "clear", DELETESUBTREE, "Delete Subtree");
        button.setVisible(true);
        toolBar.add(button);
        toolBar.addSeparator();
        JMenuItem itemAddParent = addMenuItem(treeMenu, "Add Parent", KeyStroke.getKeyStroke('p'));
        JMenuItem itemEditSymbol = addMenuItem(treeMenu, "Edit Symbol", KeyStroke.getKeyStroke('e'));
        JMenuItem itemDeleteSymbol = addMenuItem(treeMenu, "Delete Symbol", KeyStroke.getKeyStroke('l'));
        JMenuItem itemSplitNode = addMenuItem(treeMenu, "Split Node", KeyStroke.getKeyStroke('t'));
        JMenuItem itemDeleteSubtree = addMenuItem(treeMenu, "Delete Subtree", KeyStroke.getKeyStroke('b'));
        itemAddParent.addActionListener(e -> addParent());
        itemEditSymbol.addActionListener(e -> editSymbol());
        itemDeleteSymbol.addActionListener(e -> deleteSymbol());
        itemSplitNode.addActionListener(e -> splitNode());
        itemDeleteSubtree.addActionListener(e -> deleteSubtree());
        JMenuItem itemViewRules = addMenuItem(projectMenu, "View Rules", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewRules.addActionListener(e -> {
            new ViewSyntacticRuleFrame(treeBank);
        });
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

    protected void deleteSubtree(){
        SyntacticPanel current = (SyntacticPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.deleteSubtree();
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
            case DELETESUBTREE:
                deleteSubtree();
                break;
        }
    }
}
