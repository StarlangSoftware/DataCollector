package DataCollector.WordNet;

import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class SynSetMergerFrame extends SynSetProcessorFrame {
    private JList<SynSet> candidateSynSetList, synSetListForLiteral;
    private SynSet selectedSynSet = null;
    private Literal selectedLiteral = null;
    private DefaultMutableTreeNode selectedTreeNode = null;
    private IdMapping mapping;
    static final protected String MERGE = "merge";

    protected void addButtons(JToolBar toolBar){
        super.addButtons(toolBar);
        JButton merge = new DrawingButton(SynSetMergerFrame.class, this, "merge", MERGE, "Merge");
        toolBar.add(merge);
    }

    public SynSetMergerFrame(){
        mapping = new IdMapping("Data/Wordnet/mapping.txt");
        JPanel leftPanel = new JPanel(new BorderLayout());
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                if (selectedTreeNode.getUserObject() instanceof SynSet){
                    selectedSynSet = (SynSet) selectedTreeNode.getUserObject();
                    selectedLiteral = null;
                    definition.setText(selectedSynSet.getLongDefinition());
                    buildListModelForSynSet(selectedSynSet);
                } else {
                    if (selectedTreeNode.getUserObject() instanceof Literal){
                        selectedLiteral = (Literal) selectedTreeNode.getUserObject();
                        buildListModelForLiteral(selectedLiteral);
                    }
                }
            }
        });
        JScrollPane leftPane = new JScrollPane(tree);
        leftPanel.add(leftPane, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new BorderLayout());
        definition = new JTextField();
        definition.addActionListener(e -> {
            selectedSynSet.setDefinition(definition.getText());
            treeModel.reload(selectedTreeNode);
        });
        rightPanel.add(definition, BorderLayout.NORTH);
        candidateSynSetList = new JList<>();
        JScrollPane rightPane1 = new JScrollPane(candidateSynSetList);
        synSetListForLiteral = new JList<>();
        JScrollPane rightPane2 = new JScrollPane(synSetListForLiteral);
        Dimension minimumSize1 = new Dimension(400, 200);
        rightPane1.setMinimumSize(minimumSize1);
        rightPane2.setMinimumSize(minimumSize1);
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightPane1, rightPane2);
        rightPanel.add(splitPane1, BorderLayout.CENTER);
        Dimension minimumSize2 = new Dimension(400, 50);
        leftPanel.setMinimumSize(minimumSize2);
        rightPanel.setMinimumSize(minimumSize2);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        add(splitPane2, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setName("SynSet Merger");
    }

    private HashSet<SynSet> getCandidateSynSetListForDefinition(SynSet keySynSet){
        HashSet<SynSet> result = new HashSet<>();
        for (int i = 0; i < keySynSet.numberOfDefinitions(); i++){
            String[] candidates = keySynSet.getDefinition(i).split(";");
            if (candidates.length > 1){
                for (String candidate : candidates){
                    ArrayList<SynSet> synSets = turkish.getSynSetsWithLiteral(candidate.trim());
                    for (SynSet turkishSynSet : synSets){
                        if (!turkishSynSet.equals(keySynSet) && turkishSynSet.getPos() != null && turkishSynSet.getPos().equals(pos)){
                            result.add(turkishSynSet);
                        }
                    }
                }
            }
        }
        return result;
    }

    protected ArrayList<SynSet> extractSynSets(){
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : turkish.synSetList()){
            if (synSet.getPos() != null && synSet.getPos().equals(pos) && getCandidateSynSetListForDefinition(synSet).size() > 0){
                result.add(synSet);
            }
        }
        result.sort(new SynSetDefinitionComparator());
        return result;
    }

    private void buildListModelForSynSet(SynSet keySynSet){
        DefaultListModel<SynSet> listModel = new DefaultListModel<>();
        HashSet<SynSet> candidates = getCandidateSynSetListForDefinition(keySynSet);
        for (SynSet synSet : candidates){
            listModel.addElement(synSet);
        }
        candidateSynSetList.setCellRenderer(new SynSetListCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(candidateSynSetList);
        candidateSynSetList.setModel(listModel);
    }

    private void buildListModelForLiteral(Literal literal){
        DefaultListModel<SynSet> listModel = new DefaultListModel<>();
        ArrayList<SynSet> synSetList = turkish.getSynSetsWithLiteral(literal.getName());
        for (SynSet synSet : synSetList){
            if (synSet.getPos().equals(pos)){
                listModel.addElement(synSet);
            }
        }
        synSetListForLiteral.setModel(listModel);
    }

    private void mergeSynSets(SynSet first, SynSet second){
        first.mergeSynSet(second);
        first.removeSameDefinitions(new Locale("tr"));
        definition.setText(first.getLongDefinition());
        mapping.add(second.getId(), first.getId());
        turkish.removeSynSet(second);
        buildListModelForSynSet(first);
    }

    private void moveLiteral(SynSet first, SynSet second, Literal literal){
        second.getSynonym().addLiteral(literal);
        second.removeSameDefinitions(new Locale("tr"));
        first.getSynonym().removeLiteral(literal);
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        SynSet matchedSynSet1;
        switch (e.getActionCommand()){
            case SAVE:
                mapping.save("Data/Wordnet/mapping.txt");
                break;
            case MERGE:
                if (tree.getSelectionPaths() != null && tree.getSelectionPaths().length == 1 && candidateSynSetList.getSelectedValuesList().size() == 0){
                    DefaultMutableTreeNode selectedTreeNode1 = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
                    if (selectedTreeNode1.getUserObject() instanceof SynSet){
                        selectedSynSet = (SynSet) selectedTreeNode1.getUserObject();
                        if (selectedSynSet.numberOfDefinitions() == 1){
                            String[] candidateLiterals = selectedSynSet.getDefinition(0).split(";");
                            String candidateLiteral = candidateLiterals[candidateLiterals.length - 1].trim();
                            int lastIndex = turkish.numberOfSynSetsWithLiteral(candidateLiteral);
                            Literal newLiteral = new Literal(candidateLiteral, lastIndex + 1, selectedSynSet.getId());
                            selectedSynSet.getSynonym().addLiteral(newLiteral);
                            selectedSynSet.removeSameDefinitions(new Locale("tr"));
                            selectedTreeNode.add(new DefaultMutableTreeNode(newLiteral));
                            treeModel.reload(selectedTreeNode1);
                        }
                    }
                } else {
                    if (tree.getSelectionPaths() != null && tree.getSelectionPaths().length == 2){
                        DefaultMutableTreeNode selectedTreeNode1 = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
                        DefaultMutableTreeNode selectedTreeNode2 = (DefaultMutableTreeNode) tree.getSelectionPaths()[1].getLastPathComponent();
                        if (selectedTreeNode1.getUserObject() instanceof SynSet && selectedTreeNode2.getUserObject() instanceof SynSet){
                            selectedSynSet = (SynSet) selectedTreeNode1.getUserObject();
                            matchedSynSet1 = (SynSet) selectedTreeNode2.getUserObject();
                            mergeSynSets(selectedSynSet, matchedSynSet1);
                            addLiteralsAsChildren(selectedTreeNode1, matchedSynSet1);
                            treeModel.removeNodeFromParent(selectedTreeNode2);
                            treeModel.reload(selectedTreeNode1);
                        }
                    } else {
                        if (candidateSynSetList.getSelectedValuesList().size() > 1 && selectedLiteral != null){
                            SynSet matchedSynSet;
                            int lastIndex = turkish.numberOfSynSetsWithLiteral(selectedLiteral.getName());
                            for (int i = 1; i < candidateSynSetList.getSelectedValuesList().size(); i++){
                                matchedSynSet = candidateSynSetList.getSelectedValuesList().get(i);
                                Literal newLiteral = new Literal(selectedLiteral.getName(), lastIndex + i, matchedSynSet.getId());
                                matchedSynSet.getSynonym().addLiteral(newLiteral);
                                matchedSynSet.removeSameDefinitions(new Locale("tr"));
                            }
                            matchedSynSet = candidateSynSetList.getSelectedValuesList().get(0);
                            SynSet selectedSynSet = turkish.getSynSetWithId(selectedLiteral.getSynSetId());
                            if (selectedSynSet.getSynonym().literalSize() == 1){
                                mergeSynSets(selectedSynSet, matchedSynSet);
                                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                                addLiteralsAsChildren(parent, matchedSynSet);
                                treeModel.reload(parent);
                            } else {
                                moveLiteral(selectedSynSet, matchedSynSet, selectedLiteral);
                                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                                parent.remove(selectedTreeNode);
                                treeModel.reload(parent);
                            }
                        } else {
                            if (selectedSynSet != null){
                                matchedSynSet1 = candidateSynSetList.getSelectedValue();
                                if (selectedLiteral != null){
                                    moveLiteral(selectedSynSet, matchedSynSet1, selectedLiteral);
                                    selectedLiteral = null;
                                    selectedSynSet = null;
                                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                                    parent.remove(selectedTreeNode);
                                    treeModel.reload(parent);
                                } else {
                                    mergeSynSets(selectedSynSet, matchedSynSet1);
                                    addLiteralsAsChildren(selectedTreeNode, matchedSynSet1);
                                    treeModel.reload(selectedTreeNode);
                                }
                            }
                        }
                    }
                }
                break;
        }
    }
}
