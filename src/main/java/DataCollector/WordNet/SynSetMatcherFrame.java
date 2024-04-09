package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.WordNet;
import WordNet.Annotation.SynSetMatcherPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * <p> Creating synsets with synonym literals can be challenging especially when the mapping is overgrown, the
 * transitivity decreases. This process poses a problem in creating meaningful and accurate synsets. Here, the
 * Synset Matcher plays a crucial role as it enables us to view all the literals in synsets and merge/split the
 * synsets when necessary.</p>
 *
 * <p> The Synset Matcher receives data from the Literal Matcher and acts as a supportive editor. It provides
 * editing options for synonym literals in languages and provides an easy and practical interface to check the
 * synsets built in the Literal Matcher. It allows us to identify the different synsets that should be grouped
 * together because of their meanings and enables us to merge them. Similarly, any synsets whose literals
 * should be separated because of their unrelated definitions that are grouped together as a result of transitivity
 * problems or any other mistakes during the previous processes can be split via the Synset Matcher. The Synset
 * Matcher makes it possible to see the whole picture of a synset by showing us the final matching maps of all of
 * its literals and to prune the synset if need be. As a result of this mapping and editing process in the Synset
 * Matcher, we obtain the final version of synsets</p>
 */
public class SynSetMatcherFrame extends DataCollector {

    /**
     * Constructs a frame in which the annotators remove synsets from being part of a large synonym set. The system
     * shows the users n synsets. On panel i, there is the synset i, which is possibly synonym to other synsets.
     * The user then removes all synsets from all panels which do not have the same meaning (which are not synonyms).
     * The menu consists of
     * <p> itemOpen: The user selects a file containing possible literals having the same meaning. The user then
     * selects or deselect the candidates. The user also traverses the candidate list either by pressing next or
     * previous buttons, or by pressing random button. </p>
     * <p>itemNext: The program shows next possible candidate.</p>
     * <p> itemPrevious: The program shows the previous possible candidate.</p>
     * <p>itemRandom: The program shows a random possible candidate.</p>
     * @param wordNet The wordnet for which the user will match literals for synsets.
     */
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

    /**
     * Displays next synset set (a candidate)
     */
    private void nextSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextSynSetCandidate());
        }
    }

    /**
     * Displays previous synset set (a candidate)
     */
    private void previousSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousSynSetCandidate());
        }
    }

    /**
     * Displays random synset set (a candidate)
     */
    private void randomSynSetCandidate(){
        SynSetMatcherPanel current = (SynSetMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.randomSynSetCandidate());
        }
    }

    /**
     * Calls corresponding methods for different buttons or menu items.
     * @param e {@link ActionEvent} input.
     */
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
