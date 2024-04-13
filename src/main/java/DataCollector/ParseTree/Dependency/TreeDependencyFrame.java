package DataCollector.ParseTree.Dependency;

import DataCollector.ParseTree.TreeEditorFrame;
import DataCollector.ParseTree.TreeEditorPanel;

public class TreeDependencyFrame extends TreeEditorFrame {

    /**
     * Constructor for the {@link TreeDependencyFrame}. Sets title of the frame. Adds also an ActionListener for the open menu item.
     * When the user presses Open menu item, it displays a dialog box for selecting the parse tree file for Dependency Layer
     * editing. For this purpose, it generates an instance of DependencyPanel class with this parse tree file. Since each
     * frame may contain one or more parse tree files for editing, it uses {@link TreeEditorFrame#addPanelToFrame} method to add this DependencyPanel
     * to this frame.
     */
    public TreeDependencyFrame(){
        this.setTitle("Dependency Labeler");
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeDependencyPanel(currentPath, rawFileName);
    }

}
