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

    /**
     * Constructor for moving a metamorpheme group from a word to another empty word.
     * @param associatedPanel Panel associated with the action.
     * @param fromLayer Layer from which the morphemes are moved.
     * @param toLayer Layer to which the morphemes are moved.
     * @param fromIndex The starting position of the morpheme group in the morpheme list.
     */
    public MetaMorphemeMoveAction(TreeEditorPanel associatedPanel, LayerInfo fromLayer, LayerInfo toLayer, int fromIndex){
        this.associatedPanel = associatedPanel;
        this.fromLayer = fromLayer;
        this.toLayer = toLayer;
        this.fromIndex = fromIndex;
    }

    /**
     * Replaces the layer data of the toLayer with the moved morphemes. Saves the original parse for undo operation.
     */
    public synchronized void execute() {
        try {
            toLayer.setLayerData(ViewLayerType.META_MORPHEME_MOVED, fromLayer.getMetaMorphemeFromIndex(fromIndex));
            removedParse = fromLayer.metaMorphemeRemove(fromIndex);
        } catch (LayerNotExistsException | WordNotExistsException | LayerItemNotExistsException ignored) {
        }
        associatedPanel.save();
    }

    /**
     * Undoes the operation. Adds the moved morpheme group to the original saved parse. Clears the toLayer so that no
     * morphemes has been moved there.
     */
    public synchronized void undo() {
        removedParse.addMetaMorphemeList(toLayer.getLayerData(ViewLayerType.META_MORPHEME_MOVED));
        toLayer.metaMorphemeClear();
        associatedPanel.save();
    }
}
