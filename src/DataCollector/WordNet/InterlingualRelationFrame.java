package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class InterlingualRelationFrame extends DataCollector{

    public InterlingualRelationFrame(final WordNet wordNet1, final WordNet wordNet2){
        this.setTitle("Interlingual Relation Editor");
        JButton button = new DrawingButton(DataCollector.class, this, "random", RANDOM, "Random Tree");
        button.setVisible(true);
        toolBar.add(button);
        JMenu relationMenu = new JMenu("Interlingual Relation");
        menu.add(relationMenu);
        JMenuItem itemNext = addMenuItem(relationMenu, "Next Interlingual Relation Candidate", KeyStroke.getKeyStroke('s'));
        JMenuItem itemPrevious = addMenuItem(relationMenu, "Previous Interlingual Relation Candidate", KeyStroke.getKeyStroke('w'));
        JMenuItem itemRandom = addMenuItem(relationMenu, "Random Interlingual Relation Candidate", KeyStroke.getKeyStroke('r'));
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select interlingual relation candidates file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File("./interlingua2"));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                InterlingualRelationPanel interlingualRelationPanel = new InterlingualRelationPanel(wordNet1, wordNet2, fcinput.getSelectedFile().getParent() + "/" + fcinput.getSelectedFile().getName());
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(interlingualRelationPanel);
                projectPane.add(scrollPane, fcinput.getSelectedFile().getName(), projectPane.getSelectedIndex() + 1);
                enableMenu();
                interlingualRelationPanel.setFocusable(true);
            }
        });
        itemSave.addActionListener(e -> {
            InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
            if (current != null){
                current.save();
            }
        });
        itemNext.addActionListener(e -> nextInterlingualRelationCandidate());
        itemPrevious.addActionListener(e -> previousInterlingualRelationCandidate());
        itemRandom.addActionListener(e -> randomInyerlingualRelationCandidate());
    }

    private void nextInterlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextRelationCandidate());
        }
    }

    private void previousInterlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousRelationCandidate());
        }
    }

    private void randomInyerlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.randomRelationCandidate());
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getActionCommand().equals(BACKWARD)){
            previousInterlingualRelationCandidate();
        } else {
            if (e.getActionCommand().equals(FORWARD)){
                nextInterlingualRelationCandidate();
            } else {
                if (e.getActionCommand().equals(RANDOM)){
                    randomInyerlingualRelationCandidate();
                }
            }
        }
    }

}
