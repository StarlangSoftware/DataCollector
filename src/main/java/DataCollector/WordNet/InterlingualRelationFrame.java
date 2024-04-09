package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.WordNet;
import WordNet.Annotation.InterlingualRelationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * <p>Interlingual relations and matching have great importance in the development of WordNets since creating these
 * relations and linking the WordNets of different relations provide us with an important resource in many areas like
 * machine translation. Therefore, an editor that works interlingually is a crucial tool in creating internationally
 * applicable and useful resources and connecting the created WordNets to each other.</p>
 *
 * <p>StarNet WordNet editor has an interface that enables inter-lingual matching. In creating KeNet, a merge approach
 * is used and synsets in KeNet and PWN are matched as a result of this merging process. Both the synset matches and
 * possible multilingual relations are checked by human annotators. The synset groups created in this process are
 * transferred to the Interlingual Matcher to view and edit the matches.</p>
 *
 * <p>The Interlingual Matcher is used by English PWN and Turkish KeNet data and matched synsets one-to-one between
 * the languages by human annotators. As a result of this process, the existing matches can be checked and confirmed,
 * and new matches can be created when needed. This process is potentially applicable to all languages via the
 * Interlingual Matcher.</p>
 *
 * <p>The Interlingual Matcher interface is quite similar to the Literal Matcher’s interface and is easy to understand.
 * The tag-save mode is active for the Interlingual Matcher as well. Unlike the Literal Matcher, however, only
 * one-to-one matching is offered in the Interlingual Matcher: For each English word, suggested synonyms from the
 * other language can be chosen and tagged</p>
 */
public class InterlingualRelationFrame extends DataCollector{

    /**
     * Constructs a frame in which the annotators match interlingual relations between two wordnets. The system shows
     * the users two panels. On the left side, there are all synsets containing the first literal from first wordnet
     * ; and on the right side, there are all synsets containing the second literal from second wordnet. The user then
     * matches one synset from left and one synset from right which have the same meaning.
     * The menu consists of
     * <p> itemOpen: The user selects a file containing possible interlingual relation candidates. The user then selects or
     * deselect the candidates. The user also traverses the candidate list either by pressing next or previous buttons, or
     * by pressing random button. </p>
     * <p>itemNext: The program shows next possible candidate.</p>
     * <p> itemPrevious: The program shows the previous possible candidate.</p>
     * <p>itemRandom: The program shows a random possible candidate.</p>
     * @param wordNet1 First wordnet, for which the interlingual relation is annotated with the second wordnet. In our
     *                 case, this is the Turkish wordnet.
     * @param wordNet2 Second wordnet, which contains synsets those are related with the synsets in the first wordnet.
     *                 In our case, this is English wordnet, and possibly other wordnets.
     */
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

    /**
     * Displays next interlingual relation (a candidate)
     */
    private void nextInterlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextRelationCandidate());
        }
    }

    /**
     * Displays previous interlingual relation (a candidate)
     */
    private void previousInterlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousRelationCandidate());
        }
    }

    /**
     * Displays a random interlingual relation (a candidate)
     */
    private void randomInyerlingualRelationCandidate(){
        InterlingualRelationPanel current = (InterlingualRelationPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.randomRelationCandidate());
        }
    }

    /**
     * Calls corresponding methods for different buttons or menu items.
     * @param e {@link ActionEvent} input.
     */
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
