package DataCollector.WordNet;

import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;

public class SynSetSplitterFrame extends SynSetProcessorFrame {
    static final protected String SPLIT = "split";
    private SynSet selectedRootSynSet = null;
    private JList relationList;
    private WordNet english;
    private DefaultMutableTreeNode selectedRootTreeNode = null;

    public class RelationObject {
        public Relation relation;

        public RelationObject(Relation relation) {
            this.relation = relation;
        }

        public String toString() {
            SynSet synSet;
            if (relation instanceof InterlingualRelation) {
                synSet = english.getSynSetWithId(relation.getName());
                return "ILR->(" + synSet.getSynonym().getLiteral(0).getName() + ") " + synSet.getDefinition();
            } else {
                if (relation instanceof SemanticRelation) {
                    synSet = turkish.getSynSetWithId(relation.getName());
                    return ((SemanticRelation) relation).getRelationType() + "->(" + synSet.getSynonym().getLiteral(0).getName() + ") " + synSet.getDefinition();
                }
            }
            return relation.toString();
        }
    }

    protected void addButtons(JToolBar toolBar){
        super.addButtons(toolBar);
        JButton split = new DrawingButton(SynSetSplitterFrame.class, this, "split", SPLIT, "Split");
        toolBar.add(split);
        JLabel definitionLabel = new JLabel("New Definition");
        toolBar.add(definitionLabel);
        definition = new JTextField();
        definition.addActionListener(e -> {
            selectedRootSynSet.setDefinition(definition.getText());
            treeModel.reload(selectedRootTreeNode);
        });
        toolBar.add(definition);
        JLabel searchLabel = new JLabel("Search");
        toolBar.add(searchLabel);
        JTextField search = new JTextField();
        search.addActionListener(e -> {
            DefaultMutableTreeNode root = ((DefaultMutableTreeNode) tree.getModel().getRoot());
            for (int i = 0; i < root.getChildCount(); i++){
                SynSet synSet = (SynSet) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject();
                if (synSet.getId().equals(search.getText())){
                    TreePath treePath = new TreePath(((DefaultMutableTreeNode) root.getChildAt(i)).getPath());
                    tree.setSelectionPath(treePath);
                    tree.scrollPathToVisible(treePath);
                    selectedRootSynSet = synSet;
                    break;
                }
            }
        });
        toolBar.add(search);
    }

    public SynSetSplitterFrame(){
        english = new WordNet("english_wordnet_version_31.xml");
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane treePane = new JScrollPane(tree);
        treePane.setMinimumSize(new Dimension(1000, 100));
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof SynSet){
                selectedRootTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                if (selectedRootTreeNode.getUserObject() instanceof SynSet){
                    selectedRootSynSet = (SynSet) selectedRootTreeNode.getUserObject();
                    buildRelationList();
                    definition.setText(selectedRootSynSet.getLongDefinition());
                }
            }
        });
        relationList = new JList();
        JScrollPane listPane = new JScrollPane(relationList);
        listPane.setMinimumSize(new Dimension(100, 100));
        JSplitPane synSetPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, listPane);
        panel.add(synSetPane);
        add(panel, BorderLayout.CENTER);
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
        DefaultMutableTreeNode child;
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            Literal literal = synSet.getSynonym().getLiteral(i);
            child = new DefaultMutableTreeNode(literal);
            synSetTreeNode.add(child);
        }
    }

    private void buildRelationList(){
        DefaultListModel<RelationObject> listModel = new DefaultListModel<>();
        for (int i = 0; i < selectedRootSynSet.relationSize(); i++){
            Relation relation = selectedRootSynSet.getRelation(i);
            listModel.addElement(new RelationObject(relation));
        }
        relationList.setModel(listModel);
        relationList.invalidate();
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
                                for (Object object : relationList.getSelectedValuesList()){
                                    Relation selectedRelation = ((RelationObject) object).relation;
                                    newSynSet.addRelation(selectedRelation);
                                    selectedRootSynSet.removeRelation(selectedRelation);
                                    ((DefaultListModel) relationList.getModel()).removeElement(object);
                                }
                                newSynSet.setPos(pos);
                                break;
                            }
                        }
                    }
                    if (newSynSet != null){
                        HashSet<DefaultMutableTreeNode> parents = new HashSet<>();
                        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
                        for (int i = 0; i < tree.getSelectionPaths().length; i++){
                            selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[i].getLastPathComponent();
                            if (selectedTreeNode.getUserObject() instanceof Literal){
                                Literal literal = (Literal) selectedTreeNode.getUserObject();
                                SynSet selectedSynSet = getOldSynSet(literal);
                                SynSet currentSynSet = turkish.getSynSetWithLiteral(literal.getName(), literal.getSense());
                                if (selectedSynSet != null){
                                    newSynSet.addLiteral(literal);
                                    currentSynSet.getSynonym().removeLiteral(literal);
                                    children.add(selectedTreeNode);
                                    parents.add((DefaultMutableTreeNode) selectedTreeNode.getParent());
                                }
                            }
                        }
                        newSynSet.setDefinition(definition.getText());
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
