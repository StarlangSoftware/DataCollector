package DataCollector;

import java.util.ArrayList;

public class RowComparator3 extends RowComparator2 {

    int thirdIndex;

    /**
     * Constructor for the row comparator class. Sets the first, second, and third indexes for comparison criteria.
     *
     * @param firstIndex  First column index.
     * @param secondIndex Second column index.
     * @param thirdIndex Second column index.
     */
    public RowComparator3(int firstIndex, int secondIndex, int thirdIndex) {
        super(firstIndex, secondIndex);
        this.thirdIndex = thirdIndex;
    }

    /**
     * Compares two lines in the data table based on first, second, and third column indexes. If at least one of
     * the values compared is null; the function uses checkForNull for result. If none of the values compared is
     * null; the function first  compares two lines based on the first column, if they are the same, compares based
     * on the second column, if they are also same, compares based on the third column.
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
            if (o1.get(secondIndex).equals(o2.get(secondIndex))) {
                compareResultForNullCase = checkForNull(o1, o2, thirdIndex);
                if (compareResultForNullCase != 2){
                    return compareResultForNullCase;
                }
                return collator.compare(o1.get(thirdIndex), o2.get(thirdIndex));
            } else {
                return collator.compare(o1.get(secondIndex), o2.get(secondIndex));
            }
        } else {
            return collator.compare(o1.get(firstIndex), o2.get(firstIndex));
        }
    }

}
