package DataCollector.Sentence.FrameNet;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.ViewLayerType;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import FrameNet.Frame;
import FrameNet.FrameNet;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class SentenceFrameNetElementPanel extends SentenceAnnotatorPanel {

    private FrameNet frameNet;
    private WordNet wordNet;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private ArrayList<DisplayedFrame> currentFrames;
    private boolean selfSelected = false;

    public SentenceFrameNetElementPanel(String currentPath, String fileName, WordNet wordNet, FrameNet frameNet){
        super(currentPath, fileName, ViewLayerType.FRAMENET);
        this.wordNet = wordNet;
        setLayout(new BorderLayout());
        this.frameNet = frameNet;
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Frames");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && clickedWord != null && !selfSelected) {
                if (node.getLevel() == 2){
                    DisplayedFrame displayedFrame = (DisplayedFrame) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
                    String frameElement = (String) node.getUserObject();
                    clickedWord.setFrameElement(frameElement + "$" + displayedFrame.getFrame().getName() + "$" + displayedFrame.getLexicalUnit());
                    sentence.writeToFile(new File(fileDescription.getFileName()));
                } else {
                    if (node.getLevel() == 0){
                        clickedWord.setFrameElement("NONE");
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

    private void getFrames(AnnotatedSentence sentence){
        currentFrames = new ArrayList<>();
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getFrameElement() != null && word.getFrameElement().getFrameElementType().equals("PREDICATE") && word.getSemantic() != null){
                SynSet synSet = wordNet.getSynSetWithId(word.getSemantic());
                if (synSet != null && frameNet.lexicalUnitExists(synSet.getId())){
                    for (Frame frame : frameNet.getFrames(synSet.getId())){
                        if (!currentFrames.contains(new DisplayedFrame(frame, synSet.getId()))){
                            currentFrames.add(new DisplayedFrame(frame, synSet.getId()));
                        }
                    }
                }
            }
        }
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        DefaultMutableTreeNode selectedNode = null;
        getFrames(sentence);
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        for (DisplayedFrame frame : currentFrames){
            DefaultMutableTreeNode frameNode = new DefaultMutableTreeNode(frame);
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(frameNode);
            for (int i = 0; i < frame.getFrame().frameElementSize(); i++){
                String frameElement = frame.getFrame().getFrameElement(i);
                DefaultMutableTreeNode frameElementNode = new DefaultMutableTreeNode(frameElement);
                frameNode.add(frameElementNode);
                if (word.getFrameElement() != null && word.getFrameElement().getId() != null && word.getFrameElement().getFrameElementType() != null && word.getFrameElement().getFrameElementType().equals(frameElement) && word.getFrameElement().getFrame() != null && word.getFrameElement().getFrame().equals(frame.getFrame().getName())){
                    selectedNode = frameElementNode;
                }
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
            pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, 200);
            clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            selectedWordIndex = -1;
            selfSelected = false;
            this.repaint();
        }
    }

}
