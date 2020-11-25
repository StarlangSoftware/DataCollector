package DataCollector.ParseTree.Dependency;

import DataCollector.DataCollector;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TreeTurkishDependencyViewerFrame extends DataCollector{

    public TreeTurkishDependencyViewerFrame(){
        JButton button;
        button = new DrawingButton(DataCollector.class, this, "fastfastbackward", FAST_FAST_BACKWARD, "Previous 100 Tree");
        button.setVisible(true);
        toolBar.add(button, 0);
        button = new DrawingButton(DataCollector.class, this, "fastbackward", FAST_BACKWARD, "Previous 10 Tree");
        button.setVisible(true);
        toolBar.add(button, 1);
        button = new DrawingButton(DataCollector.class, this, "fastforward", FAST_FORWARD, "Next 10 Tree");
        button.setVisible(true);
        toolBar.add(button, 4);
        button = new DrawingButton(DataCollector.class, this, "fastfastforward", FAST_FAST_FORWARD, "Next 100 Tree");
        button.setVisible(true);
        toolBar.add(button, 5);
        JMenu sentenceMenu = new JMenu("Tree");
        menu.add(sentenceMenu);
        JMenuItem itemNext = addMenuItem(sentenceMenu, "Next Tree", KeyStroke.getKeyStroke('s'));
        JMenuItem itemPrevious = addMenuItem(sentenceMenu, "Previous Tree", KeyStroke.getKeyStroke('w'));
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                TreeTurkishDependencyViewerPanel turkishDependencyViewerPanel = new TreeTurkishDependencyViewerPanel(fcinput.getSelectedFile().getAbsolutePath());
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(turkishDependencyViewerPanel);
                projectPane.add(scrollPane, fcinput.getSelectedFile().getName(), projectPane.getSelectedIndex() + 1);
                enableMenu();
            }
        });
        itemNext.addActionListener(e -> nextSentence(1));
        itemPrevious.addActionListener(e -> previousSentence(1));
    }

    private void nextSentence(int count){
        TreeTurkishDependencyViewerPanel current = (TreeTurkishDependencyViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.nextSentence(count);
        }
    }

    private void previousSentence(int count){
        TreeTurkishDependencyViewerPanel current = (TreeTurkishDependencyViewerPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            current.previousSentence(count);
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()){
            case BACKWARD:
                previousSentence(1);
                break;
            case FORWARD:
                nextSentence(1);
                break;
            case FAST_BACKWARD:
                previousSentence(10);
                break;
            case FAST_FORWARD:
                nextSentence(10);
                break;
            case FAST_FAST_BACKWARD:
                previousSentence(100);
                break;
            case FAST_FAST_FORWARD:
                nextSentence(100);
                break;
        }
    }

}
