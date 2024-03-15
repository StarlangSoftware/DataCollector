package DataCollector;

import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Dictionary.TxtWord;
import MorphologicalAnalysis.Transition;
import PropBank.ArgumentType;
import PropBank.FramesetList;
import WordNet.SynSet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ArgumentEditorPanel extends JPanel {
    public static final int SAVE = 1;
    public static final int DELETE = 2;

    private final FramesetList xmlParser;
    private final SynSet currentSynSet;
    private final TxtDictionary dictionary;
    private JTextField definition;

    public ArgumentEditorPanel(SynSet currentSynSet, String treeName, FramesetList xmlParser) {
        this.currentSynSet = currentSynSet;
        this.xmlParser = xmlParser;
        dictionary = new TxtDictionary("Data/Dictionary/turkish_dictionary.txt", new TurkishWordComparator());
        initPanel(treeName);
    }

    public void setTreeName(String treeName){
        TitledBorder title = BorderFactory.createTitledBorder(treeName);
        title.setTitleColor(Color.RED);
        setBorder(title);
    }

    private void fillFrameList(final JList<String> frameList){
        ArrayList<String> stringFrame = getFrameListFromXml(currentSynSet.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : stringFrame) {
            model.addElement(s);
        }
        frameList.setModel(model);
        frameList.repaint();
    }

    public void initPanel(String treeName) {
        TitledBorder title = BorderFactory.createTitledBorder(treeName);
        title.setTitleColor(Color.RED);
        setBorder(title);
        final JComboBox<ArgumentType> argumentList = new JComboBox<>();
        final JLabel argLabel = new JLabel();
        final JLabel defLabel = new JLabel();
        definition = new JTextField();
        definition.setAutoscrolls(true);
        JButton saveButton = new JButton();
        JButton clearButton = new JButton();
        JButton editButton = new JButton();
        JButton deleteButton = new JButton();
        JTextArea definitionArea = new JTextArea();
        JScrollPane frameScrollPane = new JScrollPane();
        final JList<String> frameList = new JList<>();
        fillFrameList(frameList);
        frameScrollPane.setViewportView(frameList);
        argumentList.setModel(new DefaultComboBoxModel<>(ArgumentType.values()));
        argLabel.setText("Arg :");
        defLabel.setText("Definition");
        saveButton.setText("Save");
        argumentList.addActionListener(e -> {
            String autoDefinition = null;
            String infinitiveForm = currentSynSet.representative();
            String[] words = infinitiveForm.split(" ");
            String verbForm = words[words.length - 1];
            String prefix = "";
            switch (words.length){
                case 2:
                    prefix = words[0] + " ";
                    break;
                case 3:
                    prefix = words[0] + " " + words[1] + " ";
                    break;
            }
            String verbRootForm = verbForm.substring(0, verbForm.length() - 3);
            TxtWord verbRoot = (TxtWord) dictionary.getWord(verbRootForm);
            if (verbRoot == null){
                verbRoot = new TxtWord(verbRootForm);
                verbRoot.addFlag("CL_FIIL");
            }
            switch ((ArgumentType) argumentList.getSelectedItem()){
                case ARG0:
                    autoDefinition = prefix + new Transition("yAn").makeTransition(verbRoot, verbRoot.getName());
                    break;
                case ARG1:
                    switch (verbRoot.verbType()){
                        case "F4PW":
                        case "F4PW-NO-REF":
                            autoDefinition = prefix + new Transition("nAn").makeTransition(verbRoot, verbRoot.getName()) + " şey";
                            break;
                        default:
                            autoDefinition = prefix + new Transition("nHlAn").makeTransition(verbRoot, verbRoot.getName()) + " şey";
                            break;
                    }
                    break;
                case ARGMCAU:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " nedeni";
                    break;
                case ARGMTMP:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " zamanı";
                    break;
                case ARGMLOC:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " yeri";
                    break;
                case ARGMEXT:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " miktarı";
                    break;
                case ARGMPNC:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " amacı";
                    break;
                case ARGMMNR:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " şekli";
                    break;
                case ARGMDIR:
                    autoDefinition = prefix + new Transition("mA").makeTransition(verbRoot, verbRoot.getName()) + " yönü";
                    break;
            }
            if (autoDefinition != null){
                definition.setText(autoDefinition.substring(0, 1).toUpperCase(new Locale("tr")) + autoDefinition.substring(1));
            } else {
                definition.setText("");
            }
        });
        saveButton.addActionListener(evt -> {
            if (!definition.getText().isEmpty() && argumentList.getSelectedItem() != ArgumentType.NONE) {
                //xmlParser.saveAsXml(currentSynSet.getId(), argumentList.getSelectedItem().toString(), definition.getText(), SAVE);
            }
            fillFrameList(frameList);
            argumentList.setSelectedItem(ArgumentType.NONE);
            definition.setText("");
        });
        clearButton.setText("Clear");
        clearButton.addActionListener(e -> {
            argumentList.setSelectedItem(ArgumentType.NONE);
            definition.setText("");
        });
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> {
            if (frameList.getSelectedValue() != null) {
                for (Object o : frameList.getSelectedValuesList()) {
                    //xmlParser.saveAsXml(currentSynSet.getId(), o.toString().trim().split(":")[0], o.toString().trim().split(":")[1], DELETE);
                }
                fillFrameList(frameList);
                argumentList.setSelectedItem(ArgumentType.NONE);
                definition.setText("");
            }
        });
        editButton.setText("Edit");
        editButton.addActionListener(e -> {
            if (frameList.getSelectedValue() != null && frameList.getSelectedValuesList().size() <= 1) {
                argumentList.setSelectedItem(ArgumentType.valueOf(frameList.getSelectedValue().trim().split(":")[0]));
                definition.setText(frameList.getSelectedValue().trim().split(":")[1]);
            }
            fillFrameList(frameList);
        });

        definitionArea.setText(currentSynSet.getDefinition());
        definitionArea.setLineWrap(true);
        definitionArea.setEditable(false);
        definitionArea.setOpaque(false);
        definitionArea.setForeground(Color.RED);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(defLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(definition))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(argLabel)
                                                                .addGap(36, 36, 36)
                                                                .addComponent(argumentList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(53, 53, 53)
                                                                .addComponent(saveButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(clearButton)))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(frameScrollPane)))
                                .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(deleteButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editButton)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(definitionArea)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(definitionArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(31, 31, 31)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(argumentList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(argLabel))
                                        .addGap(24, 24, 24)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(defLabel)
                                                .addComponent(definition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(saveButton)
                                                .addComponent(clearButton))
                                        .addGap(33, 33, 33)
                                        .addComponent(frameScrollPane, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(deleteButton)
                                                .addComponent(editButton))
                                        .addContainerGap(25, Short.MAX_VALUE)
                        ));
    }

    public ArrayList<String> getFrameListFromXml(String id){
        Map<ArgumentType, String> frameset = xmlParser.readFromXML(id);
        ArrayList<String> stringFrame = new ArrayList<>();
        if (!frameset.isEmpty()) {
            stringFrame = new ArrayList<>();
            for (int i = 0; i < frameset.size(); i++) {
                stringFrame.add(frameset.keySet().toArray()[i].toString() + ":" + frameset.get(frameset.keySet().toArray()[i]));
            }
        }
        return stringFrame;
    }

}
