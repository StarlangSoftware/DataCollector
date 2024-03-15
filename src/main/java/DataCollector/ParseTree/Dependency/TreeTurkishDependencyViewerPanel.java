package DataCollector.ParseTree.Dependency;

import DependencyParser.Turkish.TurkishDependencyTreeBankCorpus;
import DependencyParser.Turkish.TurkishDependencyTreeBankSentence;
import DependencyParser.Turkish.TurkishDependencyTreeBankWord;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class TreeTurkishDependencyViewerPanel extends JPanel {

    private final TurkishDependencyTreeBankCorpus corpus;
    private int currentIndex;
    private TurkishDependencyTreeBankSentence currentSentence;
    public static final int WORD_WIDTH = 150;
    public static final int RELATION_HEIGHT = 30;

    public TreeTurkishDependencyViewerPanel(String fileName){
        corpus = new TurkishDependencyTreeBankCorpus(fileName);
        currentIndex = 0;
        currentSentence = (TurkishDependencyTreeBankSentence) corpus.getSentence(currentIndex);
    }

    public void nextSentence(int count){
        if (currentIndex < corpus.sentenceCount() - count + 1){
            currentIndex += count;
            currentSentence = (TurkishDependencyTreeBankSentence) corpus.getSentence(currentIndex);
            repaint();
        }
    }

    public void previousSentence(int count){
        if (currentIndex > count - 1){
            currentIndex -= count;
            currentSentence = (TurkishDependencyTreeBankSentence) corpus.getSentence(currentIndex);
            repaint();
        }
    }

    public void gotoSentence(int index){
        if (index >= 0 && index < corpus.sentenceCount()){
            currentIndex = index;
            currentSentence = (TurkishDependencyTreeBankSentence) corpus.getSentence(currentIndex);
            repaint();
        }
    }

    private void paintWord(Graphics g, TurkishDependencyTreeBankWord word, int index, int y){
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        int relationHeight, stringSize;
        stringSize = g.getFontMetrics().stringWidth(word.getName());
        g.drawString(word.getName(), (int) ((index + 0.5) * TreeTurkishDependencyViewerPanel.WORD_WIDTH - 0.5 * stringSize), y);
        if (word.getRelation() != null){
            stringSize = g.getFontMetrics().stringWidth(word.getRelation().toString());
            relationHeight = (word.getRelation().to() - index) * TreeTurkishDependencyViewerPanel.RELATION_HEIGHT;
            pointStart = new Point2D.Double((index + .5) * TreeTurkishDependencyViewerPanel.WORD_WIDTH, y - Math.signum(word.getRelation().to() - index) * TreeTurkishDependencyViewerPanel.RELATION_HEIGHT / 2);
            pointCtrl1 = new Point2D.Double((index + .75) * TreeTurkishDependencyViewerPanel.WORD_WIDTH, y - relationHeight);
            pointCtrl2 = new Point2D.Double((word.getRelation().to() + .25) * TreeTurkishDependencyViewerPanel.WORD_WIDTH, y - relationHeight);
            pointEnd = new Point2D.Double((word.getRelation().to() + .5) * TreeTurkishDependencyViewerPanel.WORD_WIDTH, y - Math.signum(word.getRelation().to() - index) * TreeTurkishDependencyViewerPanel.RELATION_HEIGHT / 2);
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.draw(cubicCurve);
            g.drawString(word.getRelation().toString(), (int) ((pointStart.x + pointEnd.x - stringSize) / 2), y - relationHeight - 5);
        }
    }

    private void paintSentence(Graphics g){
        for (int i = 0; i < currentSentence.wordCount(); i++){
            TurkishDependencyTreeBankWord word = (TurkishDependencyTreeBankWord) currentSentence.getWord(i);
            paintWord(g, word, i, (currentSentence.maxDependencyLength() + 1) * TreeTurkishDependencyViewerPanel.RELATION_HEIGHT);
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        setPreferredSize(new Dimension(currentSentence.wordCount() * WORD_WIDTH, getHeight()));
        paintSentence(g);
    }

}
