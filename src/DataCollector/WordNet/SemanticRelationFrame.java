package DataCollector.WordNet;

import Dictionary.Pos;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.*;

public class SemanticRelationFrame extends JFrame implements ActionListener {
    private JTree tree;
    private JList<SynSet> wordList, detailedList, matchedSynSetList;
    private JTextField leftSearch, rightSearch;
    private WordNet english, turkish;
    private Pos pos;
    private SemanticRelationType semanticRelationType;
    private HashMap<SynSet, DefaultMutableTreeNode> nodeList;
    private DefaultTreeModel treeModel;
    private DefaultListModel<SynSet> matchedSynSetListModel;
    private JButton interlingua, semanticRelation, clear;
    private InterlingualMatchedSynSets selectedInterlingual = null;
    private DefaultMutableTreeNode selectedTreeNode = null;
    private SynSet selectedSynSet = null;
    private JComboBox posComboBox, semanticRelationComboBox;

    static final protected String INTERLINGUAL = "interlingual";
    static final protected String SEMANTIC_RELATION = "semanticrelation";
    static final protected String CLEAR = "clear";
    static final protected String SAVE = "save";

    protected void addButtons(JToolBar toolBar){
        JButton save = new DrawingButton(SemanticRelationFrame.class, this, "save", SAVE, "Save");
        toolBar.add(save);
        interlingua = new DrawingButton(SemanticRelationFrame.class, this, "interlingual", INTERLINGUAL, "Add Interlingual Relation");
        interlingua.setVisible(true);
        interlingua.setEnabled(false);
        toolBar.add(interlingua);
        semanticRelation = new DrawingButton(SemanticRelationFrame.class, this, "semanticrelation", SEMANTIC_RELATION, "Add Semantic Relation");
        semanticRelation.setVisible(true);
        semanticRelation.setEnabled(false);
        toolBar.add(semanticRelation);
        clear = new DrawingButton(SemanticRelationFrame.class, this, "clear", CLEAR, "Clear Relation");
        clear.setVisible(true);
        clear.setEnabled(false);
        toolBar.add(clear);
        posComboBox = new JComboBox<>(new Pos[]{Pos.NOUN, Pos.VERB});
        posComboBox.setMaximumSize(new Dimension(150, 20));
        posComboBox.setSelectedIndex(0);
        posComboBox.addActionListener(e -> {
            pos = (Pos) posComboBox.getSelectedItem();
            rebuildTree();
        });
        toolBar.add(posComboBox);
        semanticRelationComboBox = new JComboBox<>(new SemanticRelationType[]{SemanticRelationType.HYPONYM, SemanticRelationType.INSTANCE_HYPONYM, SemanticRelationType.MEMBER_MERONYM, SemanticRelationType.PART_MERONYM, SemanticRelationType.SUBSTANCE_MERONYM});
        semanticRelationComboBox.setMaximumSize(new Dimension(150, 20));
        semanticRelationComboBox.setSelectedIndex(0);
        semanticRelationComboBox.addActionListener(e -> {
            semanticRelationType = (SemanticRelationType) semanticRelationComboBox.getSelectedItem();
            rebuildTree();
        });
        toolBar.add(semanticRelationComboBox);
    }

    private void setButtonsEnabled(){
        if (selectedInterlingual == null){
            clear.setEnabled(false);
            interlingua.setEnabled(false);
            semanticRelation.setEnabled(false);
        } else {
            if (selectedInterlingual.getSecond() != null && selectedInterlingual.getSecond().size() > 0){
                clear.setEnabled(true);
                interlingua.setEnabled(false);
                if (selectedSynSet != null){
                    semanticRelation.setEnabled(true);
                } else {
                    semanticRelation.setEnabled(false);
                }
            } else {
                clear.setEnabled(false);
                semanticRelation.setEnabled(false);
                if (selectedSynSet != null){
                    interlingua.setEnabled(true);
                } else {
                    interlingua.setEnabled(false);
                }
            }
        }
    }

