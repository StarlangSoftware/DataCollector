package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.EditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerAction extends TreeEditAction{

    private LayerInfo info;
    private String layerData;
    private String previousLayerData;
    private ViewLayerType viewLayerType;

    public LayerAction(EditorPanel associatedPanel, LayerInfo info, String layerData, ViewLayerType viewLayerType){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.layerData = layerData;
        this.viewLayerType = viewLayerType;
    }

    public void execute() {
        previousLayerData = info.getLayerData(viewLayerType);
        info.setLayerData(viewLayerType, layerData);
        associatedPanel.save();
    }

    public void undo() {
        info.setLayerData(viewLayerType, previousLayerData);
        associatedPanel.save();
    }

}
