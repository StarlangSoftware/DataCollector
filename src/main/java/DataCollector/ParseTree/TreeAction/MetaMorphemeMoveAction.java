package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import MorphologicalAnalysis.MetamorphicParse;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MetaMorphemeMoveAction extends TreeEditAction{

    private final LayerInfo fromLayer;
    private final LayerInfo toLayer;
    private final int fromIndex;
    private MetamorphicParse removedParse;

    public MetaMorphemeMoveAction(TreeEditorPanel associatedPanel, LayerInfo fromLayer, LayerInfo toLayer, int fromIndex){
        this.associatedPanel = associatedPanel;
        this.fromLayer = fromLayer;
        this.toLayer = toLayer;
        this.fromIndex = fromIndex;
    }

    public synchronized void execute() {
        try {
            toLayer.setLayerData(ViewLayerType.META_MORPHEME_MOVED, fromLayer.getMetaMorphemeFromIndex(fromIndex));
            removedParse = fromLayer.metaMorphemeRemove(fromIndex);
        } catch (LayerNotExistsException | WordNotExistsException | LayerItemNotExistsException ignored) {
        }
        associatedPanel.save();
    }

    public synchronized void undo() {
        removedParse.addMetaMorphemeList(toLayer.getLayerData(ViewLayerType.META_MORPHEME_MOVED));
        toLayer.metaMorphemeClear();
        associatedPanel.save();
    }
}
