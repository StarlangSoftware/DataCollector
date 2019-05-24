package DataCollector.WordNet;

import Dictionary.Pos;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WordNetEditorFrame extends JFrame implements ActionListener {
    private PartOfSpeechTree noun;
    private PartOfSpeechTree selectedPartOfSpeechTree;
    private JTextField leftSearch, id, literal, sense, definition;
    private WordNet turkish, domainWordNet;
    private DefaultMutableTreeNode selectedTreeNode = null;
    private SynSet selectedSynSet = null;
    private JComboBox alternatives;
    private JCheckBox showMoved;
    private boolean completed = false;

    private static final String SAVE = "save";
    private static final String EDIT = "edit";
    private static final String ADD_NEW = "add new";
    private static final String INSERT_FROM_WORDNET = "insert from wordnet";
    private static final String INSERT_CHILD = "insert child";
    private static final String REMOVE_FROM_PARENT = "remove from parent";
    private static final String DELETE = "delete";
    private static final String REPLACE = "replace with new synset";
    private static final String MERGE = "merge two synsets";

    //private final String domainWordNetFileName = "estate_wordnet.xml";
    //private final String prefix = "EST01-";
    private final String domainWordNetFileName = "tourism_wordnet.xml";
    private final String prefix = "TOU01-";
    private int finalId;

    public class PartOfSpeechTree{
        private JTree tree;
        private HashMap<SynSet, DefaultMutableTreeNode> nodeList;
        private DefaultTreeModel treeModel;

        public JTree getTree() {
            return tree;
        }

        public DefaultTreeModel getTreeModel() {
            return treeModel;
        }

        PartOfSpeechTree(JTree tree, HashMap<SynSet, DefaultMutableTreeNode> nodeList, DefaultTreeModel treeModel) {
            this.tree = tree;
            this.nodeList = nodeList;
            this.treeModel = treeModel;
        }
    }

    public class SynSetObject{
        private SynSet synSet;

        SynSetObject(SynSet synSet){
            this.synSet = synSet;
        }

        public String toString(){
            String literal = synSet.getSynonym().getLiteral(0).getName();
            for (int i = 1; i < synSet.getSynonym().literalSize(); i++){
                literal += "::" + synSet.getSynonym().getLiteral(i).getName();
            }
            return literal + " (" + synSet.getDefinition() + ")";
        }

        public SynSet getSynSet(){
            return synSet;
        }
    }

    private int getFinalId(){
        int max = 0;
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getId().startsWith(prefix)){
                int id = Integer.parseInt(synSet.getId().substring(prefix.length()));
                if (id > max){
                    max = id;
                }
            }
        }
        return max;
    }

    private void addButtons(JToolBar toolBar) {
        JButton save = new DrawingButton(WordNetEditorFrame.class, this, "save", SAVE, "Save");
        toolBar.add(save);
        JButton edit = new DrawingButton(WordNetEditorFrame.class, this, "edit", EDIT, "Edit");
        toolBar.add(edit);
        toolBar.addSeparator();
        JButton addNew = new DrawingButton(WordNetEditorFrame.class, this, "addparent", ADD_NEW, "Add New SynSet");
        toolBar.add(addNew);
        JButton insertFromWordNet = new DrawingButton(WordNetEditorFrame.class, this, "merge", INSERT_FROM_WORDNET, "Insert From Turkish WordNet");
        toolBar.add(insertFromWordNet);
        toolBar.addSeparator();
        JButton insertChild = new DrawingButton(WordNetEditorFrame.class, this, "semanticrelation", INSERT_CHILD, "Insert Child");
        toolBar.add(insertChild);
        JButton breakLink = new DrawingButton(WordNetEditorFrame.class, this, "split", REMOVE_FROM_PARENT, "Remove From Parent");
        toolBar.add(breakLink);
        toolBar.addSeparator();
        JButton replace = new DrawingButton(WordNetEditorFrame.class, this, "interlingual", REPLACE, "Replace With New Synset");
        toolBar.add(replace);
        toolBar.addSeparator();
        JButton delete = new DrawingButton(WordNetEditorFrame.class, this, "delete", DELETE, "Delete");
        toolBar.add(delete);
        toolBar.addSeparator();
        JButton merge = new DrawingButton(WordNetEditorFrame.class, this, "random", MERGE, "Merge Two SynSets");
        toolBar.add(merge);
        showMoved = new JCheckBox("Show Moved");
        toolBar.add(showMoved);
    }

    private void showPath(DefaultMutableTreeNode treeNode){
        TreePath treePath = new TreePath(noun.treeModel.getPathToRoot(treeNode));
        noun.tree.setSelectionPath(treePath);
        noun.tree.scrollPathToVisible(treePath);
    }

    private void replaceAllRelationsWithNewSynSet(String oldId, String newId){
        for (SynSet synSet : domainWordNet.synSetList()){
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i).getName().equals(oldId)){
                    synSet.getRelation(i).setName(newId);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String synsetId;
        SynSet synSet;
        switch (e.getActionCommand()){
            case SAVE:
                domainWordNet.saveAsXml(domainWordNetFileName);
                break;
            case DELETE:
                if (selectedSynSet != null){
                    domainWordNet.removeSynSet(selectedSynSet);
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    parent.remove(selectedTreeNode);
                    selectedPartOfSpeechTree.treeModel.reload(parent);
                    selectedSynSet = null;
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case EDIT:
                if (selectedSynSet != null){
                    selectedSynSet.getSynonym().getLiteral(0).setName(literal.getText());
                    selectedSynSet.getSynonym().getLiteral(0).setSense(Integer.parseInt(sense.getText()));
                    selectedSynSet.setDefinition(definition.getText());
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    selectedPartOfSpeechTree.treeModel.reload(parent);
                    selectedSynSet = null;
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case REPLACE:
                if (selectedSynSet != null){
                    finalId += 10;
                    String newSynSetId = prefix + "" + finalId;
                    DefaultMutableTreeNode node = noun.nodeList.get(selectedSynSet);
                    noun.nodeList.remove(selectedSynSet);
                    replaceAllRelationsWithNewSynSet(selectedSynSet.getId(), newSynSetId);
                    selectedSynSet.setId(newSynSetId);
                    selectedSynSet.setDefinition(" ");
                    noun.nodeList.put(selectedSynSet, node);
                    node.setUserObject(new SynSetObject(selectedSynSet));
                    noun.treeModel.reload(node);
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case ADD_NEW:
                if (id.getText().length() == 13 && id.getText().charAt(5) == '-'){
                    if (domainWordNet.getSynSetWithId(id.getText()) == null){
                        if (definition.getText().length() != 0){
                            if (domainWordNet.getSynSetWithLiteral(literal.getText(), Integer.parseInt(sense.getText())) == null){
                                SynSet newSynSet = new SynSet(id.getText());
                                newSynSet.addLiteral(new Literal(literal.getText(), Integer.parseInt(sense.getText()), id.getText()));
                                newSynSet.setDefinition(definition.getText());
                                newSynSet.setPos(Pos.NOUN);
                                domainWordNet.addSynSet(newSynSet);
                                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new SynSetObject(newSynSet));
                                noun.nodeList.put(newSynSet, newChild);
                                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)noun.tree.getModel().getRoot();
                                insertIntoCorrectPosition(rootNode, newChild);
                                noun.treeModel.reload(rootNode);
                                showPath(newChild);
                            } else {
                                JOptionPane.showMessageDialog(this, "SynSet with Same Literal and Same Sense Already Exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No Definition Given!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Synset Does Exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Synset Id!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case INSERT_FROM_WORDNET:
                synsetId = JOptionPane.showInputDialog("Enter synset id");
                synSet = turkish.getSynSetWithId(synsetId);
                if (synSet != null){
                    if (synSet.getPos() == Pos.NOUN){
                        if (domainWordNet.getSynSetWithId(synsetId) == null){
                            id.setText(synsetId);
                            literal.setText(synSet.getSynonym().getLiteral(0).getName());
                            sense.setText("" + synSet.getSynonym().getLiteral(0).getSense());
                            definition.setText(synSet.getLongDefinition());
                        } else {
                            DefaultMutableTreeNode node = noun.nodeList.get(domainWordNet.getSynSetWithId(synsetId));
                            showPath(node);
                            JOptionPane.showMessageDialog(this, "Synset Does Exist In Domain WordNet!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Synset Is Not a Noun!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Synset Does Not Exist In Turkish WordNet!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case INSERT_CHILD:
                if (selectedSynSet != null){
                    synsetId = JOptionPane.showInputDialog("Enter parent synset id");
                    synSet = domainWordNet.getSynSetWithId(synsetId);
                    if (synSet != null){
                        if (synSet.getPos() == Pos.NOUN){
                            DefaultMutableTreeNode parentNode = noun.nodeList.get(synSet);
                            insertIntoCorrectPosition(parentNode, selectedTreeNode);
                            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)noun.tree.getModel().getRoot();
                            noun.treeModel.reload(rootNode);
                            if (showMoved.isSelected()){
                                showPath(selectedTreeNode);
                            }
                            selectedSynSet.addRelation(new SemanticRelation(synSet.getId(), SemanticRelationType.HYPERNYM));
                            synSet.addRelation(new SemanticRelation(selectedSynSet.getId(), SemanticRelationType.HYPONYM));
                        } else {
                            JOptionPane.showMessageDialog(this, "Parent Synset Is Not a Noun!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Parent Synset Does Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case REMOVE_FROM_PARENT:
                if (selectedSynSet != null) {
                    for (int i = 0; i < selectedSynSet.relationSize(); i++){
                        if (selectedSynSet.getRelation(i) instanceof SemanticRelation){
                            if (((SemanticRelation) selectedSynSet.getRelation(i)).getRelationType() == SemanticRelationType.HYPERNYM || ((SemanticRelation) selectedSynSet.getRelation(i)).getRelationType() == SemanticRelationType.INSTANCE_HYPERNYM){
                                synsetId = selectedSynSet.getRelation(i).getName();
                                synSet = domainWordNet.getSynSetWithId(synsetId);
                                selectedSynSet.removeRelation(selectedSynSet.getRelation(i));
                                if (synSet != null){
                                    for (int j = 0; j < synSet.relationSize(); j++){
                                        if (synSet.getRelation(j) instanceof SemanticRelation){
                                            if ((((SemanticRelation) synSet.getRelation(j)).getRelationType() == SemanticRelationType.HYPONYM || ((SemanticRelation) synSet.getRelation(j)).getRelationType() == SemanticRelationType.INSTANCE_HYPONYM) && synSet.getRelation(j).getName().equals(selectedSynSet.getId())){
                                                synSet.removeRelation(synSet.getRelation(j));
                                                break;
                                            }
                                        }
                                    }
                                    DefaultMutableTreeNode parentNode = noun.nodeList.get(synSet);
                                    parentNode.remove(selectedTreeNode);
                                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)noun.tree.getModel().getRoot();
                                    insertIntoCorrectPosition(rootNode, selectedTreeNode);
                                    noun.treeModel.reload(rootNode);
                                    if (showMoved.isSelected()){
                                        showPath(selectedTreeNode);
                                    } else {
                                        showPath(parentNode);
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case MERGE:
                if (noun.tree.getSelectionPaths() != null){
                    if (noun.tree.getSelectionPaths().length == 2){
                        DefaultMutableTreeNode selectedTreeNode1 = (DefaultMutableTreeNode) noun.tree.getSelectionPaths()[0].getLastPathComponent();
                        DefaultMutableTreeNode selectedTreeNode2 = (DefaultMutableTreeNode) noun.tree.getSelectionPaths()[1].getLastPathComponent();
                        SynSetObject synSetObject1 = (SynSetObject) selectedTreeNode1.getUserObject();
                        SynSetObject synSetObject2 = (SynSetObject) selectedTreeNode2.getUserObject();
                        SynSet synSet1 = synSetObject1.synSet;
                        SynSet synSet2 = synSetObject2.synSet;
                        synSet1.mergeSynSet(synSet2);
                        domainWordNet.removeSynSet(synSet2);
                        replaceAllRelationsWithNewSynSet(synSet2.getId(), synSet1.getId());
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedTreeNode2.getParent();
                        for (int i = 0; i < selectedTreeNode2.getChildCount(); i++){
                            insertIntoCorrectPosition(selectedTreeNode1, (DefaultMutableTreeNode) selectedTreeNode2.getChildAt(i));
                        }
                        parentNode.remove(selectedTreeNode2);
                        noun.treeModel.reload(parentNode);
                    } else {
                        JOptionPane.showMessageDialog(this, "More Than 2 Synsets Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }

    private void insertIntoCorrectPosition(DefaultMutableTreeNode parent, DefaultMutableTreeNode newChild) {
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (collator.compare(child.getUserObject().toString(), newChild.getUserObject().toString()) > 0){
                parent.insert(newChild, i);
                return;
            }
        }
        parent.add(newChild);
    }

    private PartOfSpeechTree constructTree(Pos partOfSpeech, boolean hypernym){
        DefaultMutableTreeNode parent, child, rootNode = new DefaultMutableTreeNode(partOfSpeech);
        HashMap<SynSet, DefaultMutableTreeNode> nodeList = new HashMap<>();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getPos() == partOfSpeech){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SynSetObject(synSet));
                nodeList.put(synSet, node);
            }
        }
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getPos() == partOfSpeech){
                child = nodeList.get(synSet);
                if (hypernym){
                    boolean parentFound = false;
                    for (int j = 0; j < synSet.relationSize(); j++){
                        if ((synSet.getRelation(j) instanceof SemanticRelation)){
                            SemanticRelation relation = (SemanticRelation) synSet.getRelation(j);
                            if (relation.getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM) || relation.getRelationType().equals(SemanticRelationType.HYPERNYM)){
                                SynSet parentSynSet = domainWordNet.getSynSetWithId(relation.getName());
                                if (parentSynSet != null){
                                    parent = nodeList.get(parentSynSet);
                                    if (parent != null){
                                        try{
                                            insertIntoCorrectPosition(parent, child);
                                        } catch (IllegalArgumentException e){
                                            System.out.println(synSet.getSynonym());
                                        }
                                        parentFound = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!parentFound){
                        insertIntoCorrectPosition(rootNode, child);
                    }
                } else {
                    insertIntoCorrectPosition(rootNode, child);
                }
            }
        }
        JTree tree = new JTree(treeModel);
        PartOfSpeechTree partOfSpeechTree = new PartOfSpeechTree(tree, nodeList, treeModel);
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                if (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof SynSetObject){
                    selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                    selectedSynSet = ((SynSetObject) selectedTreeNode.getUserObject()).synSet;
                    selectedPartOfSpeechTree = partOfSpeechTree;
                    id.setText(selectedSynSet.getId());
                    literal.setText(selectedSynSet.getSynonym().getLiteral(0).getName());
                    sense.setText("" + selectedSynSet.getSynonym().getLiteral(0).getSense());
                    definition.setText(selectedSynSet.getDefinition());
                    ArrayList<SynSet> alternativeList = turkish.getSynSetsWithLiteral(selectedSynSet.getSynonym().getLiteral(0).getName());
                    completed = false;
                    alternatives.removeAllItems();
                    alternatives.setSelectedIndex(-1);
                    for (SynSet synSet : alternativeList){
                        alternatives.addItem(synSet);
                        if (synSet.equals(selectedSynSet)){
                            alternatives.setSelectedItem(synSet);
                        }
                    }
                    if (alternatives.getItemCount() <= 1){
                        alternatives.setEnabled(false);
                    } else {
                        alternatives.setEnabled(true);
                    }
                    completed = true;
                } else {
                    selectedTreeNode = null;
                    selectedSynSet = null;
                }
            }
        });
        if (partOfSpeech == Pos.NOUN){
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        } else {
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        ToolTipManager.sharedInstance().registerComponent(tree);
        return partOfSpeechTree;
    }

    private void replaceWithNewSynSet(SynSet newSynSet){
        if (newSynSet != null && selectedSynSet != null){
            DefaultMutableTreeNode node = noun.nodeList.get(selectedSynSet);
            noun.nodeList.remove(selectedSynSet);
            for (SynSet synSet1 : domainWordNet.synSetList()){
                for (int i = 0; i < synSet1.relationSize(); i++){
                    if (synSet1.getRelation(i).getName().equals(selectedSynSet.getId())){
                        synSet1.getRelation(i).setName(newSynSet.getId());
                    }
                }
            }
            noun.nodeList.put(newSynSet, node);
            node.setUserObject(new SynSetObject(newSynSet));
            noun.treeModel.reload(node);
            domainWordNet.removeSynSet(selectedSynSet);
            domainWordNet.addSynSet(newSynSet);
        }
    }

    WordNetEditorFrame(){
        domainWordNet = new WordNet(domainWordNetFileName, new Locale("tr"));
        finalId = getFinalId();
        turkish = new WordNet();
        JToolBar toolBar = new JToolBar("ToolBox");
        addButtons(toolBar);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        JPanel topPanel = new JPanel(new GridLayout(3, 4));
        topPanel.add(new JLabel("Id"));
        id = new JTextField();
        topPanel.add(id);
        topPanel.add(new JLabel("Literal"));
        literal = new JTextField();
        topPanel.add(literal);
        topPanel.add(new JLabel("Sense"));
        sense = new JTextField();
        topPanel.add(sense);
        topPanel.add(new JLabel("Definition"));
        definition = new JTextField();
        topPanel.add(definition);
        leftSearch = new JTextField();
        leftSearch.addActionListener(e -> selectTree(leftSearch.getText()));
        topPanel.add(leftSearch);
        alternatives = new JComboBox();
        topPanel.add(alternatives);
        alternatives.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (completed){
                    replaceWithNewSynSet((SynSet) alternatives.getSelectedItem());
                }
            }
        });
        JPanel leftPanel = new JPanel(new BorderLayout());
        noun = constructTree(Pos.NOUN, true);
        JScrollPane nounPane = new JScrollPane(noun.tree);
        nounPane.setMinimumSize(new Dimension(400, 400));
        PartOfSpeechTree adjective = constructTree(Pos.ADJECTIVE, false);
        JScrollPane adjectivePane = new JScrollPane(adjective.tree);
        adjectivePane.setMinimumSize(new Dimension(400, 200));
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nounPane, adjectivePane);
        leftPanel.add(leftSplitPane, BorderLayout.CENTER);
        PartOfSpeechTree verb = constructTree(Pos.VERB, true);
        JScrollPane verbPane = new JScrollPane(verb.tree);
        verbPane.setMinimumSize(new Dimension(400, 400));
        JPanel rightPanel = new JPanel(new BorderLayout());
        PartOfSpeechTree adverb = constructTree(Pos.ADVERB, false);
        JScrollPane adverbPane = new JScrollPane(adverb.tree);
        adverbPane.setMinimumSize(new Dimension(400, 200));
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, verbPane, adverbPane);
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        JSplitPane allPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, splitPane);
        add(allPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setName("WordNet Editor");
    }

    private void selectTree(String searchKey){
        noun.tree.clearSelection();
        for (Map.Entry<SynSet, DefaultMutableTreeNode> entry : noun.nodeList.entrySet()){
            if (entry.getKey().getSynonym().containsLiteral(searchKey)){
                TreePath treePath = new TreePath(noun.treeModel.getPathToRoot(entry.getValue()));
                noun.tree.addSelectionPath(treePath);
                noun.tree.scrollPathToVisible(treePath);
                break;
            }
        }
    }


}
