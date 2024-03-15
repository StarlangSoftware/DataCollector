package DataCollector;

import java.util.ArrayList;

public class FileWithSelectedWords {
    private final ArrayList<String> wordList;
    private final String fileName;

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

    public String getFileName(){
        return fileName;
    }

    public String getWord(int index){
        return wordList.get(index);
    }

    public void addWord(String word){
        wordList.add(word);
    }

    public int size(){
        return wordList.size();
    }
}
