package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import DataCollector.DataCollector;
import Translation.AutomaticTranslationDictionary;
import Translation.BilingualDictionary;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import AnnotatedTree.*;
import AnnotatedTree.TreeBankDrawable;
import Util.DrawingButton;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

public abstract class TraverseFrame extends StructureEditorFrame{

    static final protected String SAVE = "save";

    protected int layerCount;
    protected ViewLayerType layers[];
    protected int traverseLayer, treeIndex;
    protected ArrayList<String> treeFiles;
    protected ViewerPanel viewer;
    protected String traverseFile;
    protected FsmMorphologicalAnalyzer fsm;
    protected BilingualDictionary bilingualDictionary;
    protected WordNet turkish, english;
    protected TreeBankDrawable traverseBank;

    public TraverseFrame(FsmMorphologicalAnalyzer fsm, TreeBankDrawable traverseBank){
        this.fsm = fsm;
        this.traverseBank = traverseBank;
        treeFiles = new ArrayList<>();
        toolBar.addSeparator();
        JButton button = new DrawingButton(DataCollector.class, this, "save", SAVE, "Save Names of Trees");
        button.setVisible(true);
        toolBar.add(button);
    }

    public void loadTranslationDictionary(AutomaticTranslationDictionary dictionary){
        this.dictionary = dictionary;
    }

    public void loadWordNets(WordNet turkish, WordNet english){
        this.turkish = turkish;
        this.english = english;
    }

    protected boolean displayTree(){
        return true;
    }

    protected void nextTree(int count){
        if (treeIndex + count < treeFiles.size()){
            treeIndex += count;
            if (projectPane.getTabCount() > 0){
                projectPane.remove(0);
            }
            displayTree();
        }
    }

    protected void previousTree(int count){
        if (treeIndex - count >= 0){
            treeIndex -= count;
            if (projectPane.getTabCount() > 0){
                projectPane.remove(0);
            }
            displayTree();
        }
    }

    protected void saveList(){
        final JFileChooser fcOutput = new JFileChooser();
        fcOutput.setDialogTitle("Select output file for the list of file names");
        fcOutput.setDialogType(JFileChooser.SAVE_DIALOG);
        int returnVal = fcOutput.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION){
            String filename = fcOutput.getSelectedFile().getAbsolutePath();
            try {
                FileWriter fw = new FileWriter(new File(filename));
                for (String treeFile : treeFiles) {
                    fw.write(treeFile + "\n");
                }
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case SAVE:
                saveList();
                break;
        }
    }

}
