package sprax.sorts;

import sprax.arrays.Arrays1d;
import sprax.shuffles.Shuffler;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class PartialSortSelect
{
    public static int partialSortSelectKth(int array[], int k) {
        for (int p = 0; p < k; p++) {
            int minIndex = 0;
            int minValue = array[p];
            for (int q = p + 1; q < array.length; q++) {
                if (minValue > array[q]) {
                    minIndex = q;
                    minValue = array[q];
                }
                Arrays1d.arraySwap(array, p, minIndex);
            }
        }
        return array[k];
    }
    
    public static int unit_test()
    {
        String testName = PartialSortSelect.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int iA[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sx.putsArray("Original: ", iA);
        Shuffler.shuffle(iA);
        Sx.putsArray("Shuffled: ", iA);
        int k = 3;
        int kth = partialSortSelectKth(iA, k);
        Sx.format("Kth order statistic for k = %d:  %d\n", k, kth);
        Sx.putsArray("Partials: ", iA);
        k = 5;
        kth = partialSortSelectKth(iA, k);
        Sx.format("Kth order statistic for k = %d:  %d\n", k, kth);
        Sx.putsArray("Partials: ", iA);
        k = 6;
        kth = partialSortSelectKth(iA, k);
        Sx.format("Kth order statistic for k = %d:  %d\n", k, kth);
        Sx.putsArray("Partials: ", iA);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
