package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerClearAction extends TreeEditAction{
    private final LayerInfo info;
    private final ViewLayerType viewLayerType;
    private String previousLayerData;

    /**
     * Constructor for a generic layer clear action.
     * @param associatedPanel Panel associated with the action.
     * @param info Layer info which will be updated.
     * @param viewLayerType The updated layer.
     */
    public LayerClearAction(TreeEditorPanel associatedPanel, LayerInfo info, ViewLayerType viewLayerType){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.viewLayerType = viewLayerType;
    }

    /**
     * Stores previous layer info for undo operation. Clears different layers with calling appropriate clearing
     * functions.
     */
    public void execute() {
        previousLayerData = info.getLayerData(viewLayerType);
        switch (viewLayerType){
            case META_MORPHEME:
                info.metaMorphemeClear();
                break;
            case INFLECTIONAL_GROUP:
            case PART_OF_SPEECH:
                info.morphologicalAnalysisClear();
                break;
            case SEMANTICS:
                info.semanticClear();
                break;
        }
        associatedPanel.save();
    }

    /**
     * Undoes the action. Restores the old layer data.
     */
    public void undo() {
        info.setLayerData(viewLayerType, previousLayerData);
        associatedPanel.save();
    }
}
