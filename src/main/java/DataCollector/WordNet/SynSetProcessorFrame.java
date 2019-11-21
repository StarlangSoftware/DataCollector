package DataCollector.WordNet;

import Dictionary.Pos;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

public abstract class SynSetProcessorFrame extends JFrame implements ActionListener {
    protected Pos pos;
    protected WordNet turkish, oldTurkish00, oldTurkish01;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    protected JTextField definition;
    protected JComboBox posComboBox;
    static final protected String SAVE = "save";

    protected abstract ArrayList<SynSet> extractSynSets();

    protected void addButtons(JToolBar toolBar){
        JButton save = new DrawingButton(SynSetProcessorFrame.class, this, "save", SAVE, "Save");
        toolBar.add(save);
        posComboBox = new JComboBox<>(new Pos[]{Pos.NOUN, Pos.VERB, Pos.ADJECTIVE, Pos.ADVERB});
        posComboBox.setMaximumSize(new Dimension(150, 20));
        posComboBox.setSelectedIndex(0);
        posComboBox.addActionListener(e -> {
            pos = (Pos) posComboBox.getSelectedItem();
            rebuildTree();
        });
        toolBar.add(posComboBox);
    }

    public SynSetProcessorFrame(){
        this.pos = Pos.NOUN;
        turkish = new WordNet("turkish_wordnet.xml", new Locale("tr"));
        oldTurkish00 = new WordNet("turkish_wordnet_version_00.xml");
        oldTurkish01 = new WordNet("turkish_wordnet_version_01.xml");
        JToolBar toolBar = new JToolBar("ToolBox");
        addButtons(toolBar);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        buildInitialTree();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected void addSynSetsToTree(){
        ArrayList<SynSet> synSetList = extractSynSets();
        DefaultMutableTreeNode parent, rootNode = new DefaultMutableTreeNode("SynSet Tree");
        treeModel = new DefaultTreeModel(rootNode);
        for (SynSet synSet : synSetList){
            parent = new DefaultMutableTreeNode(synSet);
            rootNode.add(parent);
            addLiteralsAsChildren(parent, synSet);
        }
    }

    protected void rebuildTree() {
        addSynSetsToTree();
        tree.setModel(treeModel);
    }

    protected void buildInitialTree() {
        addSynSetsToTree();
        tree = new JTree(treeModel);
        tree.setCellRenderer(new LiteralTreeCellRenderer(oldTurkish00, oldTurkish01));
        ToolTipManager.sharedInstance().registerComponent(tree);
    }

    protected void addLiteralsAsChildren(DefaultMutableTreeNode parent, SynSet synSet){
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(synSet.getSynonym().getLiteral(i));
            parent.add(child);
        }
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case SAVE:
                turkish.saveAsXml("turkish_wordnet.xml");
                break;
        }
    }

}
