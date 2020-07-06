package DataCollector.ParseTree;

import AnnotatedSentence.ViewLayerType;
import Corpus.Sentence;
import AnnotatedTree.*;
import AnnotatedTree.AutoProcessor.AutoTransfer.TransferredSentence;
import AnnotatedTree.AutoProcessor.AutoTransfer.TurkishAutoTransfer;
import AnnotatedTree.Processor.NodeModification.ConvertToLayeredFormat;
import AnnotatedTree.Processor.TreeModifier;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AutoTransferFrame extends StructureEditorFrame{
    private TransferredSentence currentSentence;

    public AutoTransferFrame(){
        this.setTitle("AutoTransfer");
        itemOpenMultiple.setVisible(false);
        itemOpen.addActionListener(e -> {
            final JFileChooser fcinput = new JFileChooser();
            fcinput.setDialogTitle("Select project file");
            fcinput.setDialogType(JFileChooser.OPEN_DIALOG);
            fcinput.setCurrentDirectory(new File(EditorPanel.ENGLISH_PATH));
            int returnVal = fcinput.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = new File(EditorPanel.treePath2 + "/" + fcinput.getSelectedFile().getName());
                if (!f.exists()) {
                    ParseTreeDrawable parseTree = new ParseTreeDrawable(EditorPanel.ENGLISH_PATH, fcinput.getSelectedFile().getName());
                    TreeModifier treeModifier = new TreeModifier(parseTree, new ConvertToLayeredFormat());
                    treeModifier.modify();
                    TurkishAutoTransfer turkishAutoTransfer = new TurkishAutoTransfer();
                    currentSentence = new TransferredSentence(new File(parseTree.getFileDescription().getFileName(EditorPanel.phrasePath)));
                    turkishAutoTransfer.autoTransfer(parseTree, currentSentence);
                    parseTree.saveWithPath(EditorPanel.treePath2);
                }
                AutoTransferPanel autoTransferPanel = new AutoTransferPanel(EditorPanel.treePath2, fcinput.getSelectedFile().getName(), ViewLayerType.TURKISH_WORD);
                addPanelToFrame(autoTransferPanel, fcinput.getSelectedFile().getName());
                updateInfo();
            }
        });
    }

    public void updateInfo(){
        AutoTransferPanel current = (AutoTransferPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
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
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
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
