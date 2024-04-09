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

    /**
     * Constructor for setting a new morphological analysis for a word.
     * @param associatedPanel Panel associated with the action.
     * @param info Layer in which morphological analysis is changed.
     * @param morphologicalAnalysis New morphological analysis.
     * @param morphotactics New morphotactics.
     */
    public MorphologicalAnalysisAction(TreeEditorPanel associatedPanel, LayerInfo info, String morphologicalAnalysis, String morphotactics){
        this.associatedPanel = associatedPanel;
        this.info = info;
        this.morphologicalAnalysis = morphologicalAnalysis;
        this.morphotactics = morphotactics;
    }

    /**
     * Saves the original morphological analysis and morphotactics for undo operation. Sets the new morphological
     * analysis and morphotactics.
     */
    public void execute() {
        previousMorphologicalAnalysis = info.getLayerData(ViewLayerType.INFLECTIONAL_GROUP);
        previousMorphotactics = info.getLayerData(ViewLayerType.META_MORPHEME);
        info.setLayerData(ViewLayerType.INFLECTIONAL_GROUP, morphologicalAnalysis);
        info.setLayerData(ViewLayerType.META_MORPHEME, morphotactics);
        associatedPanel.save();
    }

    /**
     * Undoes the operation. Restores the original morphological analysis and morphotactics.
     */
    public synchronized void undo() {
        info.setLayerData(ViewLayerType.INFLECTIONAL_GROUP, previousMorphologicalAnalysis);
        info.setLayerData(ViewLayerType.META_MORPHEME, previousMorphotactics);
        associatedPanel.save();
    }
}
