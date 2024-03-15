package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeEditorPanel;

public class MorphologicalAnalysisAction extends TreeEditAction {

    private final LayerInfo info;
    private final String morphologicalAnalysis;
    private final String morphotactics;
    private String previousMorphologicalAnalysis;
    private String previousMorphotactics;

    public MorphologicalAnalysisAction(TreeEditorPanel associatedPanel, LayerInfo info, String morphologicalAnalysis, String morphotactics){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.morphologicalAnalysis = morphologicalAnalysis;
        this.morphotactics = morphotactics;
    }

    public void execute() {
        previousMorphologicalAnalysis = info.getLayerData(ViewLayerType.INFLECTIONAL_GROUP);
        previousMorphotactics = info.getLayerData(ViewLayerType.META_MORPHEME);
        info.setLayerData(ViewLayerType.INFLECTIONAL_GROUP, morphologicalAnalysis);
        info.setLayerData(ViewLayerType.META_MORPHEME, morphotactics);
        associatedPanel.save();
    }

    public synchronized void undo() {
        info.setLayerData(ViewLayerType.INFLECTIONAL_GROUP, previousMorphologicalAnalysis);
        info.setLayerData(ViewLayerType.META_MORPHEME, previousMorphotactics);
        associatedPanel.save();
    }
}
