package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import ParseTree.ParseNode;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import AnnotatedTree.*;
import AnnotatedTree.Processor.*;
import AnnotatedTree.Processor.LeafConverter.LeafToTurkish;
import AnnotatedTree.TreeBankDrawable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class SearchFrame extends TraverseFrame{

    protected JMenu searchMenu;
    private JCheckBoxMenuItem searchWhere[];
    private SearchTree currentSearchTree;

    public SearchFrame(FsmMorphologicalAnalyzer fsm, final TreeBankDrawable traverseBank){
        super(fsm, traverseBank);
        layerCount = 8;
        searchMenu = new JMenu("Search");
        menu.add(searchMenu);
        searchWhere = new JCheckBoxMenuItem[layerCount];
        searchWhere[0] = addMenuItem(searchMenu, "English Word Layer", KeyStroke.getKeyStroke('1'), false);
        searchWhere[1] = addMenuItem(searchMenu, "Turkish Word Layer", KeyStroke.getKeyStroke('2'), false);
        searchWhere[2] = addMenuItem(searchMenu, "Morphological Layer", KeyStroke.getKeyStroke('3'), false);
        searchWhere[3] = addMenuItem(searchMenu, "MetaMorpheme Layer", KeyStroke.getKeyStroke('4'), false);
        searchWhere[4] = addMenuItem(searchMenu, "MetaMorpheme Moved Layer", KeyStroke.getKeyStroke('5'), false);
        searchWhere[5] = addMenuItem(searchMenu, "NER Layer", KeyStroke.getKeyStroke('6'), false);
        searchWhere[6] = addMenuItem(searchMenu, "Semantic Layer", KeyStroke.getKeyStroke('7'), false);
        searchWhere[7] = addMenuItem(searchMenu, "English Semantic Layer", KeyStroke.getKeyStroke('8'), false);
        searchMenu.addSeparator();
        JMenuItem itemGoTo = addMenuItem(searchMenu, "Goto Search Result", null);
        traverseLayer = 0;
        layers = new ViewLayerType[layerCount];
        layers[0] = ViewLayerType.ENGLISH_WORD;
        layers[1] = ViewLayerType.TURKISH_WORD;
        layers[2] = ViewLayerType.PART_OF_SPEECH;
        layers[3] = ViewLayerType.META_MORPHEME;
        layers[4] = ViewLayerType.META_MORPHEME_MOVED;
        layers[5] = ViewLayerType.NER;
        layers[6] = ViewLayerType.SEMANTICS;
        layers[7] = ViewLayerType.ENGLISH_SEMANTICS;
        itemSave.setVisible(false);
        itemClose.setVisible(false);
        itemCloseAll.setVisible(false);
        itemOpenMultiple.setVisible(false);
        itemGoTo.addActionListener(e -> {
            String result = JOptionPane.showInputDialog("Enter index of the search item:");
            treeIndex = Integer.parseInt(result);
            if (projectPane.getTabCount() > 0) {
                projectPane.remove(0);
            }
            displayTree();
        });
        itemOpen.removeActionListener(itemOpen.getActionListeners()[0]);
        itemOpen.addActionListener(e -> {
            int i;
            if (projectPane.getTabCount() > 0) {
                projectPane.remove(0);
            }
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select search file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File("."));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentSearchTree = new SearchTree(fcinput.getSelectedFile().getAbsolutePath());
                treeFiles = new ArrayList<>();
                if (traverseBank.size() > 0) {
                    for (i = 0; i < traverseBank.size(); i++) {
                        ParseTreeDrawable parseTree = traverseBank.get(i);
                        if (currentSearchTree.satisfy(parseTree).size() > 0) {
                            treeFiles.add(parseTree.getName());
                        }
                        infoBottom.setText("Search Completed");
                    }
                }
                if (treeFiles.size() > 0) {
                    treeIndex = 0;
                    traverseFile = fcinput.getSelectedFile().getName();
                    displayTree();
                } else {
                    treeIndex = -1;
                }
            }
        });
        for (int i = 0; i < layerCount; i++){
            final int index = i;
            searchWhere[i].addActionListener(e -> enableMenu(index));
        }
    }

    protected boolean displayTree(){
        if (treeFiles.size() == 0)
            return false;
        File file = new File(EditorPanel.treePath + "/" + treeFiles.get(treeIndex));
        if (!file.exists())
            return false;
        switch (traverseLayer){
            case 0:
                viewer = new ViewerPanel(EditorPanel.treePath, treeFiles.get(treeIndex), ViewLayerType.ENGLISH_WORD);
                break;
            case 1:
                viewer = new TranslatorPanel(dictionary, bilingualDictionary, EditorPanel.treePath, treeFiles.get(treeIndex), ViewLayerType.TURKISH_WORD);
                break;
            case 2:
                viewer = new MorphologicalAnalyzerPanel(EditorPanel.treePath, treeFiles.get(treeIndex), fsm, true);
                break;
            case 3:
                viewer = new ViewerPanel(EditorPanel.treePath, treeFiles.get(treeIndex), ViewLayerType.META_MORPHEME);
                break;
            case 4:
                viewer = new MetaMorphemeMoverPanel(EditorPanel.treePath, treeFiles.get(treeIndex));
                break;
            case 5:
                viewer = new NERPanel(EditorPanel.treePath, treeFiles.get(treeIndex), true);
                break;
            case 6:
                viewer = new TurkishSemanticPanel(EditorPanel.treePath, treeFiles.get(treeIndex), turkish, fsm, true);
                break;
            case 7:
                viewer = new EnglishSemanticPanel(EditorPanel.treePath, treeFiles.get(treeIndex), english, turkish);
                break;
        }
        viewer.setNodeWidth(widthSlider.getValue() * 5);
        viewer.setNodeHeight(40 + heightSlider.getValue() * 5);
        ArrayList<ParseNode> result = currentSearchTree.satisfy(viewer.currentTree);
        for (ParseNode parseNode: result){
            ((ParseNodeDrawable) parseNode).setSearched(true);
        }
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(viewer);
        projectPane.add(treePane, traverseFile, 0);
        projectPane.setTitleAt(0, treeFiles.get(treeIndex));
        infoBottom.setText(treeIndex + 1 + " of " + treeFiles.size());
        if (traverseLayer == 0)
            infoTop.setText(viewer.currentTree.toSentence());
        else{
            TreeToStringConverter treeToStringConverter = new TreeToStringConverter(viewer.currentTree, new LeafToTurkish());
            infoTop.setText(treeToStringConverter.convert());
        }
        return true;
    }

    private void enableMenu(int index){
        for (int i = 0; i < layerCount; i++){
            searchWhere[i].setSelected(false);
        }
        searchWhere[index].setSelected(true);
        traverseLayer = index;
        if (projectPane.getTabCount() > 0){
            projectPane.remove(0);
        }
        displayTree();
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return null;
    }

    protected void nextTree(int count){
        super.nextTree(count);
        if (viewer instanceof LeafEditorPanel){
            ((LeafEditorPanel) viewer).defaultFill();
        }
    }

    protected void previousTree(int count){
        super.previousTree(count);
        if (viewer instanceof LeafEditorPanel){
            ((LeafEditorPanel) viewer).defaultFill();
        }
    }


}
