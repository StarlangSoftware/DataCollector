package DataCollector.WordNet;

import Dictionary.Pos;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Locale;

public class SemanticRelationExtractor extends SynSetProcessorFrame{
    private SynSet selectedSynSet = null;
    private Relation selectedRelation = null;
    private DefaultMutableTreeNode selectedTreeNode = null;
    private JTree candidateSynSetTree;
    private JList<SemanticRelationType> relationTypeList;
    static final protected String SEMANTIC_RELATION = "semantic relation";
    static final protected String DELETE = "delete";

    protected void addButtons(JToolBar toolBar){
        super.addButtons(toolBar);
        JButton merge = new DrawingButton(SemanticRelationExtractor.class, this, "semanticrelation", SEMANTIC_RELATION, "Semantic Relation");
        toolBar.add(merge);
        JButton delete = new DrawingButton(SemanticRelationExtractor.class, this, "delete", DELETE, "Delete");
        toolBar.add(delete);
        posComboBox.addActionListener(e -> {
            pos = (Pos) posComboBox.getSelectedItem();
            rebuildTree();
            createSemanticRelationTypeList();
        });
    }

    protected ArrayList<SynSet> extractSynSets(){
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : turkish.synSetList()){
            if (synSet.getPos() != null && synSet.getPos().equals(pos) && synSet.getLongDefinition() != null){
                result.add(synSet);
            }
        }
        result.sort(new SynSetDefinitionComparator());
        return result;
    }

    private DefaultTreeModel createTreeFromSynSets(ArrayList<SynSet> synSetList){
        DefaultMutableTreeNode literalsNode, relationsNode, relationNode, parent, rootNode = new DefaultMutableTreeNode("SynSet Tree");
        DefaultTreeModel result = new DefaultTreeModel(rootNode);
        for (SynSet synSet : synSetList){
            parent = new DefaultMutableTreeNode(synSet);
            rootNode.add(parent);
            literalsNode = new DefaultMutableTreeNode("Literals");
            addLiteralsAsChildren(literalsNode, synSet);
            parent.add(literalsNode);
            if (synSet.relationSize() > 0){
                relationsNode = new DefaultMutableTreeNode("Relations");
                parent.add(relationsNode);
                for (int i = 0; i < synSet.relationSize(); i++){
                    Relation relation = synSet.getRelation(i);
                    relationNode = new DefaultMutableTreeNode(relation);
                    relationsNode.add(relationNode);
                }
            }
        }
        return result;
    }

    protected void addSynSetsToTree(){
        ArrayList<SynSet> synSetList = extractSynSets();
        treeModel = createTreeFromSynSets(synSetList);
    }

    private void createSemanticRelationTypeList(){
        DefaultListModel<SemanticRelationType> listModel = new DefaultListModel<>();
        for (SemanticRelationType semanticRelationType : SemanticRelationType.values()){
            switch (semanticRelationType){
                case ANTONYM:
                case DERIVATION_RELATED:
                case ALSO_SEE:
                    listModel.addElement(semanticRelationType);
                    break;
                case HYPERNYM:
                case HYPONYM:
                    if (pos.equals(Pos.NOUN) || pos.equals(Pos.VERB)){
                        listModel.addElement(semanticRelationType);
                    }
                    break;
                case MEMBER_HOLONYM:
                case SUBSTANCE_HOLONYM:
                case PART_HOLONYM:
                case MEMBER_MERONYM:
                case SUBSTANCE_MERONYM:
                case PART_MERONYM:
                case DOMAIN_TOPIC:
                case INSTANCE_HYPERNYM:
                case INSTANCE_HYPONYM:
                case ATTRIBUTE:
                    if (pos.equals(Pos.NOUN)){
                        listModel.addElement(semanticRelationType);
                    }
                    break;
                case ENTAILMENT:
                case CAUSE:
                    if (pos.equals(Pos.VERB)){
                        listModel.addElement(semanticRelationType);
                    }
                    break;
                case SIMILAR_TO:
                    if (pos.equals(Pos.ADJECTIVE) || pos.equals(Pos.ADVERB)){
                        listModel.addElement(semanticRelationType);
                    }
                    break;
            }
        }
        relationTypeList.setModel(listModel);
    }

    private JTextArea createHelpArea(){
        JTextArea helpTextArea;
        String helpText = "Antonym (is opposite of)\n" +
                "Opposites, any ↔ any\n" +
                "leader ↔ follower\n" +
                "increase ↔ decrease\n" +
                "heavy ↔ light\n\n" +
                "Hypernym (is a kind of)\n" +
                "Concepts → Superordinates, N → N\n" +
                "Events → Superordinate events, V → V\n" +
                "breakfast → meal\n" +
                "fly → travel\n\n" +
                "Instance Hypernym (is an instance / name of a)\n" +
                "Name → Category, Proper Noun → N\n" +
                "Bush → president\n\n" +
                "Hyponym (is a superordinate to)\n" +
                "Concepts → Subtypes, N → N\n" +
                "bird → pigeon, penguin, seagull\n" +
                "Instance Hyponym (is instantiated by the name)\n" +
                "Category → Name, N → Proper Noun\n" +
                "painter → Picasso\n\n" +
                "Member Holonym (is a member of)\n" +
                "Members → Groups, N → N \n" +
                "copilot → crew\n\n" +
                "Substance Holonym (is a material/substance of)\n" +
                "Substance → Entity, N → N \n" +
                "milk → yoghurt\n\n" +
                "Part Holonym (is a part of)\n" +
                "Parts → Wholes, N → N \n" +
                "wrist → arm\n\n" +
                "Member Meronym (has as a member)\n" +
                "Groups → Members \n" +
                "faculty → professor\n\n" +
                "Substance Meronym (is made of)\n" +
                "Entity → Substance, N → N \n" +
                "chocolate → cocoa\n\n" +
                "Part Meronym (has as a part)\n" +
                "Wholes → Parts, N → N\n" +
                "forest → tree\n\n" +
                "Derivation Related (is derivationally related to)\n" +
                "any ↔ any\n" +
                "dark ↔ darkness ↔ darken\n\n" +
                "Domain Topic (is a member of the categorical domain)\n" +
                "any → N\n" +
                "phoneme → linguistics\n\n" +
                "Entailment (has as a necessary condition)\n" +
                "Events → Events they entail, V → V\n" +
                "dream → sleep\n\n" +
                "Cause (causes)\n" +
                "Cause → Effect, V → V\n" +
                "kill → die\n\n" +
                "Similar to (is similar to)\n" +
                "Adj ↔ Adj\t\n" +
                "long ↔ durable, endless, lifelong, longish, longstanding\n\n" +
                "Attribute (is an attribute/property of)\n" +
                "N → N\n" +
                "jealousy → person\n" +
                "loudness → noise\n\n" +
                "Also See (is semantically related to)\n" +
                "any ↔ any\n" +
                "long ↔ length, duration";
        helpTextArea = new JTextArea(helpText);
        helpTextArea.setEnabled(false);
        return helpTextArea;
    }

    public SemanticRelationExtractor(){
        WordNet english = new WordNet("english_wordnet_version_31.xml");
        tree.setCellRenderer(new SynSetTreeCellRenderer(oldTurkish00, oldTurkish01, turkish, english));
        ToolTipManager.sharedInstance().registerComponent(tree);
        JPanel leftPanel = new JPanel(new BorderLayout());
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                if (selectedTreeNode.getUserObject() instanceof SynSet){
                    selectedSynSet = (SynSet) selectedTreeNode.getUserObject();
                    definition.setText(selectedSynSet.getLongDefinition());
                } else {
                    if (selectedTreeNode.getUserObject() instanceof Relation){
                        selectedRelation = (Relation) selectedTreeNode.getUserObject();
                    }
                }
            }
        });
        JScrollPane leftPane = new JScrollPane(tree);
        leftPanel.add(leftPane, BorderLayout.CENTER);
        JPanel middlePanel = new JPanel(new BorderLayout());
        definition = new JTextField();
        definition.addActionListener(e -> {
            selectedSynSet.setDefinition(definition.getText());
            treeModel.reload(selectedTreeNode);
        });
        definition.addCaretListener(e -> {
            int mark = e.getMark();
            int dot = e.getDot();
            String selectedText = definition.getText().substring(Math.min(mark, dot), Math.max(mark, dot)).trim();
            ArrayList<SynSet> synSetList = turkish.getSynSetsWithLiteral(selectedText);
            DefaultTreeModel candidateTreeModel = createTreeFromSynSets(synSetList);
            candidateSynSetTree.setModel(candidateTreeModel);
        });
        middlePanel.add(definition, BorderLayout.NORTH);
        candidateSynSetTree = new JTree();
        candidateSynSetTree.setCellRenderer(new SynSetTreeCellRenderer(oldTurkish00, oldTurkish01, turkish, english));
        ToolTipManager.sharedInstance().registerComponent(candidateSynSetTree);
        JScrollPane rightPane1 = new JScrollPane(candidateSynSetTree);
        relationTypeList = new JList<>();
        createSemanticRelationTypeList();
        JScrollPane rightPane2 = new JScrollPane(relationTypeList);
        Dimension minimumSize1 = new Dimension(400, 200);
        rightPane1.setMinimumSize(minimumSize1);
        rightPane2.setMinimumSize(minimumSize1);
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightPane1, rightPane2);
        middlePanel.add(splitPane1, BorderLayout.CENTER);
        Dimension minimumSize2 = new Dimension(250, 50);
        leftPanel.setMinimumSize(minimumSize2);
        middlePanel.setMinimumSize(minimumSize2);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, middlePanel);
        JPanel rightPanel = new JPanel(new BorderLayout());
        JScrollPane helpPane = new JScrollPane(createHelpArea());
        rightPanel.add(helpPane);
        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane2, rightPanel);
        add(splitPane3, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
        setName("Semantic Relation Extractor");
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case SEMANTIC_RELATION:
                if (((DefaultMutableTreeNode) candidateSynSetTree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof SynSet){
                    SynSet relatedSynSet = (SynSet) ((DefaultMutableTreeNode) candidateSynSetTree.getSelectionPath().getLastPathComponent()).getUserObject();
                    SemanticRelationType relationType = relationTypeList.getSelectedValue();
                    SemanticRelation newRelation = new SemanticRelation(relatedSynSet.getId(), relationType);
                    selectedSynSet.addRelation(newRelation);
                    turkish.addReverseRelation(selectedSynSet, newRelation);
                    if (selectedTreeNode.getChildCount() == 1){
                        selectedTreeNode.add(new DefaultMutableTreeNode("Relations"));
                    }
                    ((DefaultMutableTreeNode) selectedTreeNode.getChildAt(1)).add(new DefaultMutableTreeNode(newRelation));
                    treeModel.reload(selectedTreeNode);
                }
                break;
            case DELETE:
                if (selectedSynSet.containsRelation(selectedRelation) && selectedTreeNode != null){
                    selectedSynSet.removeRelation(selectedRelation);
                    if (selectedRelation instanceof SemanticRelation){
                        turkish.removeReverseRelation(selectedSynSet, (SemanticRelation) selectedRelation);
                    }
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    selectedTreeNode.removeFromParent();
                    if (parent.getChildCount() == 0){
                        TreeNode grandParent = parent.getParent();
                        parent.removeFromParent();
                        treeModel.reload(grandParent);
                    } else {
                        treeModel.reload(parent);
                    }
                }
                break;
        }
    }


}
