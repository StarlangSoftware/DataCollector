package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LiteralMatcherFrame extends DataCollector{

    public LiteralMatcherFrame(final WordNet wordNet){
        this.setTitle("Literal Matcher");
        JButton button = new DrawingButton(DataCollector.class, this, "random", RANDOM, "Random Tree");
        button.setVisible(true);
        toolBar.add(button);
        JMenu synSetMenu = new JMenu("SynSet");
        menu.add(synSetMenu);
        JMenuItem itemNext = addMenuItem(synSetMenu, "Next SynSet Candidate", KeyStroke.getKeyStroke('s'));
        JMenuItem itemPrevious = addMenuItem(synSetMenu, "Previous SynSet Candidate", KeyStroke.getKeyStroke('w'));
        JMenuItem itemRandom = addMenuItem(synSetMenu, "Random SynSet Candidate", KeyStroke.getKeyStroke('r'));
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select synset candidates file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                LiteralMatcherPanel literalMatcherPanel = new LiteralMatcherPanel(wordNet, fcinput.getSelectedFile().getParent() + "/" + fcinput.getSelectedFile().getName());
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(literalMatcherPanel);
                projectPane.add(scrollPane, fcinput.getSelectedFile().getName(), projectPane.getSelectedIndex() + 1);
                enableMenu();
                literalMatcherPanel.setFocusable(true);
            }
        });
        itemSave.addActionListener(e -> {
            LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            if (current != null){
                current.save();
            }
        });
        itemNext.addActionListener(e -> nextSynSetCandidate());
        itemPrevious.addActionListener(e -> previousSynSetCandidate());
        itemRandom.addActionListener(e -> randomSynSetCandidate());
    }

    private void nextSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextSynSetCandidate());
        }
    }

    private void previousSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousSynSetCandidate());
        }
    }

    private void randomSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.randomSynSetCandidate());
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getActionCommand().equals(BACKWARD)){
            previousSynSetCandidate();
        } else {
            if (e.getActionCommand().equals(FORWARD)){
                nextSynSetCandidate();
            } else {
                if (e.getActionCommand().equals(RANDOM)){
                    randomSynSetCandidate();
                }
            }
        }
    }

}
