package DataCollector.Sentence;

import AnnotatedSentence.*;
import Corpus.FileDescription;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.ParseTree.TreeEditorPanel;
import AnnotatedTree.ParseTreeDrawable;
import Util.RectAngle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    protected abstract void setWordLayer();
    protected abstract int getMaxLayerLength(AnnotatedWord word, Graphics g);
    protected abstract void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal);
    protected abstract void setBounds();
    protected abstract void setLineSpace();

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
                    setWordLayer();
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
                String newText = editText.getText();
                if (!newText.contains(" ")){
                    clickedWord.setName(newText);
                } else {
                    int index = -1;
                    for (int i = 0; i < sentence.wordCount(); i++){
                        if (sentence.getWord(i) == clickedWord){
                            index = i;
                            break;
                        }
                    }
                    if (index != -1){
                        sentence.insertWord(newText, clickedWord, index);
                    }
                }
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

    public void setWordSpace(int wordSpace){
        this.wordSpace = wordSpace;
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

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        AnnotatedWord previousWord = null, word;
        int lineIndex, currentLeft = wordSpace, maxSize;
        ArrayList<Integer> wordSize = new ArrayList<>();
        ArrayList<Integer> wordTotal = new ArrayList<>();
        if (layerType == ViewLayerType.DEPENDENCY){
            for (int i = 0; i < sentence.wordCount(); i++) {
                wordTotal.add(currentLeft);
                word = (AnnotatedWord) sentence.getWord(i);
                maxSize = getMaxLayerLength(word, g);
                wordSize.add(maxSize);
                currentLeft += maxSize + wordSpace;
            }
            lineIndex = 1;
        } else {
            lineIndex = 0;
        }
        boolean bold = false;
        Font currentFont = g.getFont();
        setLineSpace();
        currentLeft = wordSpace;
        for (int i = 0; i < sentence.wordCount(); i++){
            if (i > 0){
                previousWord = (AnnotatedWord) sentence.getWord(i - 1);
            }
            word = (AnnotatedWord) sentence.getWord(i);
            maxSize = getMaxLayerLength(word, g);
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
            word.setArea(new RectAngle(currentLeft - 5, ((lineIndex + 1) * lineSpace - stringHeight), stringWidth + 10, (int) (1.5 * stringHeight)));
            if (word.isSelected()){
                g.setColor(Color.BLUE);
                g.drawRect(word.getArea().getX(), word.getArea().getY(), word.getArea().getWidth(), word.getArea().getHeight());
            }
            g.setColor(Color.RED);
            drawLayer(word, g, currentLeft, lineIndex, i, maxSize, wordSize, wordTotal);
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
                editText.setBounds(clickedWord.getArea().getX() - 5, clickedWord.getArea().getY() + 20, 100, 30);
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
                setBounds();
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
