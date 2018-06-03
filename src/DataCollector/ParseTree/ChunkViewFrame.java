package DataCollector.ParseTree;

import AnnotatedTree.ChunkType;

import javax.swing.*;
import java.awt.*;

public class ChunkViewFrame extends EditorFrame{
    private JComboBox<String> chunkTypeComboBox;

    public ChunkViewFrame(){
        this.setTitle("Chunking Viewer");
        toolBar.addSeparator();
        chunkTypeComboBox = new JComboBox<>(new String[]{"Exists", "Normal", "Detailed"});
        chunkTypeComboBox.setMaximumSize(new Dimension(120, 25));
        toolBar.add(chunkTypeComboBox);
        chunkTypeComboBox.addActionListener(e -> {
            switch (chunkTypeComboBox.getSelectedIndex()){
                case 0:
                    setChunkType(ChunkType.EXISTS);
                    break;
                case 1:
                    setChunkType(ChunkType.NORMAL);
                    break;
                case 2:
                    setChunkType(ChunkType.DETAILED);
                    break;
            }
        });
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new ChunkViewPanel(currentPath, rawFileName);
    }

    private void setChunkType(ChunkType chunkType){
        ChunkViewPanel current = (ChunkViewPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setChunkType(chunkType);
    }

}
