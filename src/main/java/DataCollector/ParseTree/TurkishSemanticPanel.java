package DataCollector.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.LayerAction;
import DataCollector.ParseTree.TreeAction.LayerClearAction;
import WordNet.*;
import DataCollector.WordNet.ExampleTreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class TurkishSemanticPanel extends LeafEditorPanel {

    private JTree tree;
    private DefaultTreeModel treeModel;
    private WordNet wordNet;
    private FsmMorphologicalAnalyzer fsm;
    private ArrayList<SynSet>[] meanings;
    private ArrayList<SynSet> idioms, idioms1, idioms2;

    public TurkishSemanticPanel(String path, String fileName, WordNet wordNet, FsmMorphologicalAnalyzer fsm, boolean defaultFillEnabled) {
        super(path, fileName, ViewLayerType.SEMANTICS, defaultFillEnabled);
        this.wordNet = wordNet;
        this.fsm = fsm;
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Anlamlar");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                if (node.getLevel() == 0){
                    LayerClearAction action = new LayerClearAction(((TurkishSemanticPanel) tree.getParent().getParent().getParent()), previousNode.getLayerInfo(), ViewLayerType.SEMANTICS);
                    setAction(action);
                    tree.setVisible(false);
                } else {
                    ArrayList<String> selectedMeanings = getSelectedMeanings(node);
                    if (selectedMeanings.size() > 0) {
                        String semantics = selectedMeanings.get(0);
                        for (int i = 1; i < selectedMeanings.size(); i++) {
                            semantics = semantics + "$" + selectedMeanings.get(i);
                        }
                        LayerAction action = new LayerAction(((TurkishSemanticPanel) tree.getParent().getParent().getParent()), previousNode.getLayerInfo(), semantics, ViewLayerType.SEMANTICS);
                        setAction(action);
                        tree.setVisible(false);
                    }
                }
            }
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private ArrayList<String> getSelectedMeanings(DefaultMutableTreeNode node){
        ArrayList<String> selectedMeanings = new ArrayList<>();
        switch (meanings.length){
            case 1:
                if (node.getLevel() == 1){
                    if (idioms1.size() > 0 && node.getParent().getIndex(node) < idioms1.size()){
                        selectedMeanings.add(idioms1.get(node.getParent().getIndex(node)).getId());
                    } else {
                        if (idioms2.size() > 0 && node.getParent().getIndex(node) < idioms1.size() + idioms2.size()){
                            selectedMeanings.add(idioms2.get(node.getParent().getIndex(node) - idioms1.size()).getId());
                        } else {
                            if (meanings[0].size() > 0){
                                selectedMeanings.add(meanings[0].get(node.getParent().getIndex(node) - idioms1.size() - idioms2.size()).getId());
                            }
                        }
                    }
                }
                break;
            case 2:
                if (node.getLevel() == 1 && node.getParent().getIndex(node) < idioms.size()){
                    selectedMeanings.add(idioms.get(node.getParent().getIndex(node)).getId());
                } else {
                    if (meanings[0].size() > 0 && meanings[1].size() > 0 && node.getLevel() == 2){
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                        selectedMeanings.add(meanings[0].get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                        selectedMeanings.add(meanings[1].get(node.getParent().getIndex(node)).getId());
                    }
                }
                break;
            case 3:
                if (node.getLevel() == 1 && node.getParent().getIndex(node) < idioms.size()) {
                    selectedMeanings.add(idioms.get(node.getParent().getIndex(node)).getId());
                } else {
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                    if (node.getLevel() == 2){
                        if (parentNode.getParent().getIndex(parentNode) < idioms.size() + idioms1.size()){
                            selectedMeanings.add(idioms1.get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                            selectedMeanings.add(idioms1.get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                            selectedMeanings.add(meanings[2].get(node.getParent().getIndex(node)).getId());
                        } else {
                            if (node.getParent().getIndex(node) < idioms2.size()){
                                selectedMeanings.add(meanings[0].get((parentNode.getParent().getIndex(parentNode) - idioms.size() - idioms1.size())).getId());
                                selectedMeanings.add(idioms2.get(node.getParent().getIndex(node)).getId());
                                selectedMeanings.add(idioms2.get(node.getParent().getIndex(node)).getId());
                            }
                        }
                    } else {
                        if (meanings[0].size() > 0 && meanings[1].size() > 0 && meanings[2].size() > 0 && node.getLevel() == 3) {
                            DefaultMutableTreeNode grandParentNode = (DefaultMutableTreeNode) parentNode.getParent();
                            selectedMeanings.add(meanings[0].get(grandParentNode.getParent().getIndex(grandParentNode) - idioms.size() - idioms1.size()).getId());
                            selectedMeanings.add(meanings[1].get(parentNode.getParent().getIndex(parentNode) - idioms2.size()).getId());
                            selectedMeanings.add(meanings[2].get(node.getParent().getIndex(node)).getId());
                        }
                    }
                }
                break;
        }
        return selectedMeanings;
    }

    public void populateLeaf(ParseNodeDrawable node){
        DefaultMutableTreeNode selectedNode = null;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        LayerInfo info = node.getLayerInfo();
        if (info.getLayerData(ViewLayerType.INFLECTIONAL_GROUP) != null){
            try{
                meanings = new ArrayList[info.getNumberOfWords()];
                for (int i = 0; i < info.getNumberOfWords(); i++){
                    meanings[i] = wordNet.constructSynSets(info.getMorphologicalParseAt(i).getWord().getName(), info.getMorphologicalParseAt(i), info.getMetamorphicParseAt(i), fsm);
                }
                switch (info.getNumberOfWords()){
                    case 1:
                        ParseNodeDrawable previous = currentTree.previousLeafNode(node);
                        if (previous != null && previous.getLayerInfo().getNumberOfWords() == 1){
                            idioms1 = wordNet.constructIdiomSynSets(previous.getLayerInfo().getMorphologicalParseAt(0), info.getMorphologicalParseAt(0), previous.getLayerInfo().getMetamorphicParseAt(0), info.getMetamorphicParseAt(0), fsm);
                            for (SynSet idiom: idioms1){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        } else {
                            idioms1 = new ArrayList<>();
                        }
                        ParseNodeDrawable next = currentTree.nextLeafNode(node);
                        if (next != null && next.getLayerInfo().getNumberOfWords() == 1){
                            idioms2 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), next.getLayerInfo().getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), next.getLayerInfo().getMetamorphicParseAt(0), fsm);
                            for (SynSet idiom: idioms2){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        } else {
                            idioms2 = new ArrayList<>();
                        }
                        if (idioms1.size() != 0 || idioms2.size() != 0 || meanings[0].size() != 0){
                            for (SynSet meaning: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        }
                        break;
                    case 2:
                        idioms = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), fsm);
                        for (SynSet idiom: idioms){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                selectedNode = childNode;
                            }
                        }
                        if (meanings[0].size() != 0 && meanings[1].size() != 0){
                            for (SynSet meaning0: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning0);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                for (SynSet meaning1: meanings[1]){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning1);
                                    childNode.add(grandChildNode);
                                    if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + meaning1.getId())){
                                        selectedNode = grandChildNode;
                                    }
                                }
                            }
                        }
                        break;
                    case 3:
                        idioms = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMorphologicalParseAt(2), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), info.getMetamorphicParseAt(2), fsm);
                        for (SynSet idiom: idioms){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                selectedNode = childNode;
                            }
                        }
                        idioms1 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), fsm);
                        for (SynSet idiom: idioms1){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            for (SynSet meaning2: meanings[2]){
                                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning2);
                                childNode.add(grandChildNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId() + "$" + idiom.getId() + "$" + meaning2.getId())){
                                    selectedNode = grandChildNode;
                                }
                            }
                        }
                        idioms2 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(1), info.getMorphologicalParseAt(2), info.getMetamorphicParseAt(1), info.getMetamorphicParseAt(2), fsm);
                        if (meanings[0].size() != 0 && meanings[1].size() != 0 && meanings[2].size() != 0){
                            for (SynSet meaning0: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning0);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                for (SynSet idiom: idioms2){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(idiom);
                                    childNode.add(grandChildNode);
                                    if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + idiom.getId() + "$" + idiom.getId())){
                                        selectedNode = grandChildNode;
                                    }
                                }
                                for (SynSet meaning1: meanings[1]){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning1);
                                    childNode.add(grandChildNode);
                                    for (SynSet meaning2: meanings[2]){
                                        DefaultMutableTreeNode grandGrandChildNode = new DefaultMutableTreeNode(meaning2);
                                        grandChildNode.add(grandGrandChildNode);
                                        if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + meaning1.getId() + "$" + meaning2.getId())){
                                            selectedNode = grandGrandChildNode;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                }
            } catch (WordNotExistsException | LayerNotExistsException e) {
                e.printStackTrace();
            }
        }
        treeModel.reload();
        if (selectedNode != null){
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 30, 250, 90);
        this.repaint();
        isEditing = true;
        tree.setCellRenderer(new ExampleTreeCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
    }

    protected boolean defaultFill(ParseNodeDrawable node){
        if (wordNet == null || fsm == null){
            return false;
        }
        if (node.getLayerData(ViewLayerType.SEMANTICS) != null){
            return false;
        }
        if (node.getLayerData(ViewLayerType.TURKISH_WORD) != null && node.getLayerData(ViewLayerType.META_MORPHEME) != null && node.getLayerData(ViewLayerType.INFLECTIONAL_GROUP) != null){
            LayerInfo info = node.getLayerInfo();
            try {
                if (info.getNumberOfWords() == 1){
                    ArrayList<SynSet> synSetList = wordNet.constructSynSets(info.getMorphologicalParseAt(0).getWord().getName(), info.getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), fsm);
                    if (synSetList.size() == 1){
                        node.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, synSetList.get(0).getId());
                        return true;
                    }
                } else {
                    if (info.getNumberOfWords() == 2){
                        ArrayList<SynSet> synSetList1 = wordNet.constructSynSets(info.getMorphologicalParseAt(0).getWord().getName(), info.getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), fsm);
                        ArrayList<SynSet> synSetList2 = wordNet.constructSynSets(info.getMorphologicalParseAt(1).getWord().getName(), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(1), fsm);
                        if (synSetList1.size() == 1 && synSetList2.size() == 1){
                            node.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, synSetList1.get(0).getId() + "$" + synSetList2.get(0).getId());
                            return true;
                        }
                    }
                }
            } catch (LayerNotExistsException | WordNotExistsException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
