package DataCollector.WordNet;

import DataCollector.DataCollector;
import Util.DrawingButton;
import WordNet.Annotation.LiteralMatcherPanel;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p> The Literal Matcher is a tool enabling synonym literal matching in the target languages. This interface
 * offers many facilities such as presenting every sense definition of a unique literal, convenient editing and
 * a quick tag-save mode, which saves processes as soon as literals are matched, without further operation.
 * Synonym candidates will appear in n groups in this component. While the tool is easy to use and practical in
 * many ways, checking multiple meanings and synonyms in every step can decrease the speed of the matching
 * process.</p>
 *
 * <p> The Literal Matcher is a practical option for matching intralingual synonym literals. However, transitivity
 * may cause problems as a result of multi-matching. Even if the first literal and the second literal sense
 * definitions are completely synonymous, when these literal matches are prolonged, the first literal definition
 * and the fourth/fifth literal definitions may not be exactly synonymous. As a solution to this problem, StarNet
 * presents the editor Synset Matcher. Such overgrown synsets with weak or absent synonym relations between its
 * literals can be viewed and edited in the Synset Matcher by using split/merge processes.</p>
 */
public class LiteralMatcherFrame extends DataCollector{

    /**
     * Constructs a frame in which the annotators match literals in a wordnet for possibly including as synonyms. The
     * system shows the users n panels. On panel i, there are all synsets containing the literal i from  wordnet
     * The user then matches all synsets from all panels which have the same meaning.
     * The menu consists of
     * <p> itemOpen: The user selects a file containing possible literals having the same meaning. The user then
     * selects or deselect the candidates. The user also traverses the candidate list either by pressing next or
     * previous buttons, or by pressing random button. </p>
     * <p>itemNext: The program shows next possible candidate.</p>
     * <p> itemPrevious: The program shows the previous possible candidate.</p>
     * <p>itemRandom: The program shows a random possible candidate.</p>
     * @param wordNet The wordnet for which the user will match literals for synsets.
     */
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

    /**
     * Displays next synset (a candidate)
     */
    private void nextSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.nextSynSetCandidate());
        }
    }

    /**
     * Displays previous synset (a candidate)
     */
    private void previousSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            infoBottom.setText(current.previousSynSetCandidate());
        }
    }

    /**
     * Displays random synset (a candidate)
     */
    private void randomSynSetCandidate(){
        LiteralMatcherPanel current = (LiteralMatcherPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
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