    public SemanticRelationFrame(){
        this.pos = Pos.NOUN;
        this.semanticRelationType = SemanticRelationType.HYPONYM;
        english = new WordNet("Data/Wordnet/english_wordnet_version_31.xml", "Data/Wordnet/english_exception.xml", new Locale("en"));
        turkish = new WordNet();
        JToolBar toolBar = new JToolBar("ToolBox");
        addButtons(toolBar);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftSearch = new JTextField();
        leftSearch.addActionListener(e -> selectTree(leftSearch.getText()));
        leftPanel.add(leftSearch, BorderLayout.NORTH);
        buildTree();
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                if (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof InterlingualMatchedSynSets){
                    selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                    selectedInterlingual = (InterlingualMatchedSynSets) selectedTreeNode.getUserObject();
                    buildListModelForMatchedSynSets();
                } else {
                    selectedInterlingual = null;
                }
                setButtonsEnabled();
            }
        });
        JScrollPane treePane = new JScrollPane(tree);
        matchedSynSetList = new JList<>();
        JScrollPane matchedSynSetPane = new JScrollPane(matchedSynSetList);
        treePane.setMinimumSize(new Dimension(400, 400));
        matchedSynSetPane.setMinimumSize(new Dimension(400, 100));
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePane, matchedSynSetPane);
        leftPanel.add(leftSplitPane, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightSearch = new JTextField();
        rightSearch.addActionListener(e -> {
            buildListModelForMatchInLongDefinition(rightSearch.getText());
            buildListModelForMatchInLiterals(rightSearch.getText());
            selectedSynSet = null;
            setButtonsEnabled();
        });
        rightPanel.add(rightSearch, BorderLayout.NORTH);
        wordList = buildList(false);
        wordList.addListSelectionListener(e -> {
            if (wordList.getSelectedValue() != null){
                selectedSynSet = wordList.getSelectedValue();
            } else {
                selectedSynSet = null;
            }
            setButtonsEnabled();
        });
        JScrollPane wordListPane = new JScrollPane(wordList);
        detailedList = buildList(true);
        detailedList.addListSelectionListener(e -> {
            if (detailedList.getSelectedValue() != null){
                selectedSynSet = detailedList.getSelectedValue();
            } else {
                selectedSynSet = null;
            }
            setButtonsEnabled();
        });
        JScrollPane detailedListPane = new JScrollPane(detailedList);
        Dimension minimumSize1 = new Dimension(400, 200);
        wordListPane.setMinimumSize(minimumSize1);
        detailedListPane.setMinimumSize(minimumSize1);
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wordListPane, detailedListPane);
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        Dimension minimumSize2 = new Dimension(400, 50);
        leftPanel.setMinimumSize(minimumSize2);
        rightPanel.setMinimumSize(minimumSize2);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        add(splitPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setName("Semantic Relation Editor");
    }

    private DefaultMutableTreeNode createNode(SynSet synSet, boolean isFirst){
        DefaultMutableTreeNode node;
        if (!nodeList.containsKey(synSet)){
            node = new DefaultMutableTreeNode(new InterlingualMatchedSynSets(synSet, isFirst));
            nodeList.put(synSet, node);
        } else {
            node = nodeList.get(synSet);
        }
        return node;
    }

    private void setMatches(){
        DefaultMutableTreeNode parent;
        for (SynSet turkishSynSet : turkish.synSetList()){
            if (turkishSynSet.getPos() != null && turkishSynSet.getPos().equals(pos)){
                for (int j = 0; j < turkishSynSet.relationSize(); j++){
                    if (turkishSynSet.getRelation(j) instanceof InterlingualRelation){
                        InterlingualRelation relation = (InterlingualRelation) turkishSynSet.getRelation(j);
                        SynSet englishSynSet = english.getSynSetWithId(relation.getName());
                        if (englishSynSet != null && nodeList.containsKey(englishSynSet)){
                            parent = nodeList.get(englishSynSet);
                            ((InterlingualMatchedSynSets) parent.getUserObject()).addSecond(turkishSynSet);
                            nodeList.put(turkishSynSet, parent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SynSet englishSynSet, turkishSynSet, parentSynSet;
        switch (e.getActionCommand()){
            case SAVE:
                turkish.saveAsXml("Data/Wordnet/turkish_wordnet.xml");
                break;
            case INTERLINGUAL:
                selectedInterlingual.addSecond(selectedSynSet);
                nodeList.put(selectedSynSet, selectedTreeNode);
                selectedSynSet.addRelation(new InterlingualRelation(selectedInterlingual.getFirst().getId(), "SYNONYM"));
                break;
            case SEMANTIC_RELATION:
                DefaultMutableTreeNode newNode = createNode(selectedSynSet, false);
                selectedTreeNode.add(newNode);
                turkishSynSet = selectedInterlingual.getSecond().get(0);
                SemanticRelation newRelation = new SemanticRelation(selectedSynSet.getId(), semanticRelationType);
                turkishSynSet.addRelation(newRelation);
                turkish.addReverseRelation(turkishSynSet, newRelation);
                treeModel.reload(selectedTreeNode);
                break;
            case CLEAR:
                englishSynSet = selectedInterlingual.getFirst();
                if (englishSynSet == null){
                    turkishSynSet = selectedInterlingual.getSecond().get(0);
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    parentSynSet = ((InterlingualMatchedSynSets) parent.getUserObject()).getSecond().get(0);
                    parent.remove(selectedTreeNode);
                    treeModel.reload(parent);
                    turkishSynSet.removeRelation(parentSynSet.getId());
                    parentSynSet.removeRelation(turkishSynSet.getId());
                    selectedInterlingual = null;
                } else {
                    turkishSynSet = matchedSynSetList.getSelectedValue();
                    turkishSynSet.removeRelation(englishSynSet.getId());
                    selectedInterlingual.remove(turkishSynSet);
                    matchedSynSetListModel.remove(matchedSynSetList.getSelectedIndex());
                }
                setButtonsEnabled();
                nodeList.remove(turkishSynSet);
                break;
        }
    }

    private void buildListModelForMatchedSynSets(){
        matchedSynSetListModel = new DefaultListModel<>();
        if (selectedInterlingual.getSecond() != null){
            for (SynSet turkishSynSet : selectedInterlingual.getSecond()){
                matchedSynSetListModel.addElement(turkishSynSet);
            }
        }
        matchedSynSetList.setModel(matchedSynSetListModel);
    }

    private void buildListModelForMatchInLiterals(String searchKey){
        DefaultListModel<SynSet> listModel = new DefaultListModel<>();
        for (SynSet turkishSynSet : turkish.synSetList()){
            if (turkishSynSet.getPos() != null && turkishSynSet.getPos().equals(pos) && !nodeList.containsKey(turkishSynSet) && turkishSynSet.getSynonym().containsLiteral(searchKey)){
                listModel.addElement(turkishSynSet);
            }
        }
        wordList.setModel(listModel);
    }

    private void buildListModelForMatchInLongDefinition(String searchKey){
        DefaultListModel<SynSet> listModel = new DefaultListModel<>();
        for (SynSet turkishSynSet : turkish.synSetList()){
            if (turkishSynSet.getPos() != null && turkishSynSet.getPos().equals(pos) && !nodeList.containsKey(turkishSynSet) && (turkishSynSet.getLongDefinition() != null && turkishSynSet.getLongDefinition().contains(searchKey))){
                listModel.addElement(turkishSynSet);
            }
        }
        detailedList.setModel(listModel);
    }

    private JList<SynSet> buildList(boolean tooltip){
        JList<SynSet> result;
        DefaultListModel<SynSet> listModel = new DefaultListModel<>();
        result = new JList<>(listModel);
        for (SynSet turkishSynSet : turkish.synSetList()){
            if (turkishSynSet.getPos() != null && turkishSynSet.getPos().equals(pos) && !nodeList.containsKey(turkishSynSet)){
                listModel.addElement(turkishSynSet);
            }
        }
        if (tooltip){
            result.setCellRenderer(new SynSetListCellRenderer());
            ToolTipManager.sharedInstance().registerComponent(result);
        }
        return result;
    }

    private void selectTree(String searchKey){
        if (tree.getSelectionPaths() != null){
            for (TreePath treePath : tree.getSelectionPaths()){
                tree.collapsePath(treePath);
            }
        }
        tree.clearSelection();
        for (Entry<SynSet, DefaultMutableTreeNode> entry : nodeList.entrySet()){
            if (entry.getKey().getSynonym().containsLiteral(searchKey)){
                TreePath treePath = new TreePath(treeModel.getPathToRoot(entry.getValue()));
                tree.addSelectionPath(treePath);
                tree.scrollPathToVisible(treePath);
            }
        }
    }

    private void constructTree(){
        nodeList = new HashMap<>();
        DefaultMutableTreeNode parent, child, rootNode = new DefaultMutableTreeNode("Tree");
        treeModel = new DefaultTreeModel(rootNode);
        for (SynSet synSet : english.synSetList()){
            if (synSet.getPos().equals(pos)){
                parent = createNode(synSet, true);
                for (int j = 0; j < synSet.relationSize(); j++){
                    SemanticRelation relation = (SemanticRelation) synSet.getRelation(j);
                    if (relation.getRelationType().equals(semanticRelationType)){
                        SynSet childSynSet = english.getSynSetWithId(relation.getName());
                        if (childSynSet != null){
                            child = createNode(childSynSet, true);
                            parent.add(child);
                        }
                    }
                }
            }
        }
        setMatches();
        for (SynSet synSet : turkish.synSetList()){
            if (synSet.getPos() != null && synSet.getPos().equals(pos) && nodeList.containsKey(synSet)){
                parent = nodeList.get(synSet);
                for (int j = 0; j < synSet.relationSize(); j++){
                    if ((synSet.getRelation(j) instanceof SemanticRelation)){
                        SemanticRelation relation = (SemanticRelation) synSet.getRelation(j);
                        if (relation.getRelationType().equals(semanticRelationType)){
                            SynSet childSynSet = turkish.getSynSetWithId(relation.getName());
                            if (childSynSet != null){
                                try{
                                    child = createNode(childSynSet, false);
                                    parent.add(child);
                                }catch (IllegalArgumentException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        for (SynSet synSet : nodeList.keySet()){
            if (nodeList.get(synSet).getParent() == null){
                rootNode.add(nodeList.get(synSet));
            }
        }
        for (int i = 0; i < rootNode.getChildCount(); i++){
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (childNode.getChildCount() == 0){
                treeModel.removeNodeFromParent(childNode);
                i--;
            }
        }
    }

    private void rebuildTree(){
        constructTree();
        tree.setModel(treeModel);
    }

    private void buildTree(){
        constructTree();
        tree = new JTree(treeModel);
        tree.setCellRenderer(new MatchedSynSetTreeCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(tree);
    }
}
