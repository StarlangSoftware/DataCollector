package DataCollector.Sentence;

import AnnotatedSentence.*;
import Classification.Instance.Instance;
import Classification.Model.Model;
import Corpus.FileDescription;
import AnnotatedSentence.AnnotatedWord;
import MorphologicalAnalysis.FsmParse;
import DataCollector.ParseTree.EditorPanel;
import DataGenerator.InstanceGenerator.InstanceGenerator;
import DataGenerator.InstanceGenerator.InstanceNotGenerated;
import AnnotatedTree.ParseTreeDrawable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public abstract class AnnotatorPanel extends JPanel implements MouseListener, MouseMotionListener {
    private JTextField editText;
    protected AnnotatedSentence sentence;
    protected FileDescription fileDescription;
    protected int wordSpace = 60, lineSpace;
    protected int selectedWordIndex = -1;
    protected AnnotatedWord clickedWord = null, lastClickedWord = null;
    protected ViewLayerType layerType;
    protected JList list;
    protected DefaultListModel listModel;
    protected JScrollPane pane;

    public AnnotatorPanel(String currentPath, String rawFileName, final ViewLayerType layerType){
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
                        case SHALLOW_PARSE:
                            clickedWord.setShallowParse((String) list.getSelectedValue());
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
        ParseTreeDrawable englishTree = new ParseTreeDrawable(EditorPanel.ENGLISH_PATH, fileDescription);
        if (englishTree.getRoot() != null){
            return englishTree.toSentence();
        } else {
            return "";
        }
    }

    public String getOriginalSentence(){
        AnnotatedSentence originalSentence = new AnnotatedSentence(new File(EditorPanel.ORIGINAL_PATH + fileDescription.getRawFileName()));
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
            case SHALLOW_PARSE:
                if (word.getShallowParse() != null){
                    size = g.getFontMetrics().stringWidth(word.getShallowParse());
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
        AnnotatedWord previousWord = null, word;
        int lineIndex = 0, currentLeft = wordSpace;
        boolean bold = false;
        Font currentFont = g.getFont();
        String prediction, correct = null;
        setLineSpace();
        for (int i = 0; i < sentence.wordCount(); i++){
            if (i > 0){
                previousWord = (AnnotatedWord) sentence.getWord(i - 1);
            }
            word = (AnnotatedWord) sentence.getWord(i);
            prediction = null;
            int maxSize = maxLayerLength(word, g);
            int stringWidth = g.getFontMetrics().stringWidth(word.getName());
            int stringHeight = (int) g.getFontMetrics().getStringBounds(word.getName(), g).getHeight();
            if (maxSize + currentLeft >= getWidth()){
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
                case SHALLOW_PARSE:
                    if (word.getShallowParse() != null){
                        correct = word.getShallowParse();
                        g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
                    }
                    break;
            }
            if (prediction != null){
                if (correct != null && correct.equals(prediction)){
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.MAGENTA);
                }
                g.drawString(prediction, currentLeft, (lineIndex + 1) * lineSpace + 45);
            }
            currentLeft += maxSize + wordSpace;
        }
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), (lineIndex + 2) * lineSpace));
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
                if (layerType == ViewLayerType.PROPBANK || layerType == ViewLayerType.INFLECTIONAL_GROUP){
                    pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, 30 + Math.max(3, Math.min(15, list.getModel().getSize())) * 18);
                } else {
                    if (layerType != ViewLayerType.SEMANTICS){
                        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().height, 120, 30 + Math.max(3, Math.min(15, list.getModel().getSize())) * 18);
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
