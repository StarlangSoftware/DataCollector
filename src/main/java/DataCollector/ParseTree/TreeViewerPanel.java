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
    public ParseTreeDrawable currentTree;

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
            case PERSIAN_WORD:
                treeToStringConverter = new TreeToStringConverter(currentTree, new LeafToPersian());
                break;
            case TURKISH_WORD:
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
        paint(currentTree, g, nodeWidth, nodeHeight, viewerLayer);
        getParent().revalidate();
    }

    protected void paint(ParseTreeDrawable parseTree, Graphics g, int nodeWidth, int nodeHeight, ViewLayerType viewLayer){
        paint(((ParseNodeDrawable)parseTree.getRoot()), g, nodeWidth, nodeHeight, parseTree.maxDepth(), viewLayer);
    }

    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g){
        return g.getFontMetrics().stringWidth(parseNode.getData().getName());
    }

    protected void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y){
        g.drawString(parseNode.getData().getName(), x, y);
    }

    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize){
        parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
    }

    public void paint(ParseNodeDrawable parseNode, Graphics g, int nodeWidth, int nodeHeight, int maxDepth, ViewLayerType viewLayer){
        int stringSize, addY, x, y;
        ViewLayerType originalLayer = viewLayer;
        if (parseNode.numberOfChildren() == 0 && viewLayer != ViewLayerType.WORD){
            viewLayer = parseNode.getLayerInfo().checkLayer(viewLayer);
        }
        stringSize = getStringSize(parseNode, g);
        if (parseNode.getDepth() == 0){
            addY = 15;
        } else {
            if (parseNode.getDepth() == maxDepth){
                addY = -5;
            } else {
                addY = 5;
            }
        }
        x = (parseNode.getInOrderTraversalIndex() + 1) * nodeWidth - stringSize / 2;
        y = parseNode.getDepth() * nodeHeight + addY;
        setArea(parseNode, x, y, stringSize);
        if (parseNode.isSearched()){
            g.setColor(Color.BLUE);
            g.draw3DRect(x - 5, y - 15, stringSize + 10, 20, true);
            g.setColor(Color.BLACK);
        } else {
            if (parseNode.isEditable()){
                g.setColor(Color.RED);
                g.drawRect(x - 5, y - 15, stringSize + 10, 20);
                g.setColor(Color.BLACK);
            } else {
                if (parseNode.isDragged()){
                    g.setColor(Color.MAGENTA);
                    if (parseNode.getSelectedIndex() == -1)
                        g.drawRect(x - 5, y - 15, stringSize + 10, 20);
                    else {
                        if (originalLayer != ViewLayerType.TURKISH_WORD){
                            g.drawRect(x - 5, y - 15 + 20 * parseNode.getSelectedIndex(), stringSize + 10, 20);
                        } else {
                            g.drawRect(x - 5 + parseNode.getSelectedIndex() * (stringSize + 10) / (parseNode.numberOfChildren() + 1), y - 15, (stringSize + 10) / (parseNode.numberOfChildren() + 1), 20);
                        }
                    }
                    g.setColor(Color.BLACK);
                } else {
                    if (parseNode.isSelected()){
                        if (parseNode.getSelectedIndex() == -1)
                            g.drawRect(x - 5, y - 15, stringSize + 10, 20);
                        else
                            g.drawRect(x - 5, y - 15 + 20 * parseNode.getSelectedIndex(), stringSize + 10, 20);
                    }
                }
            }
        }
        if (parseNode.numberOfChildren() == 0){
            if (parseNode.isGuessed()){
                g.setColor(Color.MAGENTA);
            } else {
                if (originalLayer != viewLayer && (originalLayer == ViewLayerType.TURKISH_WORD || originalLayer == ViewLayerType.PERSIAN_WORD)){
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
            }
        } else {
            if (parseNode.getParent() != null && parseNode == parseNode.getParent().headChild()){
                g.setColor(Color.GRAY);
            }
        }
        drawString(parseNode, g, x, y);
        g.setColor(Color.BLACK);
        for (int j = 0; j < parseNode.numberOfChildren(); j++) {
            ParseNodeDrawable aChild = (ParseNodeDrawable) parseNode.getChild(j);
            g.drawLine((parseNode.getInOrderTraversalIndex() + 1) * nodeWidth, parseNode.getDepth() * nodeHeight + 20, (aChild.getInOrderTraversalIndex() + 1) * nodeWidth, aChild.getDepth() * nodeHeight - 20);
        }
        for (int j = 0; j < parseNode.numberOfChildren(); j++) {
            ParseNodeDrawable aChild = (ParseNodeDrawable) parseNode.getChild(j);
            paint(aChild, g, nodeWidth, nodeHeight, maxDepth, viewLayer);
        }
    }

}
