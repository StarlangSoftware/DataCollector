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
        treeMenu.addSeparator();
        JMenuItem itemAddParent = addMenuItem(treeMenu, "Add Parent", KeyStroke.getKeyStroke('p'));
        JMenuItem itemEditSymbol = addMenuItem(treeMenu, "Edit Symbol", KeyStroke.getKeyStroke('e'));
        itemAddParent.addActionListener(e -> addParent());
        itemEditSymbol.addActionListener(e -> editSymbol());
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

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case ADDPARENT:
                addParent();
                break;
            case EDITSYMBOL:
                editSymbol();
                break;
        }
    }
}
