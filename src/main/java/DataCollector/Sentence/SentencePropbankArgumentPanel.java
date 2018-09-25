package DataCollector.Sentence;

import AnnotatedSentence.*;
import AnnotatedSentence.AutoProcessor.AutoArgument.TurkishSentenceAutoArgument;
import PropBank.Frameset;
import PropBank.FramesetArgument;
import PropBank.FramesetList;
import WordNet.SynSet;
import WordNet.WordNet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;

public class SentencePropbankArgumentPanel extends AnnotatorPanel{
    private FramesetList xmlParser;
    private WordNet wordNet;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private boolean selfSelected = false;
    private HashSet<Frameset> currentFrameSets;
    private TurkishSentenceAutoArgument turkishSentenceAutoArgument;

    public SentencePropbankArgumentPanel(String currentPath, String fileName, WordNet wordNet, FramesetList xmlParser){
        super(currentPath, fileName, ViewLayerType.PROPBANK, null);
        this.wordNet = wordNet;
        setLayout(new BorderLayout());
        this.xmlParser = xmlParser;
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("FrameSets");
        turkishSentenceAutoArgument = new TurkishSentenceAutoArgument();
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && clickedWord != null && !selfSelected) {
                if (node.getLevel() == 2){
                    SynSet synSet = (SynSet) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
                    FramesetArgument argument = (FramesetArgument) node.getUserObject();
                    clickedWord.setArgument(argument.getArgumentType() + "$" + synSet.getId());
                    sentence.writeToFile(new File(fileDescription.getFileName()));
                } else {
                    if (node.getLevel() == 0){
                        clickedWord.setArgument("NONE");
                        sentence.writeToFile(new File(fileDescription.getFileName()));
                    }
                }
            }
            clickedWord = null;
            pane.setVisible(false);
            tree.setVisible(false);
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        pane.setVisible(false);
        setFocusable(false);
    }

    public void autoDetect(){
        if (turkishSentenceAutoArgument.autoArgument(sentence)){
            sentence.save();
            this.repaint();
        }
    }

    private HashSet<Frameset> getPredicateSynSets(AnnotatedSentence sentence){
        HashSet<Frameset> synSets = new HashSet<>();
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArgument() != null && word.getArgument().getArgumentType().equals("PREDICATE") && word.getSemantic() != null){
                SynSet synSet = wordNet.getSynSetWithId(word.getSemantic());
                if (synSet != null && xmlParser.frameExists(synSet.getId())){
                    synSets.add(xmlParser.getFrameSet(synSet.getId()));
                }
            }
        }
        return synSets;
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        boolean argTmp, argLoc, argDis, argMnr;
        DefaultMutableTreeNode selectedNode = null;
        currentFrameSets = getPredicateSynSets(sentence);
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        for (Frameset frameset : currentFrameSets){
            DefaultMutableTreeNode frameNode = new DefaultMutableTreeNode(wordNet.getSynSetWithId(frameset.getId()));
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(frameNode);
            argTmp = false;
            argDis = false;
            argLoc = false;
            argMnr = false;
            for (FramesetArgument argument : frameset.getFramesetArguments()){
                DefaultMutableTreeNode argumentNode = new DefaultMutableTreeNode(argument);
                frameNode.add(argumentNode);
                if (word.getArgument() != null && word.getArgument().getId() != null && word.getArgument().getId().equals(frameset.getId()) && word.getArgument().getArgumentType() != null && word.getArgument().getArgumentType().equals(argument.getArgumentType())){
                    selectedNode = argumentNode;
                }
                if (argument.getArgumentType().equals("ARGMTMP")){
                    argTmp = true;
                }
                if (argument.getArgumentType().equals("ARGMLOC")){
                    argLoc = true;
                }
                if (argument.getArgumentType().equals("ARGMDIC")){
                    argDis = true;
                }
                if (argument.getArgumentType().equals("ARGMMNR")){
                    argMnr = true;
                }
            }
            if (!argTmp){
                frameNode.add(new DefaultMutableTreeNode(new FramesetArgument("ARGMTMP", "")));
            }
            if (!argLoc){
                frameNode.add(new DefaultMutableTreeNode(new FramesetArgument("ARGMLOC", "")));
            }
            if (!argDis){
                frameNode.add(new DefaultMutableTreeNode(new FramesetArgument("ARGMDIS", "")));
            }
            if (!argMnr){
                frameNode.add(new DefaultMutableTreeNode(new FramesetArgument("ARGMMNR", "")));
            }
        }
        treeModel.reload();
        if (selectedNode != null){
            selfSelected = true;
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        return -1;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (selectedWordIndex != -1){
            populateLeaf(sentence, selectedWordIndex);
            pane.getVerticalScrollBar().setValue(0);
            pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, 90);
            clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            selectedWordIndex = -1;
            selfSelected = false;
            this.repaint();
        }
    }

    public void previous(int count) {
        while (fileDescription.previousFileExists(count)){
            fileDescription.addToIndex(-count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            if (sentence.containsPredicate()){
                break;
            }
        }
        pane.setVisible(false);
        repaint();
    }

    public void next(int count) {
        while (fileDescription.nextFileExists(count)){
            fileDescription.addToIndex(count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            if (sentence.containsPredicate()) {
                break;
            }
        }
        pane.setVisible(false);
        repaint();
    }



}
