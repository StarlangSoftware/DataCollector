package DataCollector.WordNet;

import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Util.DrawingButton;
import WordNet.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public abstract class DomainEditorFrame extends JFrame implements ActionListener {
    protected WordNet turkish, domainWordNet;
    protected TxtDictionary dictionary;
    protected JToolBar toolBar;

    protected static final String SAVE = "save";

    //protected final String domainWordNetFileName = "estate_wordnet.xml";
    //protected final String domainDictionaryFileName = "estate_dictionary.txt";
    //protected final String prefix = "EST01-";
    protected final String domainWordNetFileName = "tourism_wordnet.xml";
    protected final String domainDictionaryFileName = "tourism_dictionary.txt";
    protected final String prefix = "TOU01-";
    protected int finalId;
    abstract void loadContents();

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case SAVE:
                domainWordNet.saveAsXml(domainWordNetFileName);
                dictionary.saveAsTxt(domainDictionaryFileName);
                break;
        }
    }

    protected SynSet addSynSet(SynSet addedSynSet, String root){
        SynSet newSynSet = new SynSet(addedSynSet.getId());
        newSynSet.setPos(addedSynSet.getPos());
        newSynSet.setDefinition(addedSynSet.getLongDefinition());
        for (int i = 0; i < addedSynSet.getSynonym().literalSize(); i++){
            if (addedSynSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr")).startsWith(root.toLowerCase(new Locale("tr")))){
                domainWordNet.addLiteralToLiteralList(addedSynSet.getSynonym().getLiteral(i));
                newSynSet.addLiteral(addedSynSet.getSynonym().getLiteral(i));
                break;
            }
        }
        domainWordNet.addSynSet(newSynSet);
        return newSynSet;
    }

    protected int getFinalId(){
        int max = 0;
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getId().startsWith(prefix)){
                int id = Integer.parseInt(synSet.getId().substring(prefix.length()));
                if (id > max){
                    max = id;
                }
            }
        }
        return max;
    }

    private void addButtons() {
        JButton save = new DrawingButton(WordNetEditorFrame.class, this, "save", SAVE, "Save");
        toolBar.add(save);
    }

    public DomainEditorFrame(){
        dictionary = new TxtDictionary(domainDictionaryFileName, new TurkishWordComparator());
        domainWordNet = new WordNet(domainWordNetFileName, new Locale("tr"));
        finalId = getFinalId();
        turkish = new WordNet();
        toolBar = new JToolBar("ToolBox");
        addButtons();
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        loadContents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
