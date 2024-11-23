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

    /**
     * Current sentence displayed in the panel.
     */
    public AnnotatedSentence sentence;

    protected FileDescription fileDescription;
    protected int wordSpace = 60, lineSpace;
    protected int selectedWordIndex = -1, draggedWordIndex = -1;
    protected boolean selectionMode = false;
    protected AnnotatedWord clickedWord = null, lastClickedWord = null;

    /**
     * The sentence is displayed according to the given viewerLayer. If viewerLayer is PART_OF_SPEECH or
     * INFLECTIONAL_GROUP, the sentence will show the morphological analysis of the word.  If viewerLayer is SEMANTICS,
     * the sentence will show the semantic id of the Turkish word according to the WordNet. If viewerLayer is NER,
     * the sentence will show the named entity tag of the Turkish word. If viewerLayer is DEPENDENCY, the sentence
     * will show the Dependency information of the Turkish  word according to the Dependency
     * annotation.
     */
    protected ViewLayerType layerType;

    protected JList list;
    protected DefaultListModel listModel;
    protected JScrollPane pane;

    protected abstract void setWordLayer();
    protected abstract int getMaxLayerLength(AnnotatedWord word, Graphics g);
    protected abstract void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal);
    protected abstract void setBounds();
    protected abstract void setLineSpace();

    /**
     * Constructs base annotator panel which is the base panel for all possible annotations such as NER, SRL, WSD,
     * Morphological Disambiguation, etc. The method first read the annotated sentence, then constructs
     * the default option list, which shows options for NER, SRL, Morphological Analysis, WSD. Then the method
     * constructs the edit text, which is used for modifying a single word (also possibly inserting a word)
     * @param currentPath The absolute path for the annotated file.
     * @param rawFileName The raw file name of the annotated file.
     * @param layerType The layerType shows the annotation layer for which this panel is constructed.
     */
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

    /**
     * Displays the previous sentence according to the index of the sentence. For example, if the current
     * sentence fileName is 0123.train, after the call of previous(4), the panel will display 0119.train. If the
     * previous sentence does not exist, nothing will happen.
     * @param count Number of sentences to go backward
     */
    public void previous(int count) {
        if (fileDescription.previousFileExists(count)){
            fileDescription.addToIndex(-count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            pane.setVisible(false);
            repaint();
        }
    }

    /**
     * Displays the next sentence according to the index of the sentence. For example, if the
     * current sentence fileName is 0123.train, after the call of nextT(3), panel will display 0126.train. If the next
     * sentence does not exist, nothing will happen.
     * @param count Number of sentences to go forward
     */
    public void next(int count) {
        if (fileDescription.nextFileExists(count)){
            fileDescription.addToIndex(count);
            sentence = new AnnotatedSentence(new File(fileDescription.getFileName()));
            pane.setVisible(false);
            repaint();
        }
    }

    /**
     * If this annotated corpus has also a parallel English treebank corpus, this method returns the parallel tree as
     * a sentence string. The sentence is read from the English path with the same raw file name. Currently, NlpToolkit
     * supports only one source language, namely English.
     * @return Parallel source string in English.
     */
    public String getSourceSentence(){
        ParseTreeDrawable englishTree = new ParseTreeDrawable(TreeEditorPanel.englishPath, fileDescription);
        if (englishTree.getRoot() != null){
            return englishTree.toSentence();
        } else {
            return "";
        }
    }

    /**
     * Sets the space between annotated words displayed in the current panel
     * @param wordSpace New space width between annotated words.
     */
    public void setWordSpace(int wordSpace){
        this.wordSpace = wordSpace;
    }

    /**
     * If this annotated corpus has also a parallel sentence corpus, this method returns the parallel sentence as a
     * string. The sentence is read from the 'Original' path.
     * @return Parallel sentence string.
     */
    public String getOriginalSentence(){
        AnnotatedSentence originalSentence = new AnnotatedSentence(new File(TreeEditorPanel.originalPath + fileDescription.getRawFileName()));
        return originalSentence.toWords();
    }

    /**
     * Getter for the lastClickedWord.
     * @return lastClickedWord.
     */
    public AnnotatedWord getClickedWord(){
        return lastClickedWord;
    }

    /**
     * Returns the raw file name of annotated file associated with this panel.
     * @return Raw file name of annotated file associated with this panel.
     */
    public String getRawFileName(){
        return fileDescription.getRawFileName();
    }

    /**
     * Draws the annotated sentence on the panel. If the layer is dependency, draws the sentence  in one line.
     * Otherwise, draws the sentence in multiple lines if the width of the sentence does not fit to the length of the
     * screen. If the word is selected, prints a blue rectangle surrounding the word. If the layer is propbank and if
     * the shallow parse layer is annotated, draws separate shallow parse groups in bold and not bold interchangeably.
     * The function also calls abstract methods:
     * <p> drawLayer: to draw extra information for layer associated with this panel. </p>
     * <p> setLineSpace: to calculate the space between multiple lines, depending on the layer associated with this
     * panel.</p>
     * <p> getMaxLayerLength: to get the width of the layer info in pixels.</p>
     *
     * @param g Graphics to paint on.
     */
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

    /**
     * Base mouse event handling procedure for selecting and deselecting a word. If the mouse is in the area of a word,
     * it will be selected by setting selectedWordIndex to that word's index. Also, previously selected word's selected
     * property is set to false. If the mouse is not on one of the word's area, previously selected word becomes
     * unselected.
     * @param e Mouse event to be processed.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArea().contains(e.getX(), e.getY())){
                word.setSelected(true);
                if (i != selectedWordIndex){
                    if (selectedWordIndex != -1 && !e.isShiftDown()){
                        ((AnnotatedWord)sentence.getWord(i)).setSelected(false);
                    }
                }
                selectedWordIndex = i;
                repaint();
                return;
            }
        }
        if (selectedWordIndex != -1){
            if (!e.isShiftDown()){
                for (int i = 0; i < sentence.wordCount(); i++) {
                    ((AnnotatedWord)sentence.getWord(i)).setSelected(false);
                }
            }
            selectedWordIndex = -1;
            if (!editText.isVisible()){
                clickedWord = null;
            }
            pane.setVisible(false);
            repaint();
        }
    }

    /**
     * Base method for populating the list. It does nothing.
     * @param sentence Sentence used to populate for the current word.
     * @param wordIndex Index of the selected word.
     * @return Index of the selected item in the list.
     */
    protected int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        return -1;
    }

    /**
     * Base mouse event handling procedure for clicking a word. There are two possibilities:
     * <p>If the mouse is clicked without control key is pressed, the list box with possible options is displayed.</p>
     * <p>If the mouse is clicked with control key is pressed, the EditText is displayed with the word in it.</p>
     * @param mouseEvent Mouse event to be processed.
     */
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
