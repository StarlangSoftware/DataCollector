package DataCollector;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator class for comparing two lines in the data table.
 */
public class RowComparator2 implements Comparator<ArrayList<String>> {

    int firstIndex, secondIndex;
    Collator collator;

    /**
     * Constructor for the row comparator class. Sets the first, second, and third indexes for comparison criteria.
     * @param firstIndex First column index.
     * @param secondIndex Second column index.
     */
    public RowComparator2(int firstIndex, int secondIndex){
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        Locale locale = new Locale("tr");
        collator = Collator.getInstance(locale);
    }

    /**
     * Checks if at least one of the columns of the two lines are null or not. If both of them are null, the
     * function returns 0, which means both lines are equal. If one of them is null, the null line is larger
     * than the not null line.
     * @param o1 First line to be compared
     * @param o2 Second line to be compared
     * @param index Index of the column for comparisons
     * @return 0 if both values are null, -1 if second is null, 1 if first is null, 2 if none of them is null.
     */
    protected int checkForNull(ArrayList<String> o1, ArrayList<String> o2, int index){
        if (o1.get(index) == null && o2.get(index) == null){
            return 0;
        } else {
            if (o1.get(index) == null){
                return 1;
            } else {
                if (o2.get(index) == null){
                    return -1;
                }
            }
        }
        return 2;
    }

    /**
     * Compares two lines in the data table based on first and second column indexes. If at least one of
     * the values compared is null; the function uses checkForNull for result. If none of the values compared is
     * null; the function first  compares two lines based on the first column, if they are the same, compares based
     * on the second column.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 0 for equality, 1 when the first line is larger than the second line, -1 otherwise.
     */
    @Override
    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
        int compareResultForNullCase = checkForNull(o1, o2, firstIndex);
        if (compareResultForNullCase != 2){
            return compareResultForNullCase;
        }
        if (o1.get(firstIndex).equals(o2.get(firstIndex))) {
            compareResultForNullCase = checkForNull(o1, o2, secondIndex);
            if (compareResultForNullCase != 2){
                return compareResultForNullCase;
            }
            return collator.compare(o1.get(secondIndex), o2.get(secondIndex));
        } else {
            return collator.compare(o1.get(firstIndex), o2.get(firstIndex));
        }
    }

}
