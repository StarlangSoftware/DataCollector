package DataCollector.Sentence;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.ViewLayerType;
import DependencyParser.UniversalDependencyType;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class SentenceDependencyPanel extends AnnotatorPanel {
    private boolean dragged = false;
    private int dragX = -1, dragY = -1;

    public SentenceDependencyPanel(String currentPath, String rawFileName) {
        super(currentPath, rawFileName, ViewLayerType.DEPENDENCY);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (draggedWordIndex != -1) {
            int selectedIndex = populateLeaf(sentence, selectedWordIndex);
            if (selectedIndex != -1){
                list.setValueIsAdjusting(true);
                list.setSelectedIndex(selectedIndex);
            }
            list.setVisible(true);
            pane.setVisible(true);
            pane.getVerticalScrollBar().setValue(0);
            pane.setBounds((((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().x + ((AnnotatedWord) sentence.getWord(draggedWordIndex)).getArea().x) / 2, ((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().y + 100, 80, 120);
        }
        ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
        dragged = false;
        selectionMode = true;
        dragX = -1;
        dragY = -1;
        this.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        if (!selectionMode){
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
                    clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
                    repaint();
                    return;
                }
            }
            if (selectedWordIndex != -1){
                ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
                selectedWordIndex = -1;
                repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        selectionMode = false;
        list.setVisible(false);
        pane.setVisible(false);
    }

    public void mouseDragged(MouseEvent e) {
        dragged = true;
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArea().contains(e.getX(), e.getY())){
                if (i != selectedWordIndex){
                    draggedWordIndex = i;
                    repaint();
                    return;
                }
            }
        }
        if (selectedWordIndex != -1){
            draggedWordIndex = -1;
            dragX = e.getX();
            dragY = e.getY();
            this.repaint();
        }
    }

    protected void paintComponent(Graphics g){
        int startX, startY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        super.paintComponent(g);
        if (dragged && selectedWordIndex != -1){
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            startX = selectedWord.getArea().x + selectedWord.getArea().width / 2;
            startY = selectedWord.getArea().y + 20;
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
            g2.setColor(Color.MAGENTA);
            g2.draw(cubicCurve);
        }
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        listModel.clear();
        AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
        for (int i = 0; i < UniversalDependencyType.values().length; i++){
            if (selectedWord.getUniversalDependency() != null && selectedWord.getUniversalDependency().toString().equalsIgnoreCase(UniversalDependencyType.values()[i].toString())){
                selectedIndex = i;
            }
            listModel.addElement(UniversalDependencyType.values()[i].toString());
        }
        return selectedIndex;
    }

}
