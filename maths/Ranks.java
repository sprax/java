package sprax.maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import sprax.Sx;
import sprax.arrays.ArrayFactory;
import sprax.containers.Reversed;
import sprax.search.BinarySearch;
import sprax.shuffles.Shuffler;

public class Ranks 
{
    /**
     * Number of elements in an unordered array that are less than value.
     * O(N) from one traversal.
     */
    static int arrayRank(int A[], int value)
    {
        int count = 0;
        for (int a : A)
            if (a < value)
                count++;
        return count;
    }
    
    static <T extends Comparable<T>> int arrayRank(T A[], T value)
    {
        int count = 0;
        for (T a : A)
            if (a.compareTo(value) < 0)
                count++;
        return count;
    }    

    /**
     * Number of elements in a sorted array that are less than value.
     * O(lgN) from one binary search.
     */
    static int sortedArrayRank(int A[], int value)
    {
        // This binary search returns the index of last element in A < value.
        // We need to return the size of the inferior subset, which is this
        // index + 1
        return 1 + BinarySearch.binarySearchStrictLowerBound(A, value);
    }
    
    static <T extends Comparable<T>> int listRank(List<T> list, T value)
    {
        int count = 0;
        for (T a : list)
            if (a.compareTo(value) < 0)
                count++;
        return count;
    }
    
    static <T extends Comparable<T>> int setRank(Set<T> set, T value)
    {
        int count = 0;
        for (T t : set)
            if (t.compareTo(value) < 0)
                count++;
        return count;
    }
    
    
    static <T extends Comparable<T>> int sortedSetRank(SortedSet<T> set, T value)
    {
        return set.headSet(value).size();
    }
    
    static <T extends Comparable<T>> int sortedSetAntiRank(SortedSet<T> set, T value)
    {
        return set.tailSet(value).size();
    }
    
    
    public static int unit_test(int dbg) 
    {
      String testName =  Ranks.class.getName() + ".unit_test";
      Sx.puts(testName + ": BEGIN");
      
      int value = 6;
      int sortedArray[]   = { 0, 1, 2, 2, 3, 4, 6, 6, 7, 8, 9, 11, 14, 14, 15, 16, 17, 18, 19, 21, 24 };
      int randomArray[]   = Shuffler.shuffled(sortedArray);
      Sx.putsArray("How many in: ", randomArray, " < " + value + "?");    
      Sx.putsArray("How many in: ", sortedArray, " < " + value + "?  The same.");    
      Integer randomIntArray[] = ArrayFactory.makeIntegerArray(randomArray);
      Integer sortedIntArray[] = ArrayFactory.makeIntegerArray(sortedArray);
      
      int rank = arrayRank(randomArray, value);
      Sx.format("%2d sez %s\n", rank, "arrayRank(randomArray)");

      rank = arrayRank(sortedIntArray, value);
      Sx.format("%2d sez %s\n", rank, "arrayRank<Integer>(sortedArray)");

      rank = sortedArrayRank(sortedArray, value);
      Sx.format("%2d sez %s\n", rank, "sortedArrayRank");
      
      List<Integer> list = new ArrayList<Integer>(Arrays.asList(randomIntArray));
      rank = listRank(list, value);
      Sx.format("%2d sez %s\n", rank, "listRank");
      
      Set<Integer> set = new HashSet<Integer>();
      for (Integer i : Reversed.reversed(list))
          set.add(i);
      Sx.putsIterable("unsortedSet: ", set);

      SortedSet<Integer> sortedSet = new TreeSet<Integer>(list);
      Sx.putsIterable("  sortedSet: ", sortedSet);

      rank = setRank(set, value);
      Sx.format("%2d sez %s\n", rank, "setRank(randomSet)");
             
      rank = setRank(sortedSet, value);
      Sx.format("%2d sez %s\n", rank, "setRank(sortedSet)");
       
      rank = sortedSetRank(sortedSet, value);
      Sx.format("%2d sez %s\n", rank, "sortedSetRank");
       
      Sx.puts(testName + ": END");
      return 0;
    }

    public static void main(String[] args) { unit_test(0); }

}
