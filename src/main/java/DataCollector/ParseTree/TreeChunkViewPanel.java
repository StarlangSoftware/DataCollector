package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ChunkType;

public class TreeChunkViewPanel extends TreeEditorPanel {
    private ChunkType chunkType;

    public TreeChunkViewPanel(String path, String fileName) {
        super(path, fileName, ViewLayerType.SHALLOW_PARSE);
        chunkType = ChunkType.NORMAL;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void updateChunks(){
        currentTree.reload();
        currentTree.extractVerbal();
        currentTree.setShallowParseLayer(chunkType);
        repaint();
    }

    public void setChunkType(ChunkType chunkType){
        this.chunkType = chunkType;
        updateChunks();
    }

    protected void nextTree(int count){
        super.nextTree(count);
        updateChunks();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        updateChunks();
    }

}
