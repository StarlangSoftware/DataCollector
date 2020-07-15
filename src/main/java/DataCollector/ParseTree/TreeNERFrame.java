package DataCollector.ParseTree;

import AnnotatedTree.AutoProcessor.AutoNER.TreeAutoNER;
import AnnotatedTree.AutoProcessor.AutoNER.TurkishTreeAutoNER;
import AnnotatedTree.TreeBankDrawable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class TreeNERFrame extends TreeEditorFrame {
    private JCheckBox autoNEROption;

    public TreeNERFrame(){
        this.setTitle("Named Entity Recognition Editor");
        autoNEROption = new JCheckBox("AutoNER", true);
        toolBar.add(autoNEROption);
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TreeEditorPanel.treePath));
        JMenuItem itemViewAnnotations = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotations.addActionListener(e -> {
            new ViewTreeNERAnnotationFrame(treeBank, this);
        });
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeNERPanel(currentPath, rawFileName, true);
    }

    private void autoNER(){
        TreeAutoNER treeAutoNER;
        if (autoNEROption.isSelected()){
            TreeEditorPanel current = (TreeEditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            treeAutoNER = new TurkishTreeAutoNER();
            treeAutoNER.autoNER(current.currentTree);
            current.currentTree.reload();
            current.repaint();
        }
    }

    protected void nextTree(int count){
        super.nextTree(count);
        autoNER();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        autoNER();
    }


}
