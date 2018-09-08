package DataCollector.ParseTree;

public class DependencyFrame extends EditorFrame{

    /**
     * Constructor for the Dependency Frame. Sets title of the frame. Adds also an ActionListener for the open menu item.
     * When the user presses Open menu item, it displays a dialog box for selecting the parse tree file for Dependency Layer
     * editing. For this purpose, it generates an instance of DependencyPanel class with this parse tree file. Since each
     * frame may contain one or more parse tree files for editing, it uses {@link EditorFrame#addPanelToFrame} method to add this DependencyPanel
     * to this frame.
     */
    public DependencyFrame(){
        this.setTitle("Dependency Labeler");
    }

    @Override
    protected EditorPanel generatePanel(String currentPath, String rawFileName) {
        return new DependencyPanel(currentPath, rawFileName);
    }

}
