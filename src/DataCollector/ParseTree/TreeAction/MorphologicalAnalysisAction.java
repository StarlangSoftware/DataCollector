package DataCollector.ParseTree.TreeAction;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.EditorPanel;

public class MorphologicalAnalysisAction extends TreeEditAction {

    private LayerInfo info;
    private String morphologicalAnalysis;
    private String morphotactics;
    private String previousMorphologicalAnalysis;
    private String previousMorphotactics;

    public MorphologicalAnalysisAction(EditorPanel associatedPanel, LayerInfo info, String morphologicalAnalysis, String morphotactics){
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
