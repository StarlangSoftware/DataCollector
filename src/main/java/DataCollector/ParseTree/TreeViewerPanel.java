package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.LeafConverter.LeafToPersian;
import AnnotatedTree.Processor.LeafConverter.LeafToTurkish;
import AnnotatedTree.Processor.TreeToStringConverter;

import javax.swing.*;
import java.awt.*;

public class TreeViewerPanel extends JPanel {

    /**
     * Relative path for the English parse trees.
     */
    static public String englishPath = "../English/";

    /**
     * Relative path for the original forms of the sentences.
     */
    static public String originalPath = "../Original/";

    /**
     * Current tree displayed in the panel.
     */
    protected ParseTreeDrawable currentTree;

    /**
     * The parse tree is displayed in the panel with spaces from the right end and bottom end of the panel. The distance
     * from the bottom of the tree to the bottom of the panel is heightDecrease, whereas the distance from the right
     * end of the tree to the right end of the panel is widthDecrease.
     */
    protected int widthDecrease, heightDecrease;

    /**
     * The parse tree is displayed according to the given viewerLayer. If viewerLayer is PART_OF_SPEECH or INFLECTIONAL_GROUP,
     * the leaves of the tree will show the morphological analysis of the word. If viewerLayer is META_MORPHEME, the leaves of the
     * tree will show the metaMorpheme representation of the morphological analysis of the word. If viewerLayer is META_MORPHEME_MOVED,
     * the leaves of the tree will show the metaMorphemes after the pos moving phase. If viewerLayer is TURKISH_WORD, the leaves of the
     * tree will show the original translated Turkish word. If viewerLayer is PERSIAN_WORD, the leaves of the tree will show the
     * original translated Persian word. If viewerLayer is ENGLISH_WORD, the leaves of the tree will show the original English word.
     * If viewerLayer is SEMANTICS, the leaves of the tree will show the semantic id of the (Turkish or Persian) word according to the
     * WordNet. If viewerLayer is NER, the leaves of the tree will show the named entity tag of the (Turkish or Persian) word. If viewerLayer
     * is DEPENDENCY, the leaves of the tree will show the Dependency information of the (Turkish or Persian) word according to the Dependency
     * annotation.
     */
    protected ViewLayerType viewerLayer;

    /**
     * The width and height of each node in the parse tree is fixed and given by the variables nodeWidth and nodeHeight.
     */
    protected int nodeWidth = 60;
    protected int nodeHeight = 80;

    /**
     * Constructor for the ViewPanel class. Reads current tree from file and displays it.
     * @param currentPath File path of the current tree.
     * @param fileName Name of the file which stores the parse tree.
     * @param viewerLayer Name of the layer that is displayed for the current tree.
     */
    public TreeViewerPanel(String currentPath, String fileName, ViewLayerType viewerLayer){
        setLayout(null);
        this.viewerLayer = viewerLayer;
        currentTree = new ParseTreeDrawable(currentPath, fileName);
        widthDecrease = 30;
        heightDecrease = 0;
    }

    /**
     * Reads the current parse tree from the file again.
     */
    public void reload(){
        currentTree.reload();
    }

    /**
     * @return File name of the current tree with current path.
     */
    public String getFileName(){
        return currentTree.getFileDescription().getFileName();
    }

    /**
     * @return File name of the current tree without current path.
     */
    public String getRawFileName(){
        return currentTree.getFileDescription().getRawFileName();
    }

    /**
     * Overloaded function that displays the next tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of nextTree, ViewerPanel will display 0124.train. If the next tree does not
     * exist, nothing will happen.
     * @param count Number of trees to pass next
     */
    protected void nextTree(int count){
        currentTree.nextTree(count);
        repaint();
    }

    /**
     * Overloaded function that displays the previous tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of previousTree, ViewerPanel will display 0122.train. If the previous tree does not
     * exist, nothing will happen.
     * @param count Number of trees to pass before
     */
    protected void previousTree(int count){
        currentTree.previousTree(count);
        repaint();
    }

    /**
     * Returns the sentence of the current parse tree in the target language. Currently, NlpToolkit supports two target languages,
     * namely Turkish and Persian. Target language is selected according to the viewerLayer property.
     * @return String form of the current parse tree.
     */
    public String getTargetSentence(){
        TreeToStringConverter treeToStringConverter;
        switch (viewerLayer){
            case TURKISH_WORD:
                treeToStringConverter = new TreeToStringConverter(currentTree, new LeafToTurkish());
                break;
            case PERSIAN_WORD:
                treeToStringConverter = new TreeToStringConverter(currentTree, new LeafToPersian());
                break;
            default:
                treeToStringConverter = new TreeToStringConverter(currentTree, new LeafToTurkish());
                break;
        }
        return treeToStringConverter.convert();
    }

    protected void setNodeWidth(int nodeWidth){
        this.nodeWidth = nodeWidth;
        repaint();
    }

    protected void setNodeHeight(int nodeHeight){
        this.nodeHeight = nodeHeight;
        repaint();
    }

    public void saveTree(){
        currentTree.saveAsSvg(viewerLayer);
    }
    /**
     * Returns the sentence of the current parse tree in the source language. Currently, NlpToolkit supports only one source
     * language, namely English.
     * @return String form of the current parse tree.
     */
    public String getSourceSentence(){
        ParseTreeDrawable englishTree = new ParseTreeDrawable(englishPath, currentTree.getFileDescription());
        return englishTree.toSentence();
    }

    /**
     * Draws current parse tree in the panel. The width of the parse tree is calculated by multiplying the maximum inorder traversal
     * index of the current tree with nodeWidth. The height of the parse tree is calculated by multiplying the maximum depth of the
     * current tree with nodeHeight.
     * @param g Graphics object used to draw the parse tree.
     */
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int newWidth, newHeight;
        newWidth = (currentTree.getMaxInOrderTraversalIndex() + 1) * nodeWidth;
        newHeight = currentTree.maxDepth() * nodeHeight;
        setPreferredSize(new Dimension(newWidth + widthDecrease, newHeight + heightDecrease));
        currentTree.paint(g, nodeWidth, nodeHeight, viewerLayer);
        getParent().revalidate();
    }

}
