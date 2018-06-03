package DataCollector.WordNet;

import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;

public class SynSetSplitterFrame extends SynSetProcessorFrame {
    private IdMapping mapping;
    static final protected String SPLIT = "split";
    private SynSet selectedRootSynSet = null;
    private DefaultMutableTreeNode selectedRootTreeNode = null;

    protected void addButtons(JToolBar toolBar){
        super.addButtons(toolBar);
        JButton split = new DrawingButton(SynSetSplitterFrame.class, this, "split", SPLIT, "Split");
        toolBar.add(split);
        definition = new JTextField();
        definition.addActionListener(e -> {
            selectedRootSynSet.setDefinition(definition.getText());
            treeModel.reload(selectedRootTreeNode);
        });
        toolBar.add(definition);
    }

    public SynSetSplitterFrame(){
        mapping = new IdMapping("Data/Wordnet/mapping.txt");
        JPanel leftPanel = new JPanel(new BorderLayout());
        JScrollPane leftPane = new JScrollPane(tree);
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof SynSet){
                selectedRootTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                if (selectedRootTreeNode.getUserObject() instanceof SynSet){
                    selectedRootSynSet = (SynSet) selectedRootTreeNode.getUserObject();
                    definition.setText(selectedRootSynSet.getLongDefinition());
                }
            }
        });
        leftPanel.add(leftPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
        setName("SynSet Splitter");
    }

    protected ArrayList<SynSet> extractSynSets(){
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : turkish.synSetList()){
            if (synSet.getDefinition() != null && synSet.getPos() != null && synSet.getPos().equals(pos) && synSet.getSynonym().literalSize() > 1){
                result.add(synSet);
            }
        }
        result.sort(new SynSetDefinitionComparator());
        return result;
    }

    private SynSet getOldSynSet(Literal literal){
        SynSet oldSynSet = oldTurkish00.getSynSetWithLiteral(literal.getName(), literal.getSense());
        if (oldSynSet == null){
            return oldTurkish01.getSynSetWithLiteral(literal.getName(), literal.getSense());
        } else {
            return oldSynSet;
        }
    }

    private void buildMergeTree(DefaultMutableTreeNode synSetTreeNode, SynSet synSet){
        DefaultMutableTreeNode parent, child;
        String oldParentId;
        Literal middleLiteral;
        SynSet oldSynSet;
        LinkedHashMap<String, DefaultMutableTreeNode> treeNodeMapping = new LinkedHashMap<>();
        if (mapping == null){
            return;
        }
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            Literal literal = synSet.getSynonym().getLiteral(i);
            child = new DefaultMutableTreeNode(literal);
            oldSynSet = getOldSynSet(literal);
            if (oldSynSet != null){
                oldParentId = mapping.singleMap(oldSynSet.getId());
                if (oldParentId != null  && !oldParentId.equals(synSet.getId())){
                    if (treeNodeMapping.containsKey(oldParentId)){
                        parent = treeNodeMapping.get(oldParentId);
                    } else {
                        middleLiteral = new Literal(oldParentId, 0, oldParentId);
                        parent = new DefaultMutableTreeNode(middleLiteral);
                        treeNodeMapping.put(oldParentId, parent);
                    }
                    parent.add(child);
                } else {
                    synSetTreeNode.add(child);
                }
            }
        }
        while (!treeNodeMapping.isEmpty()){
            DefaultMutableTreeNode nextNode;
            String nextId = treeNodeMapping.keySet().iterator().next();
            oldParentId = mapping.singleMap(nextId);
            if (oldParentId != null && !oldParentId.equals(synSet.getId())){
                middleLiteral = new Literal(oldParentId, 0, oldParentId);
                parent = new DefaultMutableTreeNode(middleLiteral);
                treeNodeMapping.put(oldParentId, parent);
                nextNode = treeNodeMapping.get(nextId);
                if (nextNode.getChildCount() == 1){
                    parent.add((DefaultMutableTreeNode) nextNode.getFirstChild());
                } else {
                    parent.add(nextNode);
                }
            } else {
                synSetTreeNode.add(treeNodeMapping.get(nextId));
            }
            treeNodeMapping.remove(nextId);
        }
    }

    protected void addSynSetsToTree(){
        ArrayList<SynSet> synSetList = extractSynSets();
        DefaultMutableTreeNode parent, rootNode = new DefaultMutableTreeNode("SynSet Tree");
        treeModel = new DefaultTreeModel(rootNode);
        for (SynSet synSet : synSetList){
            parent = new DefaultMutableTreeNode(synSet);
            rootNode.add(parent);
            buildMergeTree(parent, synSet);
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        DefaultMutableTreeNode selectedTreeNode;
        switch (e.getActionCommand()){
            case SAVE:
                mapping.save("Data/Wordnet/mapping.txt");
                break;
            case SPLIT:
                if (tree.getSelectionPaths() != null){
                    SynSet newSynSet = null;
                    for (int i = 0; i < tree.getSelectionPaths().length; i++){
                        selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[i].getLastPathComponent();
                        if (selectedTreeNode.getUserObject() instanceof Literal){
                            Literal literal = (Literal) selectedTreeNode.getUserObject();
                            SynSet selectedSynSet = getOldSynSet(literal);
                            if (selectedSynSet != null && turkish.getSynSetWithId(selectedSynSet.getId()) == null){
                                newSynSet = new SynSet(selectedSynSet.getId());
                                mapping.remove(selectedSynSet.getId());
                                newSynSet.setPos(pos);
                                break;
                            }
                        }
                    }
                    if (newSynSet != null){
                        HashSet<DefaultMutableTreeNode> parents = new HashSet<>();
                        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
                        String definition = "";
                        for (int i = 0; i < tree.getSelectionPaths().length; i++){
                            selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[i].getLastPathComponent();
                            if (selectedTreeNode.getUserObject() instanceof Literal){
                                Literal literal = (Literal) selectedTreeNode.getUserObject();
                                SynSet selectedSynSet = getOldSynSet(literal);
                                SynSet currentSynSet = turkish.getSynSetWithLiteral(literal.getName(), literal.getSense());
                                if (selectedSynSet != null){
                                    newSynSet.addLiteral(literal);
                                    if (selectedSynSet.getLongDefinition() != null){
                                        if (definition.length() != 0){
                                            definition = definition + "|" + selectedSynSet.getLongDefinition();
                                        } else {
                                            definition = selectedSynSet.getLongDefinition();
                                        }
                                    }
                                    currentSynSet.removeDefinition(selectedSynSet.getLongDefinition());
                                    currentSynSet.getSynonym().removeLiteral(literal);
                                    children.add(selectedTreeNode);
                                    parents.add((DefaultMutableTreeNode) selectedTreeNode.getParent());
                                }
                            }
                        }
                        newSynSet.setDefinition(definition);
                        newSynSet.removeSameDefinitions(new Locale("tr"));
                        turkish.addSynSet(newSynSet);
                        for (DefaultMutableTreeNode child : children){
                            treeModel.removeNodeFromParent(child);
                        }
                        for (DefaultMutableTreeNode parent : parents){
                            treeModel.reload(parent);
                            if (parent.getChildCount() == 0){
                                treeModel.removeNodeFromParent(parent);
                                treeModel.reload(parent.getParent());
                            }
                        }
                    }
                }
                break;
        }
    }

}
