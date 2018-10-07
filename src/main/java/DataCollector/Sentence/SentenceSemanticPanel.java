package DataCollector.Sentence;

import AnnotatedSentence.*;
import AnnotatedSentence.AutoProcessor.AutoSemantic.TurkishSentenceAutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import DataGenerator.InstanceGenerator.FeaturedSemanticInstanceGenerator;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SentenceSemanticPanel extends AnnotatorPanel{

    private FsmMorphologicalAnalyzer fsm;
    private WordNet wordNet;
    private TurkishSentenceAutoSemantic turkishSentenceAutoSemantic;
    private JTree tree;
    private DefaultTreeModel treeModel;

    public SentenceSemanticPanel(String currentPath, String fileName, FsmMorphologicalAnalyzer fsm, WordNet wordNet){
        super(currentPath, fileName, ViewLayerType.SEMANTICS, new FeaturedSemanticInstanceGenerator(fsm, wordNet, 1));
        this.fsm = fsm;
        this.wordNet = wordNet;
        turkishSentenceAutoSemantic = new TurkishSentenceAutoSemantic(wordNet, fsm);
        setLayout(new BorderLayout());
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Anlamlar");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && clickedWord != null) {
                if (node.getLevel() == 0 || (node.getLevel() == 1 && node.getUserObject() instanceof SynSet)){
                    if (node.getLevel() == 0){
                        clickedWord.setSemantic(null);
                    } else {
                        if (clickedWord.getSemantic() != null){
                            sentence.updateConnectedPredicate(clickedWord.getSemantic(), ((SynSet) node.getUserObject()).getId());
                        }
                        clickedWord.setSemantic(((SynSet) node.getUserObject()).getId());
                    }
                    tree.setVisible(false);
                    sentence.writeToFile(new File(fileDescription.getFileName()));
                    pane.setVisible(false);
                    clickedWord = null;
                    repaint();
                }
            }
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setVisible(false);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
    }

    public void autoDetect(){
        turkishSentenceAutoSemantic.autoSemantic(sentence);
        sentence.save();
        this.repaint();
    }

    private DefaultMutableTreeNode addSynSets(AnnotatedWord word, ArrayList<SynSet> synSets){
        DefaultMutableTreeNode selectedNode = null;
        for (SynSet synSet : synSets) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(synSet);
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(synSet.getSynonym().getLiteral(i));
                childNode.add(grandChildNode);
            }
            if (word.getSemantic() != null && word.getSemantic().equals(synSet.getId())) {
                selectedNode = childNode;
            }
        }
        return selectedNode;
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        AnnotatedWord word;
        int lineIndex = 0, currentLeft = wordSpace, multiple = 1;
        String current;
        Font currentFont = g.getFont();
        g.setColor(Color.BLUE);
        for (int i = 0; i < sentence.wordCount(); i++){
            word = (AnnotatedWord) sentence.getWord(i);
            int maxSize = maxLayerLength(word, g);
            if (maxSize + currentLeft >= getWidth()){
                lineIndex++;
                currentLeft = wordSpace;
                multiple = 1;
            }
            multiple--;
            if (word.getSemantic() != null && multiple == 0) {
                SynSet synSet = wordNet.getSynSetWithId(word.getSemantic());
                if (synSet != null){
                    multiple = 1;
                    if (i + 1 < sentence.wordCount()){
                        AnnotatedWord next = (AnnotatedWord) sentence.getWord(i + 1);
                        if (next.getSemantic() != null && synSet.equals(wordNet.getSynSetWithId(next.getSemantic()))){
                            multiple = 2;
                        }
                    }
                    if (i + 2 < sentence.wordCount()){
                        AnnotatedWord twoNext = (AnnotatedWord) sentence.getWord(i + 2);
                        if (twoNext.getSemantic() != null && multiple == 2 && synSet.equals(wordNet.getSynSetWithId(twoNext.getSemantic()))){
                            multiple = 3;
                        }
                    }
                    if (synSet.getDefinition() != null){
                        if (synSet.getDefinition().length() < 24 + (multiple - 1) * 35){
                            current = synSet.getDefinition();
                        } else {
                            current = synSet.getDefinition().substring(0, 24 + (multiple - 1) * 35);
                        }
                        g.setFont(new Font(currentFont.getName(), Font.PLAIN, currentFont.getSize() - 2));
                        g.drawString(current, currentLeft, (lineIndex + 1) * lineSpace + 50);
                        g.setFont(currentFont);
                    }
                }
            } else {
                if (word.getSemantic() == null){
                    multiple = 1;
                }
            }
            currentLeft += maxSize + wordSpace;
        }
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), (lineIndex + 2) * lineSpace));
        getParent().invalidate();
    }


    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        DefaultMutableTreeNode selectedNode = null;
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        for (int i = wordIndex - 2; i <= wordIndex; i++){
            if (i >= 0 && i + 2 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                AnnotatedWord word3 = (AnnotatedWord) sentence.getWord(i + 2);
                if (word1.getParse() != null && word2.getParse() != null && word3.getParse() != null){
                    ArrayList<SynSet> idioms = wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word3.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), word3.getMetamorphicParse(), fsm);
                    DefaultMutableTreeNode currentSelected = addSynSets(word, idioms);
                    if (currentSelected != null){
                        selectedNode = currentSelected;
                    }
                }
            }
        }
        for (int i = wordIndex - 1; i <= wordIndex; i++){
            if (i >= 0 && i + 1 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                if (word1.getParse() != null && word2.getParse() != null){
                    ArrayList<SynSet> idioms = wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), fsm);
                    DefaultMutableTreeNode currentSelected = addSynSets(word, idioms);
                    if (currentSelected != null){
                        selectedNode = currentSelected;
                    }
                }
            }
        }
        if (word.getParse() != null){
            ArrayList<SynSet> synSets = wordNet.constructSynSets(word.getParse().getWord().getName(), word.getParse(), word.getMetamorphicParse(), fsm);
            DefaultMutableTreeNode currentSelected = addSynSets(word, synSets);
            if (currentSelected != null){
                selectedNode = currentSelected;
            }
        }
        treeModel.reload();
        if (selectedNode != null){
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, 30 + Math.max(3, Math.min(15, ((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount() + 1)) * 18);
        return selectedIndex;
    }

}
