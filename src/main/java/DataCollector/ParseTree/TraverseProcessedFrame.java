package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import AnnotatedTree.*;
import AnnotatedTree.Processor.*;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.LayerExist.ContainsLayerInformation;
import AnnotatedTree.Processor.LayerExist.LeafListCondition;
import AnnotatedTree.Processor.LayerExist.NotContainsLayerInformation;
import AnnotatedTree.Processor.LayerExist.SemiContainsLayerInformation;
import AnnotatedTree.Processor.LeafConverter.LeafToTurkish;
import AnnotatedTree.TreeBankDrawable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class TraverseProcessedFrame extends TraverseFrame{

    protected JMenu traverseMenu, viewMenu;

    public TraverseProcessedFrame(FsmMorphologicalAnalyzer fsm, TreeBankDrawable traverseBank){
        super(fsm, traverseBank);
        String layerNames[];
        layerCount = 6;
        traverseMenu = new JMenu("Traverse");
        projectMenu.setVisible(false);
        menu.add(traverseMenu);
        JMenu itemDone = addMenu(traverseMenu, "Done List");
        JMenu itemNotDone = addMenu(traverseMenu, "Not Done List");
        JMenu itemSemiDone = addMenu(traverseMenu, "Semi Done List");
        JMenu itemSaveAll = addMenu(traverseMenu, "Save Tree In Svg Format");
        viewMenu = new JMenu("View");
        menu.add(viewMenu);
        layerNames = new String[layerCount];
        layerNames[0] = "English Word Layer";
        layerNames[1] = "Turkish Word Layer";
        layerNames[2] = "Morphological Layer";
        layerNames[3] = "NER Layer";
        layerNames[4] = "Semantic Layer";
        layerNames[5] = "Propbank Layer";
        JMenuItem itemEnglish = addMenuItem(viewMenu, layerNames[0], KeyStroke.getKeyStroke('0'), false);
        JMenuItem itemTurkish = addMenuItem(viewMenu, layerNames[1], KeyStroke.getKeyStroke('1'), false);
        JMenuItem itemMorphological = addMenuItem(viewMenu, layerNames[2], KeyStroke.getKeyStroke('2'), false);
        JMenuItem itemNER = addMenuItem(viewMenu, layerNames[3], KeyStroke.getKeyStroke('3'), false);
        JMenuItem itemSemantic = addMenuItem(viewMenu, layerNames[4], KeyStroke.getKeyStroke('4'), false);
        JMenuItem itemPropbank = addMenuItem(viewMenu, layerNames[5], KeyStroke.getKeyStroke('5'), false);
        itemEnglish.addActionListener(e -> {
            traverseLayer = 0;
            displayTree();
        });
        itemTurkish.addActionListener(e -> {
            traverseLayer = 1;
            displayTree();
        });
        itemMorphological.addActionListener(e -> {
            traverseLayer = 2;
            displayTree();
        });
        itemNER.addActionListener(e -> {
            traverseLayer = 3;
            displayTree();
        });
        itemSemantic.addActionListener(e -> {
            traverseLayer = 4;
            displayTree();
        });
        itemPropbank.addActionListener(e -> {
            traverseLayer = 5;
            displayTree();
        });
        layers = new ViewLayerType[layerCount];
        layers[0] = ViewLayerType.ENGLISH_WORD;
        layers[1] = ViewLayerType.TURKISH_WORD;
        layers[2] = ViewLayerType.PART_OF_SPEECH;
        layers[3] = ViewLayerType.NER;
        layers[4] = ViewLayerType.SEMANTICS;
        layers[5] = ViewLayerType.ENGLISH_PROPBANK;
        for (int i = 0; i < 6; i++){
            JMenuItem menuItemDone = addMenuItem(itemDone, layerNames[i]);
            final int index = i;
            menuItemDone.addActionListener(e -> {
                traverseLayer = index;
                filterFiles(new ContainsLayerInformation(layers[index]));
            });
            JMenuItem menuItemNotDone = addMenuItem(itemNotDone, layerNames[i]);
            menuItemNotDone.addActionListener(e -> {
                traverseLayer = index;
                filterFiles(new NotContainsLayerInformation(layers[index]));
            });
            JMenuItem menuItemSemiDone = addMenuItem(itemSemiDone, layerNames[i]);
            menuItemSemiDone.addActionListener(e -> {
                traverseLayer = index;
                filterFiles(new SemiContainsLayerInformation(layers[index]));
            });
            JMenuItem menuItemSaveAll = addMenuItem(itemSaveAll, layerNames[i]);
            menuItemSaveAll.addActionListener(e -> traverseBank.get(treeIndex).saveAsSvg(layers[index]));
        }
    }

    private void filterFiles(LeafListCondition leafListCondition){
        int i;
        treeFiles = new ArrayList<>();
        if (traverseBank.size() > 0) {
            for (i = 0; i < traverseBank.size(); i++) {
                ParseTreeDrawable parseTree = traverseBank.get(i);
                NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsLeafNode());
                ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
                if (leafListCondition.satisfies(leafList)) {
                    treeFiles.add(parseTree.getName());
                }
            }
        }
        if (treeFiles.size() > 0) {
            treeIndex = 0;
            traverseFile = treeFiles.get(treeIndex);
            displayTree();
        } else {
            treeIndex = -1;
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
                viewer = new MorphologicalAnalyzerPanel(EditorPanel.treePath, treeFiles.get(treeIndex), fsm, false);
                break;
            case 3:
                viewer = new NERPanel(EditorPanel.treePath, treeFiles.get(treeIndex), false);
                break;
            case 4:
                viewer = new TurkishSemanticPanel(EditorPanel.treePath, treeFiles.get(treeIndex), turkish, fsm, false);
                break;
            case 5:
                viewer = new PropbankArgumentPanel(EditorPanel.treePath, treeFiles.get(treeIndex), turkish);
                break;
        }
        viewer.setNodeWidth(widthSlider.getValue() * 5);
        viewer.setNodeHeight(40 + heightSlider.getValue() * 5);
        JScrollPane treePane = new JScrollPane();
        treePane.setViewportView(viewer);
        if (projectPane.getTabCount() > 0){
            projectPane.remove(0);
        }
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

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return null;
    }
}
