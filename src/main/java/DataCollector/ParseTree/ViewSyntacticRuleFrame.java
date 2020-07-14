package DataCollector.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.TreeBankDrawable;
import AnnotatedTree.WordNotExistsException;
import Util.DrawingButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class ViewSyntacticRuleFrame extends JFrame implements ActionListener {
    protected ArrayList<ArrayList<String>> data;
    protected JTable dataTable;
    protected TreeBankDrawable treeBank;
    protected int COLOR_COLUMN_INDEX;

    protected static final String ID_SORT = "sortid";

    protected class RowComparator implements Comparator<ArrayList<String>> {

        Collator collator;

        public RowComparator(){
            Locale locale = new Locale("tr");
            collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
            if (o1.get(1).equals(o2.get(1))) {
                if (o1.get(0).equals(o2.get(0))) {
                    return collator.compare(o1.get(0), o2.get(0));
                } else {
                    return collator.compare(o1.get(2), o2.get(2));
                }
            } else {
                return collator.compare(o1.get(1), o2.get(1));
            }
        }
    }

    protected void updateGroupColors(){
        int groupCount = 0;
        data.get(0).set(COLOR_COLUMN_INDEX, "0");
        for (int i = 1; i < data.size(); i++){
            if (!data.get(i).get(1).equals(data.get(i - 1).get(1))){
                groupCount++;
            }
            data.get(i).set(COLOR_COLUMN_INDEX, "" + groupCount);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ID_SORT:
                data.sort(new RowComparator());
                updateGroupColors();
                JOptionPane.showMessageDialog(this, "Rules Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    public class CellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int groupCount = Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX));
            if (groupCount % 2 == 0){
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(Color.LIGHT_GRAY);
            }
            if (isSelected){
                c.setBackground(Color.BLUE);
            }
            return c;
        }
    }

    private void addRule(ParseTreeDrawable parseTree, ParseNodeDrawable parseNode){
        if (parseNode.numberOfChildren() > 0) {
            String rule = parseNode.getData() + " ->";
            String sentenceString = "<html>";
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable child = (ParseNodeDrawable) parseNode.getChild(i);
                if (child.numberOfChildren() > 0){
                    rule += " " + child.getData();
                } else {
                    try {
                        rule += " " + child.getLayerInfo().getMorphologicalParseAt(0).getWord().getName();
                    } catch (LayerNotExistsException | WordNotExistsException e) {
                        rule += " " + child.getLayerData(ViewLayerType.TURKISH_WORD);
                    }
                }
                switch (i) {
                    case 0:
                        sentenceString += " <b><font color=\"red\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 1:
                        sentenceString += " <b><font color=\"blue\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 2:
                        sentenceString += " <b><font color=\"green\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 3:
                        sentenceString += " <b><font color=\"fuchsia\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 4:
                        sentenceString += " <b><font color=\"aqua\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 5:
                        sentenceString += " <b><font color=\"grey\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    case 6:
                        sentenceString += " <b><font color=\"pink\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                    default:
                        sentenceString += " <b><font color=\"black\">" + child.toTurkishSentence() + "</font></b>";
                        break;
                }
            }
            sentenceString += "</html>";
            ArrayList<String> row = new ArrayList<>();
            row.add(parseTree.getFileDescription().getRawFileName());
            row.add(rule);
            row.add(sentenceString);
            row.add("0");
            data.add(row);
            for (int i = 0; i < parseNode.numberOfChildren(); i++) {
                ParseNodeDrawable child = (ParseNodeDrawable) parseNode.getChild(i);
                addRule(parseTree, child);
            }
        }
    }

    protected void prepareData(TreeBankDrawable treeBank){
        data = new ArrayList<>();
        for (int i = 0; i < treeBank.size(); i++){
            ParseTreeDrawable parseTree = treeBank.get(i);
            addRule(parseTree, (ParseNodeDrawable) parseTree.getRoot());
        }
    }

    public class DependencyTableDataModel extends AbstractTableModel {

        public int getColumnCount() {
            return COLOR_COLUMN_INDEX;
        }

        public int getRowCount() {
            return data.size();
        }

        public Class getColumnClass(int col) {
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "FileName";
                case 1:
                    return "Rule";
                case 2:
                    return "Sentence";
                default:
                    return "";
            }
        }

    }

    public ViewSyntacticRuleFrame(TreeBankDrawable treeBank, TreeSyntacticFrame syntacticFrame){
        this.treeBank = treeBank;
        COLOR_COLUMN_INDEX = 3;
        JToolBar toolBar = new JToolBar("ToolBox");
        JButton idSort = new DrawingButton(ViewSyntacticRuleFrame.class, this, "sortnumbers", ID_SORT, "");
        toolBar.add(idSort);
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        prepareData(treeBank);
        dataTable = new JTable(new DependencyTableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(150);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(1).setMinWidth(200);
        dataTable.getColumnModel().getColumn(2).setMinWidth(300);
        dataTable.setDefaultRenderer(Object.class, new ViewSyntacticRuleFrame.CellRenderer());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2){
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        syntacticFrame.addPanelToFrame(new TreeSyntacticPanel(TreeEditorPanel.treePath, fileName, ViewLayerType.TURKISH_WORD), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }


}
