package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SynSetMatcherFrame extends DataCollector {

    public SynSetMatcherFrame(final WordNet wordNet){
        this.setTitle("SynSet Matcher");
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
            fcinput.setCurrentDirectory(new File("./components"));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                SynSetMatcherPanel synSetMatcherPanel = new SynSetMatcherPanel(wordNet, fcinput.getSelectedFile().getParent() + "/" + fcinput.getSelectedFile().getName());
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(synSetMatcherPanel);
                projectPane.add(scrollPane, fcinput.getSelectedFile().getName(), projectPane.getSelectedIndex() + 1);
                enableMenu();
                synSetMatcherPanel.setFocusable(true);
            }
        });
        itemSave.addActionListener(e -> {
            SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            if (current != null){
                current.save();
            }
        });
        itemNext.addActionListener(e -> nextSynSetCandidate());
        itemPrevious.addActionListener(e -> previousSynSetCandidate());
        itemRandom.addActionListener(e -> randomSynSetCandidate());
    }

    private void nextSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextSynSetCandidate());
        }
    }

    private void previousSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousSynSetCandidate());
        }
    }

    private void randomSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.randomSynSetCandidate());
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case BACKWARD:
                previousSynSetCandidate();
                break;
            case FORWARD:
                nextSynSetCandidate();
                break;
            case RANDOM:
                randomSynSetCandidate();
                break;
        }
    }

}
