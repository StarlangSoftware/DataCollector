package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.EditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerClearAction extends TreeEditAction{
    private LayerInfo info;
    private ViewLayerType viewLayerType;
    private String previousLayerData;

    public LayerClearAction(EditorPanel associatedPanel, LayerInfo info, ViewLayerType viewLayerType){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.viewLayerType = viewLayerType;
    }

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

    public void undo() {
        info.setLayerData(viewLayerType, previousLayerData);
        associatedPanel.save();
    }
}
