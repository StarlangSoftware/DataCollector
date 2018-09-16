package DataCollector;

import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class DataCollector extends JFrame implements ActionListener {

    protected JMenuItem itemClose;
    protected JMenuItem itemCloseAll;
    protected JMenuItem itemSave;
    protected JMenuItem itemOpen;
    protected JMenuItem itemOpenMultiple;
    protected JLabel infoTop;
    protected JLabel infoBottom;
    protected JTabbedPane projectPane;
    protected JToolBar toolBar;
    protected JMenuBar menu;
    protected JMenu projectMenu;

    static final protected String BACKWARD = "backward";
    static final protected String FORWARD = "forward";
    static final protected String FAST_BACKWARD = "fastbackward";
    static final protected String FAST_FORWARD = "fastforward";
    static final protected String FAST_FAST_BACKWARD = "fastfastbackward";
    static final protected String FAST_FAST_FORWARD = "fastfastforward";
    static final protected String RANDOM = "random";
    static final protected String CLEAR = "clear";

    protected JMenuItem addMenuItem(JMenu menu, String name, KeyStroke stroke){
        JMenuItem newItem;
        newItem = new JMenuItem(name);
        newItem.setAccelerator(stroke);
        menu.add(newItem);
        return newItem;
    }

    protected JCheckBoxMenuItem addMenuItem(JMenu menu, String name, KeyStroke stroke, boolean checked){
        JCheckBoxMenuItem newItem;
        newItem = new JCheckBoxMenuItem(name);
        newItem.setAccelerator(stroke);
        newItem.setSelected(checked);
        menu.add(newItem);
        return newItem;
    }

    protected JMenuItem addMenuItem(JMenu menu, String name){
        JMenuItem newItem;
        newItem = new JMenuItem(name);
        menu.add(newItem);
        return newItem;
    }

    protected JMenu addMenu(JMenu menu, String name){
        JMenu newItem;
        newItem = new JMenu(name);
        menu.add(newItem);
        return newItem;
    }

    protected void addButtons(JToolBar toolBar){
        JButton button;
        button = new DrawingButton(DataCollector.class, this, "backward", BACKWARD, "Previous Tree");
        button.setVisible(true);
        toolBar.add(button);
        button = new DrawingButton(DataCollector.class, this, "forward", FORWARD, "Next Tree");
        button.setVisible(true);
        toolBar.add(button);
    }

    protected void enableMenu(){
        itemClose.setEnabled(true);
        itemCloseAll.setEnabled(true);
        itemSave.setEnabled(true);
    }

    protected void disableMenu(){
        itemClose.setEnabled(false);
        itemCloseAll.setEnabled(false);
        itemSave.setEnabled(false);
    }

    protected ArrayList<FileWithSelectedWords> loadMultipleFileNames(String fileName){
        ArrayList<FileWithSelectedWords> fileList = new ArrayList<FileWithSelectedWords>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            while (line != null){
                FileWithSelectedWords fileWithSelectedWords = new FileWithSelectedWords(line.trim());
                if (fileList.size() == 0 || !fileWithSelectedWords.getFileName().equals(fileList.get(fileList.size() - 1).getFileName())){
                    fileList.add(fileWithSelectedWords);
                } else {
                    fileList.get(fileList.size() - 1).addWord(fileWithSelectedWords.getWord(0));
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
        }
        return fileList;
    }

    protected DataCollector(){
        JPanel info;
        menu = new JMenuBar();
        setJMenuBar(menu);
        projectMenu = new JMenu("Project");
        menu.add(projectMenu);
        itemOpen = addMenuItem(projectMenu, "Open", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemOpenMultiple = addMenuItem(projectMenu, "Open Multiple", null);
        itemSave = addMenuItem(projectMenu, "Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemClose = addMenuItem(projectMenu, "Close", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        itemCloseAll = addMenuItem(projectMenu, "Close All", null);
        projectPane = new JTabbedPane();
        projectPane.setFocusable(false);
        disableMenu();
        add(projectPane, BorderLayout.CENTER);
        toolBar = new JToolBar("ToolBox");
        addButtons(toolBar);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        info = new JPanel(new BorderLayout(50, 0));
        add(info, BorderLayout.SOUTH);
        infoTop = new JLabel("Ali Topu At ");
        infoTop.setForeground(Color.BLUE);
        info.add(infoTop, BorderLayout.NORTH);
        infoBottom = new JLabel("Cümle Burada Olacak ");
        infoBottom.setForeground(Color.RED);
        info.add(infoBottom, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        itemClose.addActionListener(e -> {
            projectPane.remove(projectPane.getSelectedIndex());
            if (projectPane.getTabCount() == 0){
                disableMenu();
            }
        });
        itemCloseAll.addActionListener(e -> {
            while (projectPane.getTabCount() != 0){
                projectPane.remove(0);
            }
            disableMenu();
        });
    }

    public void actionPerformed(ActionEvent e) {
    }
}
