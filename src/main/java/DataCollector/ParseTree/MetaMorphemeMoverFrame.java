package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import AnnotatedTree.AutoProcessor.AutoMetaMorphemeMovement.AutoMetaMorphemeMover;
import AnnotatedTree.AutoProcessor.AutoMetaMorphemeMovement.TurkishAutoMetaMorphemeMover;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MetaMorphemeMoverFrame extends StructureEditorFrame {
    private JCheckBox autoMetaMorphemeMovement;

    public MetaMorphemeMoverFrame(){
        this.setTitle("MetaMorpheme Mover");
        JButton button = new DrawingButton(DataCollector.class, this, "clear", CLEAR, "Clear Tree");
        button.setVisible(true);
        toolBar.add(button);
        autoMetaMorphemeMovement = new JCheckBox("AutoMetaMorphemeMovement", true);
        toolBar.add(autoMetaMorphemeMovement);
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new MetaMorphemeMoverPanel(currentPath, rawFileName);
    }

    private void autoPosMove(){
        AutoMetaMorphemeMover autoMetaMorphemeMover;
        if (autoMetaMorphemeMovement.isSelected()){
            EditorPanel current = (EditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            autoMetaMorphemeMover = new TurkishAutoMetaMorphemeMover();
            autoMetaMorphemeMover.autoPosMove(current.currentTree);
            current.currentTree.reload();
            current.repaint();
        }
    }

    protected void nextTree(int count){
        super.nextTree(count);
        autoPosMove();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        autoPosMove();
    }

    private void clearMetaMorphemeMovements(){
        EditorPanel current = (EditorPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.currentTree.clearLayer(ViewLayerType.META_MORPHEME_MOVED);
        current.currentTree.save();
        current.currentTree.reload();
        current.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case CLEAR:
                clearMetaMorphemeMovements();
                break;
        }
    }

}
