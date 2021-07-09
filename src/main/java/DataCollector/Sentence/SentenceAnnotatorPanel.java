package DataCollector.Sentence;

import AnnotatedSentence.*;
import Corpus.FileDescription;
import AnnotatedSentence.AnnotatedWord;
import MorphologicalAnalysis.FsmParse;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseTreeDrawable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

public abstract class SentenceAnnotatorPanel extends JPanel implements MouseListener, MouseMotionListener {
    protected JTextField editText;
    public AnnotatedSentence sentence;
    protected FileDescription fileDescription;
    protected int wordSpace = 60, lineSpace;
    protected int selectedWordIndex = -1, draggedWordIndex = -1;
    protected boolean selectionMode = false;
    protected AnnotatedWord clickedWord = null, lastClickedWord = null;
    protected ViewLayerType layerType;
    protected JList list;
    protected DefaultListModel listModel;
    protected JScrollPane pane;

    public SentenceAnnotatorPanel(String currentPath, String rawFileName, final ViewLayerType layerType){
        this.fileDescription = new FileDescription(currentPath, rawFileName);
        this.layerType = layerType;
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        setLayout(null);
        sentence = new AnnotatedSentence(new File(currentPath + "/" + rawFileName));
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setVisible(false);
        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (list.getSelectedIndex() != -1 && clickedWord != null) {
                    clickedWord.setSelected(false);
                    switch (layerType){
                        case NER:
                            clickedWord.setNamedEntityType((String) list.getSelectedValue());
                            break;
                        case INFLECTIONAL_GROUP:
                            clickedWord.setParse(((FsmParse)list.getSelectedValue()).transitionList());
                            clickedWord.setMetamorphicParse(((FsmParse)list.getSelectedValue()).withList());
                            break;
                        case PROPBANK:
                            clickedWord.setArgument(list.getSelectedValue().toString());
                            break;
                        case FRAMENET:
                            clickedWord.setFrameElement(list.getSelectedValue().toString());
                            break;
                        case SLOT:
                            clickedWord.setSlot(list.getSelectedValue().toString());
                            break;
                        case POLARITY:
                            clickedWord.setPolarity(list.getSelectedValue().toString());
                            break;
                        case SHALLOW_PARSE:
                            clickedWord.setShallowParse((String) list.getSelectedValue());
                            break;
                        case POS_TAG:
                            clickedWord.setPosTag((String) list.getSelectedValue());
                            break;
                        case DEPENDENCY:
                            String relation = ((String) list.getSelectedValue()).toLowerCase();
                            if (relation.equals("root")){
                                clickedWord.setUniversalDependency(0, relation);
                            } else {
                                clickedWord.setUniversalDependency(draggedWordIndex + 1, relation);
                            }
                            draggedWordIndex = -1;
                            selectionMode = false;
                            break;
                    }
                    sentence.writeToFile(new File(fileDescription.getFileName()));
                    list.setVisible(false);
                    pane.setVisible(false);
                    clickedWord = null;
                    list.setSelectedIndex(-1);
                    repaint();
                }
            }
        });
        list.setFocusTraversalKeysEnabled(false);
        editText = new JTextField();
        editText.setVisible(false);
        editText.addActionListener(actionEvent -> {
            if (clickedWord != null) {
                clickedWord.setName(editText.getText());
                sentence.writeToFile(new File(fileDescription.getFileName()));
                editText.setVisible(false);
                list.setVisible(false);
                pane.setVisible(false);
                repaint();
            }
        });
        add(editText);
        editText.setFocusTraversalKeysEnabled(false);
        pane = new JScrollPane(list);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
    }

    public void previous(int count) {
        if (fileDescription.previousFileExists(count)){
            fileDescription.addToIndex(-count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            pane.setVisible(false);
            repaint();
        }
    }

    public void next(int count) {
        if (fileDescription.nextFileExists(count)){
            fileDescription.addToIndex(count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            pane.setVisible(false);
            repaint();
        }
    }

    public String getSourceSentence(){
        ParseTreeDrawable englishTree = new ParseTreeDrawable(TreeEditorPanel.englishPath, fileDescription);
        if (englishTree.getRoot() != null){
            return englishTree.toSentence();
        } else {
            return "";
        }
    }

    public String getOriginalSentence(){
        AnnotatedSentence originalSentence = new AnnotatedSentence(new File(TreeEditorPanel.originalPath + fileDescription.getRawFileName()));
        return originalSentence.toWords();
    }

    public AnnotatedWord getClickedWord(){
        return lastClickedWord;
    }

    public String getRawFileName(){
        return fileDescription.getRawFileName();
    }

    protected int maxLayerLength(AnnotatedWord word, Graphics g){
        int size, maxSize = g.getFontMetrics().stringWidth(word.getName());
        switch (layerType){
            case NER:
                if (word.getNamedEntityType() != null){
                    size = g.getFontMetrics().stringWidth(word.getNamedEntityType().toString());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case INFLECTIONAL_GROUP:
                if (word.getParse() != null){
                    for (int j = 0; j < word.getParse().size(); j++){
                        size = g.getFontMetrics().stringWidth(word.getParse().getInflectionalGroupString(j));
                        if (size > maxSize){
                            maxSize = size;
                        }
                    }
                }
                break;
            case SEMANTICS:
                if (word.getSemantic() != null){
                    size = g.getFontMetrics().stringWidth(word.getSemantic());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case PROPBANK:
                if (word.getArgument() != null){
                    size = g.getFontMetrics().stringWidth(word.getArgument().getArgumentType());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case FRAMENET:
                if (word.getFrameElement() != null){
                    size = g.getFontMetrics().stringWidth(word.getFrameElement().getFrameElementType());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case SLOT:
                if (word.getSlot() != null){
                    size = g.getFontMetrics().stringWidth(word.getSlot().toString());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case POLARITY:
                if (word.getPolarity() != null){
                    size = g.getFontMetrics().stringWidth(word.getPolarity().toString());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case SHALLOW_PARSE:
                if (word.getShallowParse() != null){
                    size = g.getFontMetrics().stringWidth(word.getShallowParse());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
            case POS_TAG:
                if (word.getPosTag() != null){
                    size = g.getFontMetrics().stringWidth(word.getPosTag());
                    if (size > maxSize){
                        maxSize = size;
                    }
                }
                break;
        }
        return maxSize;
    }

    private void setLineSpace(){
        if (layerType != ViewLayerType.INFLECTIONAL_GROUP){
            lineSpace = 60;
        }
        int maxSize = 1;
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            switch (layerType){
                case INFLECTIONAL_GROUP:
                    if (word.getParse() != null && word.getParse().size() > maxSize){
                        maxSize = word.getParse().size();
                    }
                    break;
            }
        }
        lineSpace = 40 * (maxSize + 1);
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        AnnotatedWord previousWord = null, word;
        int lineIndex, currentLeft = wordSpace, maxSize;
        ArrayList<Integer> wordSize = new ArrayList<>();
        ArrayList<Integer> wordTotal = new ArrayList<>();
        if (layerType == ViewLayerType.DEPENDENCY){
            for (int i = 0; i < sentence.wordCount(); i++) {
                wordTotal.add(currentLeft);
                word = (AnnotatedWord) sentence.getWord(i);
                maxSize = maxLayerLength(word, g);
                wordSize.add(maxSize);
                currentLeft += maxSize + wordSpace;
            }
            lineIndex = 1;
        } else {
            lineIndex = 0;
        }
        boolean bold = false;
        Font currentFont = g.getFont();
        String correct;
        setLineSpace();
        currentLeft = wordSpace;
        for (int i = 0; i < sentence.wordCount(); i++){
            if (i > 0){
                previousWord = (AnnotatedWord) sentence.getWord(i - 1);
            }
            word = (AnnotatedWord) sentence.getWord(i);
            maxSize = maxLayerLength(word, g);
            int stringWidth = g.getFontMetrics().stringWidth(word.getName());
            int stringHeight = (int) g.getFontMetrics().getStringBounds(word.getName(), g).getHeight();
            if (maxSize + currentLeft >= getWidth() && layerType != ViewLayerType.DEPENDENCY){
                lineIndex++;
                currentLeft = wordSpace;
            }
            g.setColor(Color.BLACK);
            if (layerType == ViewLayerType.PROPBANK){
                if (word.getShallowParse() != null && previousWord != null && previousWord.getShallowParse() != null){
                    if (!previousWord.getShallowParse().equals(word.getShallowParse())){
                        bold = !bold;
                    }
                }
                if (bold){
                    g.setFont(new Font(currentFont.getName(), Font.BOLD, currentFont.getSize()));
                } else {
                    g.setFont(currentFont);
                }
            }
            g.drawString(word.getName(), currentLeft, (lineIndex + 1) * lineSpace);
            if (layerType == ViewLayerType.PROPBANK){
                g.setFont(currentFont);
            }
            word.setArea(new Rectangle(currentLeft - 5, ((lineIndex + 1) * lineSpace - stringHeight), stringWidth + 10, (int) (1.5 * stringHeight)));
            if (word.isSelected()){
                g.setColor(Color.BLUE);
                g.drawRect(word.getArea().x, word.getArea().y, word.getArea().width, word.getArea().height);
            }
            g.setColor(Color.RED);
            switch (layerType){
                case NER:
                    if (word.getNamedEntityType() != null){
                        correct = word.getNamedEntityType().toString();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case INFLECTIONAL_GROUP:
                    if (word.getParse() != null){
                        for (int j = 0; j < word.getParse().size(); j++){
                            g.drawString(word.getParse().getInflectionalGroupString(j), currentLeft, (lineIndex + 1) * lineSpace + 30 * (j + 1));
                        }
                    }
                    break;
                case SEMANTICS:
                    if (word.getSemantic() != null){
                        correct = word.getSemantic();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case PROPBANK:
                    if (word.getArgument() != null){
                        correct = word.getArgument().getArgumentType();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case FRAMENET:
                    if (word.getFrameElement() != null){
                        correct = word.getFrameElement().getFrameElementType();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case SLOT:
                    if (word.getSlot() != null){
                        correct = word.getSlot().toString();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case POLARITY:
                    if (word.getPolarity() != null){
                        correct = word.getPolarity().toString();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case SHALLOW_PARSE:
                    if (word.getShallowParse() != null){
                        correct = word.getShallowParse();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case POS_TAG:
                    if (word.getPosTag() != null){
                        correct = word.getPosTag();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
                case DEPENDENCY:
                    if (word.getUniversalDependencyPos() != null){
                        g.drawString(word.getUniversalDependencyPos(), currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    if (word.getUniversalDependency() != null){
                        correct = word.getUniversalDependency().toString().toLowerCase();
                        if (word.getUniversalDependency().to() != 0){
                            Color color = Color.BLACK;
                            switch (correct){
                                case "acl":
                                    color = Color.YELLOW;
                                    break;
                                case "advcl":
                                    color = new Color(0, 128, 128);
                                    break;
                                case "advmod":
                                    color = Color.BLUE;
                                    break;
                                case "amod":
                                    color = Color.RED;
                                    break;
                                case "aux":
                                    color = new Color(0, 0, 128);
                                    break;
                                case "case":
                                    color = Color.WHITE;
                                    break;
                                case "cc":
                                    color = new Color(128, 0, 0);
                                    break;
                                case "ccomp":
                                    color = new Color(255, 215, 0);
                                    break;
                                case "compound":
                                    color = Color.LIGHT_GRAY;
                                    break;
                                case "conj":
                                    color = new Color(128, 128, 0);
                                    break;
                                case "det":
                                    color = Color.PINK;
                                    break;
                                case "flat":
                                    color = new Color(128, 0, 128);
                                    break;
                                case "mark":
                                    color = new Color(255, 127, 80);
                                    break;
                                case "nmod":
                                    color = Color.ORANGE;
                                    break;
                                case "nsubj":
                                    color = Color.CYAN;
                                    break;
                                case "nummod":
                                    color = Color.GRAY;
                                    break;
                                case "obj":
                                    color = Color.GREEN;
                                    break;
                                case "obl":
                                    color = Color.MAGENTA;
                                    break;
                                case "xcomp":
                                    color = new Color(184, 134, 11);
                                    break;
                            }
                            g.setColor(color);
                            int startX = currentLeft + maxSize / 2;
                            int startY = lineIndex * lineSpace + 50;
                            double height = Math.pow(Math.abs(word.getUniversalDependency().to() - 1 - i), 0.7);
                            int toX = wordTotal.get(word.getUniversalDependency().to() - 1) + wordSize.get(word.getUniversalDependency().to() - 1) / 2 /*+ added*/;
                            pointEnd = new Point2D.Double(startX + 5 * Math.signum(word.getUniversalDependency().to() - 1 - i), startY);
                            pointStart = new Point2D.Double(toX, startY);
                            int controlY = (int) (pointStart.y - 20 - 20 * height);
                            g.drawString(correct, ((int) (pointStart.x + pointEnd.x) / 2) - g.getFontMetrics().stringWidth(correct) / 2, (int) (controlY + 30 + 4 * height));
                            pointCtrl1 = new Point2D.Double(pointStart.x, controlY);
                            pointCtrl2 = new Point2D.Double(pointEnd.x, controlY);
                            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
                            Graphics2D g2 = (Graphics2D)g;
                            g2.setColor(color);
                            g2.draw(cubicCurve);
                            g.drawOval((int) pointStart.x - 2, (int) pointStart.y - 2, 4, 4);
                            g.drawLine((int) pointEnd.x, (int) pointEnd.y, (int) pointEnd.x - 5, (int) pointEnd.y - 5);
                            g.drawLine((int) pointEnd.x, (int) pointEnd.y, (int) pointEnd.x + 5, (int) pointEnd.y - 5);
                        } else {
                            g.drawString("root", currentLeft + maxSize / 2 - g.getFontMetrics().stringWidth("root") / 2, lineIndex * lineSpace);
                            g.setColor(Color.BLACK);
                            g.drawLine(currentLeft + maxSize / 2, lineIndex * lineSpace + 50, currentLeft + maxSize / 2, lineIndex * lineSpace + 15);
                        }
                    }
                    break;
            }
            currentLeft += maxSize + wordSpace;
        }
        if (layerType == ViewLayerType.DEPENDENCY){
            setPreferredSize(new Dimension(currentLeft, (int) getPreferredSize().getHeight()));
        } else {
            setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), (lineIndex + 2) * lineSpace));
        }
        getParent().invalidate();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArea().contains(e.getX(), e.getY())){
                word.setSelected(true);
                if (i != selectedWordIndex){
                    if (selectedWordIndex != -1){
                        ((AnnotatedWord)sentence.getWord(i)).setSelected(false);
                    }
                }
                selectedWordIndex = i;
                repaint();
                return;
            }
        }
        if (selectedWordIndex != -1){
            ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
            selectedWordIndex = -1;
            if (!editText.isVisible()){
                clickedWord = null;
            }
            pane.setVisible(false);
            repaint();
        }
    }

    protected int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        return -1;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        int selectedIndex;
        if (selectedWordIndex != -1){
            if (mouseEvent.isControlDown()){
                clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
                lastClickedWord = clickedWord;
                editText.setText(clickedWord.getName());
                editText.setBounds(clickedWord.getArea().x - 5, clickedWord.getArea().y + 20, 100, 30);
                editText.setVisible(true);
                pane.setVisible(false);
                editText.requestFocus();
            } else {
                selectedIndex = populateLeaf(sentence, selectedWordIndex);
                if (selectedIndex != -1){
                    list.setValueIsAdjusting(true);
                    list.setSelectedIndex(selectedIndex);
                }
                editText.setVisible(false);
                if (layerType != ViewLayerType.SEMANTICS){
                    list.setVisible(true);
                }
                clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
                lastClickedWord = clickedWord;
                pane.setVisible(true);
                pane.getVerticalScrollBar().setValue(0);
                if (layerType == ViewLayerType.PROPBANK || layerType == ViewLayerType.INFLECTIONAL_GROUP || layerType == ViewLayerType.SLOT){
                    pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
                } else {
                    if (layerType == ViewLayerType.POLARITY){
                        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 120, 80);
                    } else {
                        if (layerType != ViewLayerType.SEMANTICS){
                            pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().height, 120, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
                        }
                    }
                }
                this.repaint();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
