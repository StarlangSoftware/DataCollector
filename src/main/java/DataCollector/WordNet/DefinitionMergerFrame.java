package DataCollector.WordNet;

import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;

public class DefinitionMergerFrame extends SynSetProcessorFrame {
    private SynSet selectedSynSet = null;
    private DefaultMutableTreeNode selectedTreeNode = null;

    public DefinitionMergerFrame(){
        JPanel leftPanel = new JPanel(new BorderLayout());
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                if (selectedTreeNode.getUserObject() instanceof SynSet){
                    selectedSynSet = (SynSet) selectedTreeNode.getUserObject();
                    definition.setText(selectedSynSet.getLongDefinition());
                }
            }
        });
        JScrollPane leftPane = new JScrollPane(tree);
        leftPanel.add(leftPane, BorderLayout.CENTER);
        definition = new JTextField();
        definition.addActionListener(e -> {
            selectedSynSet.setDefinition(definition.getText());
            treeModel.reload(selectedTreeNode);
        });
        leftPanel.add(definition, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setName("Definition Merger");
    }

    protected ArrayList<SynSet> extractSynSets(){
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : turkish.synSetList()){
            if (synSet.getPos() != null && synSet.getPos().equals(pos) && synSet.numberOfDefinitions() > 1){
                result.add(synSet);
            }
        }
        result.sort(new SynSetDefinitionComparator());
        return result;
    }

}
