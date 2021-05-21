package DataCollector.ParseTree.AutoTransfer;

import AnnotatedSentence.ViewLayerType;
import Corpus.Sentence;
import AnnotatedTree.*;
import AnnotatedTree.Processor.NodeModification.ConvertToLayeredFormat;
import AnnotatedTree.Processor.TreeModifier;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.ParseTree.TreeStructureEditorFrame;
import Translation.Tree.Rule.AutoTransfer.TransferredSentence;
import Translation.Tree.Rule.AutoTransfer.TurkishAutoTransfer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TreeAutoTransferFrame extends TreeStructureEditorFrame {
    private TransferredSentence currentSentence;

    public TreeAutoTransferFrame(){
        this.setTitle("AutoTransfer");
        itemOpenMultiple.setVisible(false);
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(TreeEditorPanel.englishPath));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = new File(TreeEditorPanel.treePath2 + "/" + fcinput.getSelectedFile().getName());
                if (!f.exists()) {
                    ParseTreeDrawable parseTree = new ParseTreeDrawable(TreeEditorPanel.englishPath, fcinput.getSelectedFile().getName());
                    TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
                    treeModifier.modify();
                    TurkishAutoTransfer turkishAutoTransfer = new TurkishAutoTransfer();
                    currentSentence = new TransferredSentence(new File(parseTree.getFileDescription().getFileName(TreeEditorPanel.phrasePath)));
                    turkishAutoTransfer.autoTransfer(parseTree, currentSentence);
                    parseTree.saveWithPath(TreeEditorPanel.treePath2);
                }
                TreeAutoTransferPanel autoTransferPanel = new TreeAutoTransferPanel(TreeEditorPanel.treePath2, fcinput.getSelectedFile().getName(), ViewLayerType.TURKISH_WORD);
                addPanelToFrame(autoTransferPanel, fcinput.getSelectedFile().getName());
                updateInfo();
            }
        });
    }

    public void updateInfo(){
        TreeAutoTransferPanel current = (TreeAutoTransferPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null){
            Sentence targetSentence = new Sentence(current.getTargetSentence());
            currentSentence = current.getCurrentSentence();
            String html = "<html>";
            for (int i = 0; i < currentSentence.wordCount(); i++){
                if (targetSentence.getIndex(currentSentence.getWord(i)) != -1){
                    html = html + "<font color=\"blue\">" + currentSentence.getWord(i) + " </font>";
                } else {
                    html = html + "<font color=\"red\">" + currentSentence.getWord(i) + " </font>";
                }
            }
            infoTop.setText(html);
            infoBottom.setForeground(Color.MAGENTA);
            infoBottom.setText("<html>" + current.getSourceSentence() + "</html>");
        }
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return null;
    }

    protected void nextTree(int count){
        super.nextTree(count);
        updateInfo();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        updateInfo();
    }

    protected void moveLeft(){
        super.moveLeft();
        updateInfo();
    }

    protected void moveRight(){
        super.moveRight();
        updateInfo();
    }

}
