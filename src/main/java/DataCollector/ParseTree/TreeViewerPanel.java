package DataCollector.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import AnnotatedTree.Processor.LeafConverter.LeafToPersian;
import AnnotatedTree.Processor.LeafConverter.LeafToTurkish;
import AnnotatedTree.Processor.TreeToStringConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

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

    private int getStringSize(ParseNodeDrawable parseNode, Graphics g, ViewLayerType viewLayer){
        int i, stringSize = 0;
        if (parseNode.numberOfChildren() == 0){
            switch (viewLayer){
                case ENGLISH_WORD:
                case TURKISH_WORD:
                case PERSIAN_WORD:
                case NER:
                    stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerData(viewLayer));
                    break;
                case PROPBANK:
                    stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getArgument().getArgumentType());
                    break;
                case ENGLISH_SEMANTICS:
                    stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerData(viewLayer).substring(6, 14));
                    break;
                case SHALLOW_PARSE:
                    try {
                        for (i = 0; i < parseNode.getLayerInfo().getNumberOfWords(); i++)
                            if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i)) > stringSize){
                                stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i));
                            }
                    } catch (LayerNotExistsException | WordNotExistsException e) {
                        e.printStackTrace();
                    }
                    break;
                case SEMANTICS:
                    try {
                        stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerData(ViewLayerType.TURKISH_WORD));
                        for (i = 0; i < parseNode.getLayerInfo().getNumberOfMeanings(); i++)
                            if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getSemanticAt(i).substring(6)) > stringSize){
                                stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getSemanticAt(i).substring(6));
                            }
                    } catch (LayerNotExistsException | WordNotExistsException e) {
                        e.printStackTrace();
                    }
                    break;
                case META_MORPHEME_MOVED:
                case META_MORPHEME:
                case PART_OF_SPEECH:
                case INFLECTIONAL_GROUP:
                case ENGLISH_PROPBANK:
                    for (i = 0; i < parseNode.getLayerInfo().getLayerSize(viewLayer); i++)
                        try {
                            if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getLayerInfoAt(viewLayer, i)) > stringSize){
                                stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getLayerInfoAt(viewLayer, i));
                            }
                        } catch (LayerNotExistsException | LayerItemNotExistsException | WordNotExistsException e) {
                            e.printStackTrace();
                        }
                    break;
                default:
                    stringSize = g.getFontMetrics().stringWidth(parseNode.getData().getName());
                    break;
            }
            return stringSize;
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    private void paint(ParseTreeDrawable parseTree, Graphics g, int nodeWidth, int nodeHeight, ViewLayerType viewLayer){
        paint(((ParseNodeDrawable)parseTree.getRoot()), g, nodeWidth, nodeHeight, parseTree.maxDepth(), viewLayer);
        if (viewLayer == ViewLayerType.INFLECTIONAL_GROUP){
            drawDependency((ParseNodeDrawable)parseTree.getRoot(), g, parseTree);
        }
    }

    private void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y, ViewLayerType viewLayer){
        int i;
        if (parseNode.numberOfChildren() == 0){
            switch (viewLayer){
                case WORD:
                    g.drawString(parseNode.getData().getName(), x, y);
                    break;
                case ENGLISH_WORD:
                case TURKISH_WORD:
                case PERSIAN_WORD:
                    g.drawString(parseNode.getLayerData(viewLayer), x, y);
                    break;
                case ENGLISH_SEMANTICS:
                    g.drawString(parseNode.getLayerData(ViewLayerType.ENGLISH_WORD), x, y);
                    g.setColor(Color.RED);
                    g.drawString(parseNode.getLayerData(viewLayer).substring(6, 14), x, y + 20);
                    break;
                case META_MORPHEME_MOVED:
                case META_MORPHEME:
                case PART_OF_SPEECH:
                case INFLECTIONAL_GROUP:
                    for (i = 0; i < parseNode.getLayerInfo().getLayerSize(viewLayer); i++){
                        if (i > 0 && !parseNode.isGuessed()){
                            g.setColor(Color.RED);
                        }
                        try {
                            g.drawString(parseNode.getLayerInfo().getLayerInfoAt(viewLayer, i), x, y);
                            y += 20;
                        } catch (LayerNotExistsException | LayerItemNotExistsException | WordNotExistsException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PROPBANK:
                    g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
                    g.setColor(Color.RED);
                    y += 25;
                    g.drawString(parseNode.getLayerInfo().getArgument().getArgumentType(), x, y);
                    if (parseNode.getLayerInfo().getArgument().getId() != null){
                        Font previousFont = g.getFont();
                        g.setFont(new Font("Serif", Font.PLAIN, 10));
                        g.drawString(parseNode.getLayerInfo().getArgument().getId(), x - 15, y + 10);
                        g.setFont(previousFont);
                    }
                    break;
                case ENGLISH_PROPBANK:
                    g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
                    g.setColor(Color.RED);
                    y += 25;
                    if (parseNode.getLayerData(ViewLayerType.PROPBANK) != null){
                        g.drawString(parseNode.getLayerInfo().getArgument().getArgumentType(), x, y);
                        if (parseNode.getLayerInfo().getArgument().getId() != null){
                            Font previousFont = g.getFont();
                            g.setFont(new Font("Serif", Font.PLAIN, 10));
                            g.drawString(parseNode.getLayerInfo().getArgument().getId(), x - 15, y + 10);
                            g.setFont(previousFont);
                        }
                    }
                    y += 25;
                    g.setColor(Color.MAGENTA);
                    g.drawString(parseNode.getLayerData(ViewLayerType.ENGLISH_WORD), x, y);
                    for (i = 0; i < parseNode.getLayerInfo().getLayerSize(viewLayer); i++){
                        g.setColor(Color.RED);
                        try {
                            y += 25;
                            g.drawString(parseNode.getLayerInfo().getArgumentAt(i).getArgumentType(), x, y);
                            if (parseNode.getLayerInfo().getArgumentAt(i).getId() != null){
                                Font previousFont = g.getFont();
                                g.setFont(new Font("Serif", Font.PLAIN, 10));
                                g.drawString(parseNode.getLayerInfo().getArgumentAt(i).getId(), x - 15, y + 10);
                                g.setFont(previousFont);
                            }
                        } catch (LayerNotExistsException | WordNotExistsException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SHALLOW_PARSE:
                    g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
                    g.setColor(Color.RED);
                    try {
                        for (i = 0; i < parseNode.getLayerInfo().getNumberOfWords(); i++){
                            try {
                                y += 20;
                                g.drawString(parseNode.getLayerInfo().getShallowParseAt(i), x, y);
                            } catch (LayerNotExistsException | WordNotExistsException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (LayerNotExistsException e) {
                        e.printStackTrace();
                    }
                    break;
                case SEMANTICS:
                    g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
                    g.setColor(Color.RED);
                    for (i = 0; i < parseNode.getLayerInfo().getNumberOfMeanings(); i++){
                        try {
                            y += 20;
                            g.drawString(parseNode.getLayerInfo().getSemanticAt(i).substring(6), x, y);
                        } catch (LayerNotExistsException | WordNotExistsException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case NER:
                    g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
                    g.setColor(Color.RED);
                    g.drawString(parseNode.getLayerData(ViewLayerType.NER), x, y + 20);
                    break;
            }
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    public void drawDependency(ParseNodeDrawable parseNode, Graphics g, ParseTreeDrawable tree){
        ParseNodeDrawable toNode;
        int toIG;
        String dependency;
        int startX, startY, dragX, dragY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        if (parseNode.numberOfChildren() == 0 && parseNode.getLayerInfo() != null && parseNode.getLayerData(ViewLayerType.DEPENDENCY) != null){
            String[] words = parseNode.getLayerData(ViewLayerType.DEPENDENCY).split(",");
            toNode = tree.getLeafWithIndex(Integer.parseInt(words[0]));
            toIG = Integer.parseInt(words[1]);
            dependency = words[2];
            startX = parseNode.getArea().x + parseNode.getArea().width / 2;
            startY = parseNode.getArea().y + 20;
            dragX = toNode.getArea().x + toNode.getArea().width / 2;
            dragY = toNode.getArea().y + 20 * toIG;
            pointStart = new Point2D.Double(startX, startY);
            pointEnd = new Point2D.Double(dragX, dragY);
            if (dragY > startY){
                pointCtrl1 = new Point2D.Double(startX, (startY + dragY) / 2 + 40);
                pointCtrl2 = new Point2D.Double((startX + dragX) / 2, dragY + 50);
            } else {
                pointCtrl1 = new Point2D.Double((startX + dragX) / 2, startY + 30);
                pointCtrl2 = new Point2D.Double(dragX, (startY + dragY) / 2 + 40);
            }
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.RED);
            g.drawString(dependency, (startX + dragX) / 2, Math.max(startY, dragY) + 50);
            g2.draw(cubicCurve);
            g2.setColor(Color.BLACK);
        } else {
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable aChild = (ParseNodeDrawable) parseNode.getChild(i);
                drawDependency(aChild, g, tree);
            }
        }
    }

    public void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize, ViewLayerType viewLayer){
        if (parseNode.numberOfChildren() == 0){
            switch (viewLayer){
                case WORD:
                case TURKISH_WORD:
                case PERSIAN_WORD:
                case ENGLISH_WORD:
                case NER:
                case PROPBANK:
                    parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
                    break;
                case ENGLISH_SEMANTICS:
                    parseNode.setArea(x - 5, y - 15, stringSize + 10, 40);
                    break;
                case SHALLOW_PARSE:
                case SEMANTICS:
                    try {
                        parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getNumberOfWords() + 1));
                    } catch (LayerNotExistsException e) {
                        e.printStackTrace();
                    }
                    break;
                case META_MORPHEME_MOVED:
                case META_MORPHEME:
                case PART_OF_SPEECH:
                case INFLECTIONAL_GROUP:
                case ENGLISH_PROPBANK:
                    parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getLayerSize(viewLayer) + 1));
                    break;
            }
        } else {
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
        }
    }

    public void paint(ParseNodeDrawable parseNode, Graphics g, int nodeWidth, int nodeHeight, int maxDepth, ViewLayerType viewLayer){
        int stringSize, addY, x, y;
        ViewLayerType originalLayer = viewLayer;
        if (parseNode.numberOfChildren() == 0 && viewLayer != ViewLayerType.WORD){
            viewLayer = parseNode.getLayerInfo().checkLayer(viewLayer);
        }
        stringSize = getStringSize(parseNode, g, viewLayer);
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
        setArea(parseNode, x, y, stringSize, viewLayer);
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
        drawString(parseNode, g, x, y, viewLayer);
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
