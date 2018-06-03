package DataCollector.PropBank;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import ParseTree.ParseTree;
import DataCollector.DataCollector;
import Dictionary.Pos;
import AnnotatedTree.*;
import AnnotatedTree.AutoProcessor.AutoArgument.TurkishAutoArgument;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.LayerExist.ContainsLayerInformation;
import AnnotatedTree.Processor.LeafConverter.LeafToTurkish;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.Processor.TreeToStringConverter;
import DataCollector.ParseTree.EditorPanel;
import PropBank.Frameset;
import PropBank.FramesetList;
import AnnotatedTree.TreeBankDrawable;
import WordNet.*;
import WordNet.WordNet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Collator;
import java.util.*;
import java.util.List;

public class ArgumentFrame extends DataCollector implements Comparator<DefaultMutableTreeNode>{

    private WordNet wordNet;
    private Map<String, List<Integer>> semanticsMap;
    private Map<String, Map<Integer, List<ParseTreeDrawable>>> synSetMap;
    private ArrayList<ParseTreeDrawable> synsetTrees;
    private int countTree;
    private SynSet currentSynSet;
    private JPanel argumentsPanel;
    ArgumentPanel argumentPanel;
    private ArgumentEditorPanel argumentEditorPanel = null;
    private FramesetList xmlParser;
    private JTree tree;
    private JCheckBox autoArgumentOption;
    private JCheckBox englishIncluded;

    static final protected String BACKWARD = "backward";
    static final protected String FORWARD = "forward";

    public ArgumentFrame(WordNet wordNet, TreeBankDrawable treeBank) throws WordNotExistsException, LayerNotExistsException {
        this.wordNet = wordNet;
        xmlParser = new FramesetList("frameset.xml");
        argumentsPanel = new JPanel();
        autoArgumentOption = new JCheckBox("AutoArgument", false);
        toolBar.add(autoArgumentOption);
        englishIncluded = new JCheckBox("English", false);
        englishIncluded.addActionListener(e -> {
            if (englishIncluded.isSelected()){
                argumentPanel.setViewLayerType(ViewLayerType.ENGLISH_PROPBANK);
            } else {
                argumentPanel.setViewLayerType(ViewLayerType.PROPBANK);
            }
        });
        toolBar.add(englishIncluded);
        add(argumentsPanel, BorderLayout.EAST);
        findAllVerbs(treeBank);
        tree = buildTree();
        setName("Argument Editor");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JScrollPane pane = new JScrollPane(tree);
        add(pane, BorderLayout.WEST, 0);
        projectMenu.setVisible(false);
        setVisible(true);
    }

