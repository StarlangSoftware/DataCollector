package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerAction extends TreeEditAction{

    private final LayerInfo info;
    private final String layerData;
    private String previousLayerData;
    private final ViewLayerType viewLayerType;

    /**
     * Constructor for a generic layer update action.
     * @param associatedPanel Panel associated with the action.
     * @param info Layer info which will be updated.
     * @param layerData New data for the updated layer.
     * @param viewLayerType The updated layer.
     */
    public LayerAction(TreeEditorPanel associatedPanel, LayerInfo info, String layerData, ViewLayerType viewLayerType){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.layerData = layerData;
        this.viewLayerType = viewLayerType;
    }

    /**
     * Stores previous layer info for undo operation. Sets the new layer info.
     */
    public void execute() {
        previousLayerData = info.getLayerData(viewLayerType);
        info.setLayerData(viewLayerType, layerData);
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
