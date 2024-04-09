package DataCollector;

import java.util.ArrayList;

public class FileWithSelectedWords {
    private final ArrayList<String> wordList;
    private final String fileName;

    /**
     * FileWithSelectedWords reads a line which contains a filename and also possibly a set of words separated via tab
     * character. The method sets the filename and if they exist, also words.
     * @param line Line consisting filename and possibly a set of words.
     */
    public FileWithSelectedWords(String line){
        wordList = new ArrayList<>();
        if (line.contains("\t")){
            String[] tokens = line.split("\t");
            this.fileName = tokens[0];
            wordList.add(tokens[1]);
        } else {
            this.fileName = line;
        }
    }

    /**
     * Getter for the filename
     * @return File name
     */
    public String getFileName(){
        return fileName;
    }

    /**
     * Gets word at index
     * @param index Index of the word
     * @return Word at index
     */
    public String getWord(int index){
        return wordList.get(index);
    }

    /**
     * Adds a new word to the word list
     * @param word Word to be added
     */
    public void addWord(String word){
        wordList.add(word);
    }

    /**
     * Size of the word list
     * @return Size of the word list
     */
    public int size(){
        return wordList.size();
    }
}
