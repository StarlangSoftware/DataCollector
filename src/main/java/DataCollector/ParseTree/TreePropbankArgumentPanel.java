package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import DataCollector.ParseTree.TreeAction.LayerAction;
import PropBank.Argument;
import PropBank.Frameset;
import PropBank.FramesetArgument;
import PropBank.FramesetList;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashSet;

public class TreePropbankArgumentPanel extends TreeLeafEditorPanel {

    private FramesetList xmlParser;
    private WordNet wordNet;
    private JTree tree;
    private DefaultTreeModel treeModel;

    public TreePropbankArgumentPanel(String path, String fileName, WordNet wordNet) {
        super(path, fileName, ViewLayerType.PROPBANK, false);
        this.wordNet = wordNet;
        xmlParser = new FramesetList();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("FrameSets");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (previousNode != null && treeNode != null) {
                LayerAction action;
                previousNode.setSelected(false);
                if (treeNode.getLevel() == 2){
                    SynSet predicateSynSet = (SynSet)((DefaultMutableTreeNode)treeNode.getParent()).getUserObject();
                    FramesetArgument argument = (FramesetArgument) treeNode.getUserObject();
                    action = new LayerAction(((TreePropbankArgumentPanel)((JTree) e.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), argument.getArgumentType() + "$" + predicateSynSet.getId(), ViewLayerType.PROPBANK);
                } else {
                    action = new LayerAction(((TreePropbankArgumentPanel)((JTree) e.getSource()).getParent().getParent().getParent()), previousNode.getLayerInfo(), "NONE", ViewLayerType.PROPBANK);
                }
                actionList.add(action);
                action.execute();
                isEditing = false;
                repaint();
            }
            pane.setVisible(false);
            tree.setVisible(false);
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private HashSet<Frameset> getPredicateSynSets(){
        HashSet<Frameset> synSets = new HashSet<>();
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) currentTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (ParseNodeDrawable leafNode : leafList){
            Argument argument = leafNode.getLayerInfo().getArgument();
            if (argument != null && argument.getArgumentType().equals("PREDICATE")){
                SynSet synSet = wordNet.getSynSetWithId(leafNode.getLayerInfo().getArgument().getId());
                if (synSet != null && xmlParser.frameExists(synSet.getId())){
                    synSets.add(xmlParser.getFrameSet(synSet.getId()));
                }
            }
        }
        return synSets;
    }

    public void populateLeaf(ParseNodeDrawable node){
        DefaultMutableTreeNode selectedNode = null;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        HashSet<Frameset> frameSets = getPredicateSynSets();
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        for (Frameset frameset : frameSets){
            DefaultMutableTreeNode frameNode = new DefaultMutableTreeNode(wordNet.getSynSetWithId(frameset.getId()));
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(frameNode);
            for (FramesetArgument framesetArgument : frameset.getFramesetArguments()){
                DefaultMutableTreeNode argumentNode = new DefaultMutableTreeNode(framesetArgument);
                frameNode.add(argumentNode);
                Argument argument = node.getLayerInfo().getArgument();
                if (argument != null && argument.getArgumentType().equals(framesetArgument.getArgumentType())){
                    selectedNode = argumentNode;
                }
            }
        }
        treeModel.reload();
        if (selectedNode != null){
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().x - 5, node.getArea().y + 30, 250, 200);
        this.repaint();
        isEditing = true;
    }

}
