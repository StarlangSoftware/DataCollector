package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.LayerInfo;

public class LayerClearAction extends TreeEditAction{
    private final LayerInfo info;
    private final ViewLayerType viewLayerType;
    private String previousLayerData;

    public LayerClearAction(TreeEditorPanel associatedPanel, LayerInfo info, ViewLayerType viewLayerType){
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
