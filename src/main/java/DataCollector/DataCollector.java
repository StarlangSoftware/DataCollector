package DataCollector;

import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataCollector extends JFrame implements ActionListener {

    protected JMenuItem itemClose;
    protected JMenuItem itemCloseAll;
    protected JMenuItem itemSave;
    protected JMenuItem itemOpen;
    protected JMenuItem itemOpenMultiple;
    protected JMenuItem itemGoToFile;
    protected JLabel infoTop;
    protected JLabel infoBottom;
    protected JPanel bottom;
    protected JPanel info;
    protected JTabbedPane projectPane;
    protected JToolBar toolBar;
    protected JMenuBar menu;
    protected JMenu projectMenu;
    private JTextArea textArea;
    public String userName;

    static final protected String BACKWARD = "backward";
    static final protected String FORWARD = "forward";
    static final protected String FAST_BACKWARD = "fastbackward";
    static final protected String FAST_FORWARD = "fastforward";
    static final protected String FAST_FAST_BACKWARD = "fastfastbackward";
    static final protected String FAST_FAST_FORWARD = "fastfastforward";
    static final protected String RANDOM = "random";
    static final protected String CLEAR = "clear";

    /**
     * The addMenuItem method creates a new {@link JMenuItem} with given name and {@link KeyStroke} then adds
     * this item to the given {@link JMenu}.
     *
     * @param menu   {@link JMenu} to add item.
     * @param name   text of the <code>JMenuItem</code>.
     * @param stroke key combination which invokes the menu item's action listeners.
     * @return A new {@link JMenuItem}.
     */
    protected JMenuItem addMenuItem(JMenu menu, String name, KeyStroke stroke) {
        JMenuItem newItem;
        newItem = new JMenuItem(name);
        newItem.setAccelerator(stroke);
        menu.add(newItem);
        return newItem;
    }

    /**
     * The addMenuItem method creates a new {@link JCheckBoxMenuItem} with given name and {@link KeyStroke} then adds
     * this item to the given {@link JMenu}.
     *
     * @param menu    {@link JMenu} to add item.
     * @param name    text of the {@link JCheckBoxMenuItem}.
     * @param stroke  key combination which invokes the menu item's action listeners.
     * @param checked the state of the button.
     * @return A new {@link JMenuItem}.
     */
    protected JCheckBoxMenuItem addMenuItem(JMenu menu, String name, KeyStroke stroke, boolean checked) {
        JCheckBoxMenuItem newItem;
        newItem = new JCheckBoxMenuItem(name);
        newItem.setAccelerator(stroke);
        newItem.setSelected(checked);
        menu.add(newItem);
        return newItem;
    }

    /**
     * The addMenuItem method creates a new {@link JMenuItem} with given name then adds
     * this item to the given {@link JMenu}.
     *
     * @param menu {@link JMenu} to add item.
     * @param name text of the <code>JMenuItem</code>.
     * @return A new {@link JMenuItem}.
     */
    protected JMenuItem addMenuItem(JMenu menu, String name) {
        JMenuItem newItem;
        newItem = new JMenuItem(name);
        menu.add(newItem);
        return newItem;
    }

    /**
     * The addMenu method creates a new {@link JMenu} with given name then adds
     * it to the given {@link JMenu}.
     *
     * @param menu {@link JMenu} to add newly created menu.
     * @param name text of the <code>JMenu</code>.
     * @return A new {@link JMenu}.
     */
    protected JMenu addMenu(JMenu menu, String name) {
        JMenu newItem;
        newItem = new JMenu(name);
        menu.add(newItem);
        return newItem;
    }

    /**
     * The addButtons method creates {@link JButton} as "backward" and "forward", and adds them to the given {@link JToolBar}.
     *
     * @param toolBar component that is useful for displaying buttons.
     */
    protected void addButtons(JToolBar toolBar) {
        JButton button;
        button = new DrawingButton(DataCollector.class, this, "backward", BACKWARD, "Previous Tree");
        button.setVisible(true);
        toolBar.add(button);
        button = new DrawingButton(DataCollector.class, this, "forward", FORWARD, "Next Tree");
        button.setVisible(true);
        toolBar.add(button);
    }

    /**
     * The enableMenu method enables Close, Close All and Save menu items.
     */
    protected void enableMenu() {
        itemClose.setEnabled(true);
        itemCloseAll.setEnabled(true);
        itemSave.setEnabled(true);
    }

    /**
     * The disableMenu method disables Close, Close All and Save menu items.
     */
    protected void disableMenu() {
        itemClose.setEnabled(false);
        itemCloseAll.setEnabled(false);
        itemSave.setEnabled(false);
    }

    /**
     * The loadMultipleFileNames method takes a file name as an input which has multiple file names in it. It then creates
     * an {@link ArrayList} which holds each distinct file name inside the given file.
     *
     * @param fileName File that has multiple file names in it.
     * @return an {@link ArrayList} of file names to be loaded.
     */
    protected ArrayList<FileWithSelectedWords> loadMultipleFileNames(String fileName) {
        ArrayList<FileWithSelectedWords> fileList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null) {
                FileWithSelectedWords fileWithSelectedWords = new FileWithSelectedWords(line.trim());
                if (fileList.isEmpty() || !fileWithSelectedWords.getFileName().equals(fileList.get(fileList.size() - 1).getFileName())) {
                    fileList.add(fileWithSelectedWords);
                } else {
                    fileList.get(fileList.size() - 1).addWord(fileWithSelectedWords.getWord(0));
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
        }
        return fileList;
    }

    /**
     * DataCollector constructor initially creates a new menu bar and a <code>JMenu</code> with the supplied string "Project".
     * Then adds Open, Open Multiple, Save, Close and Close All items to the {@link JMenu} and define actions for Close and Close All items.
     */
    protected DataCollector() {
        userName = JOptionPane.showInputDialog("Enter your user name");
        menu = new JMenuBar();
        setJMenuBar(menu);
        projectMenu = new JMenu("Project");
        menu.add(projectMenu);
        itemOpen = addMenuItem(projectMenu, "Open", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemOpenMultiple = addMenuItem(projectMenu, "Open Multiple", null);
        itemSave = addMenuItem(projectMenu, "Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemClose = addMenuItem(projectMenu, "Close", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        itemCloseAll = addMenuItem(projectMenu, "Close All", null);
        itemGoToFile = addMenuItem(projectMenu, "Go To File", KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        projectPane = new JTabbedPane();
        projectPane.setFocusable(false);
        disableMenu();
        add(projectPane, BorderLayout.CENTER);
        textArea = new JTextArea();
        textArea.setColumns(10);
        File f = new File(userName + ".txt");
        if (f.exists()){
            try {
                FileReader reader = new FileReader(f);
                BufferedReader br = new BufferedReader(reader);
                textArea.read( br, null);
                br.close();
            } catch (IOException ignored) {
            }
        }
        add(textArea, BorderLayout.EAST);
        toolBar = new JToolBar("ToolBox");
        addButtons(toolBar);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        bottom = new JPanel(new BorderLayout());
        info = new JPanel(new BorderLayout());
        infoTop = new JLabel("Ali Topu At ");
        infoTop.setForeground(Color.BLUE);
        info.add(infoTop, BorderLayout.NORTH);
        infoBottom = new JLabel("Cümle Burada Olacak ");
        infoBottom.setForeground(Color.RED);
        info.add(infoBottom, BorderLayout.SOUTH);
        bottom.add(info, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        itemClose.addActionListener(e -> {
            projectPane.remove(projectPane.getSelectedIndex());
            if (projectPane.getTabCount() == 0) {
                disableMenu();
            }
        });
        itemCloseAll.addActionListener(e -> {
            while (projectPane.getTabCount() != 0) {
                projectPane.remove(0);
            }
            disableMenu();
        });
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                try {
                    FileWriter writer = new FileWriter( userName + ".txt");
                    BufferedWriter bw = new BufferedWriter(writer);
                    textArea.write(bw);
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        });
    }

    /**
     * Overridden actionPerformed method.
     *
     * @param e {@link ActionEvent} input.
     */
    public void actionPerformed(ActionEvent e) {
    }
}
