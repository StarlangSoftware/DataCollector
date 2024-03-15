package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerAction extends TreeEditAction{

    private final LayerInfo info;
    private final String layerData;
    private String previousLayerData;
    private final ViewLayerType viewLayerType;

    public LayerAction(TreeEditorPanel associatedPanel, LayerInfo info, String layerData, ViewLayerType viewLayerType){
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
