package DataCollector.ParseTree;

import AnnotatedTree.AutoProcessor.AutoNER.TreeAutoNER;
import AnnotatedTree.AutoProcessor.AutoNER.TurkishTreeAutoNER;

import javax.swing.*;

public class NERFrame extends EditorFrame{
    private JCheckBox autoNEROption;

    public NERFrame(){
        this.setTitle("Named Entity Recognition Editor");
        autoNEROption = new JCheckBox("AutoNER", true);
        toolBar.add(autoNEROption);
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new NERPanel(currentPath, rawFileName, true);
    }

    private void autoNER(){
        TreeAutoNER treeAutoNER;
        if (autoNEROption.isSelected()){
            EditorPanel current = (EditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
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