    public class NodeDrawer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) currentNode.getParent();
            ContainsLayerInformation layerControl = new ContainsLayerInformation(ViewLayerType.PROPBANK);
            if (leaf){
                SynSet synSet = wordNet.getSynSetWithLiteral((String) parentNode.getUserObject(), (Integer) currentNode.getUserObject());
                if (xmlParser.frameExists(synSet.getId())){
                    setForeground(Color.BLACK);
                    ArrayList<ParseTreeDrawable> trees = (ArrayList<ParseTreeDrawable>) synSetMap.get(parentNode.getUserObject()).get(currentNode.getUserObject());
                    for (ParseTreeDrawable parseTree : trees){
                        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
                        if (!layerControl.satisfies(nodeDrawableCollector.collect())){
                            setForeground(new Color(0, 128, 0));
                            break;
                        }
                    }
                } else {
                    setForeground(Color.RED);
                }
            } else {
                if (parentNode != null){
                    String name = (String) currentNode.getUserObject();
                    setForeground(Color.BLACK);
                    for (Integer i:semanticsMap.get(name)){
                        SynSet synSet = wordNet.getSynSetWithLiteral(name, i);
                        if (!xmlParser.frameExists(synSet.getId())){
                            setForeground(Color.RED);
                            break;
                        } else {
                            ArrayList<ParseTreeDrawable> trees = (ArrayList<ParseTreeDrawable>) synSetMap.get(name).get(i);
                            for (ParseTreeDrawable parseTree : trees){
                                NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
                                if (!layerControl.satisfies(nodeDrawableCollector.collect())){
                                    setForeground(new Color(0, 128, 0));
                                }
                            }
                        }
                    }
                }
            }
            return this;
        }
    }

    public JTree buildTree() {
        int verbCount = 0, synSetCount = 0;
        ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<>();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Fiiller");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        for (String name : semanticsMap.keySet()) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);
            for (Integer i:semanticsMap.get(name)){
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(i);
                treeNode.add(childNode);
                synSetCount++;
            }
            nodeList.add(treeNode);
            verbCount++;
        }
        rootNode.setUserObject("Fiiller (" + verbCount + "/" + synSetCount + ")");
        Collections.sort(nodeList, this);
        for (DefaultMutableTreeNode node : nodeList){
            rootNode.add(node);
        }
        final JTree tree = new JTree(treeModel);
        tree.addTreeSelectionListener(e -> {
            if (((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getChildCount() == 0) {
                SynSet synSet = wordNet.getSynSetWithLiteral(((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getParent().toString(), Integer.valueOf((tree.getLastSelectedPathComponent()).toString()));
                synsetTrees = (ArrayList<ParseTreeDrawable>) synSetMap.get(((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getParent().toString()).get(Integer.valueOf((tree.getLastSelectedPathComponent()).toString()));
                currentSynSet = synSet;
                countTree = 0;
                initTreeView();
                if (argumentsPanel.getComponentCount() != 0) {
                    argumentsPanel.removeAll();
                }
                initFramesetEditorView();
            }
        });
        tree.setCellRenderer(new NodeDrawer());
        return tree;
    }

    private void initTreeView(){
        ParseTreeDrawable parseTree = synsetTrees.get(countTree);
        if (autoArgumentOption.isSelected()){
            TurkishAutoArgument turkishAutoArgument = new TurkishAutoArgument();
            Frameset frameset = xmlParser.getFrameSet(currentSynSet.getId());
            if (frameset != null){
                turkishAutoArgument.autoArgument(parseTree, frameset);
            }
        }
        if (argumentEditorPanel != null){
            argumentEditorPanel.setTreeName(parseTree.getName());
        }
        if (englishIncluded.isSelected()){
            argumentPanel = new ArgumentPanel(EditorPanel.TURKISH_PATH, parseTree.getName(), ViewLayerType.ENGLISH_PROPBANK, currentSynSet.getId(), parseTree);
        } else {
            argumentPanel = new ArgumentPanel(EditorPanel.TURKISH_PATH, parseTree.getName(), ViewLayerType.PROPBANK, currentSynSet.getId(), parseTree);
        }
        if (projectPane.getComponentCount() != 0){
            projectPane.removeAll();
        }
        projectPane.add(new JScrollPane(argumentPanel), countTree + 1 + "/" + synsetTrees.size());
        TreeToStringConverter treeToStringConverter =  new TreeToStringConverter(parseTree, new LeafToTurkish());
        infoBottom.setText(treeToStringConverter.convert());
        ParseTree englishTree;
        try {
            englishTree = new ParseTree(new FileInputStream(new File(EditorPanel.ENGLISH_PATH + "/" + parseTree.getName())));
            infoTop.setText(englishTree.toSentence());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initFramesetEditorView(){
        ParseTreeDrawable parseTreeDrawable = synsetTrees.get(countTree);
        argumentEditorPanel = new ArgumentEditorPanel(currentSynSet, parseTreeDrawable.getName(), xmlParser);
        argumentsPanel.removeAll();
        argumentsPanel.add(argumentEditorPanel);
    }

    private void addToMaps(Literal literal, ParseTreeDrawable parseTree){
        if (!semanticsMap.containsKey(literal.getName())){
            List<Integer> senses = new ArrayList<>();
            senses.add(literal.getSense());
            semanticsMap.put(literal.getName(), senses);
            HashMap<Integer, List<ParseTreeDrawable>> treeMap = new HashMap<>();
            List<ParseTreeDrawable> trees = new ArrayList<>();
            trees.add(parseTree);
            treeMap.put(literal.getSense(), trees);
            synSetMap.put(literal.getName(), treeMap);
        } else {
            if (!semanticsMap.get(literal.getName()).contains(literal.getSense())){
                semanticsMap.get(literal.getName()).add(literal.getSense());
                HashMap<Integer, List<ParseTreeDrawable>> treeMap = (HashMap<Integer, List<ParseTreeDrawable>>) synSetMap.get(literal.getName());
                List<ParseTreeDrawable> trees = new ArrayList<>();
                trees.add(parseTree);
                treeMap.put(literal.getSense(), trees);
            } else {
                synSetMap.get(literal.getName()).get(literal.getSense()).add(parseTree);
            }
        }
    }

    public void findAllVerbs(TreeBankDrawable treeBank) {
        List<ParseTreeDrawable> treesWithPredicates = treeBank.extractTreesWithPredicates(wordNet);
        synSetMap = new HashMap<>();
        semanticsMap = new HashMap<>();
        for (ParseTreeDrawable parseTree : treesWithPredicates){
            ArrayList<ParseNodeDrawable> nodesWithPredicateVerbs = parseTree.extractNodesWithPredicateVerbs(wordNet);
            for (ParseNodeDrawable parseNode : nodesWithPredicateVerbs) {
                LayerInfo layerInfo = parseNode.getLayerInfo();
                try {
                    for (int i = layerInfo.getNumberOfMeanings() - 1; i >= 0; i--) {
                        String synSetId = layerInfo.getSemanticAt(i);
                        SynSet synSet = wordNet.getSynSetWithId(synSetId);
                        if (synSetId != null && synSet.getPos() == Pos.VERB) {
                            if (synSet.getSynonym().literalSize() == 1){
                                addToMaps(synSet.getSynonym().getLiteral(0), parseTree);
                            } else {
                                for (int j = 0; j < synSet.getSynonym().literalSize(); j++){
                                    Literal literal = synSet.getSynonym().getLiteral(j);
                                    String verbRoot = literal.getName().substring(0, literal.getName().length() - 3);
                                    if (layerInfo.getLayerData(ViewLayerType.TURKISH_WORD).toLowerCase(new Locale("tr")).contains(verbRoot) || layerInfo.getLayerData(ViewLayerType.META_MORPHEME).toLowerCase(new Locale("tr")).contains(verbRoot)){
                                        addToMaps(literal, parseTree);
                                    } else {
                                        if (verbRoot.contains(" ")){
                                            verbRoot = verbRoot.substring(verbRoot.lastIndexOf(" ") + 1);
                                            if (layerInfo.getLayerData(ViewLayerType.TURKISH_WORD).toLowerCase(new Locale("tr")).contains(verbRoot) || layerInfo.getLayerData(ViewLayerType.META_MORPHEME).toLowerCase(new Locale("tr")).contains(verbRoot)) {
                                                addToMaps(literal, parseTree);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                } catch (LayerNotExistsException | WordNotExistsException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String k:semanticsMap.keySet()){
            Collections.sort(semanticsMap.get(k));
        }
    }

    private void nextTree(int count) {
        if (countTree < synsetTrees.size() - count){
            countTree += count;
            initTreeView();
        }
        tree.repaint();
    }

    private void previousTree(int count) {
        if (countTree > count - 1){
            countTree -= count;
            initTreeView();
        }
        tree.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case BACKWARD:
                previousTree(1);
                break;
            case FORWARD:
                nextTree(1);
                break;
        }
    }

    public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        return collator.compare((String) o1.getUserObject(), (String) o2.getUserObject());
    }
}
