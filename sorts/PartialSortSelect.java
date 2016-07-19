package sprax.sorts;

import java.util.*;

import sprax.test.Sz;

public class PartialSortSelect
{

    /*
    function select(list[1..n], k)
    for i from 1 to k
        minIndex = i
        minValue = list[i]
        for j from i+1 to n
            if list[j] < minValue
                minIndex = j
                minValue = list[j]
        swap list[i] and list[minIndex]
    return list[k]
    */
    
    public static int unit_test()
    {
        String testName = PartialSortSelect.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        for (int SIZE = 100; SIZE < 1000000; SIZE *= 2) {
            long start, end;
            long elapsed1 = 0, elapsed2 = 0, elapsed3 = 0;
            Integer[] a = new Integer[SIZE];
            
            // sorted input
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(i);
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed1 = end - start;
            
            // reverse sorted input
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(SIZE - i);
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed2 = end - start;
            
            // random input
            Random r = new Random();
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(r.nextInt(SIZE));
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed3 = end - start;
            
            System.out.println("size: " + SIZE +
                    "\tsorted: " + elapsed1 +
                    "\treverse: " + elapsed2 +
                    "\trandom: " + elapsed3);
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
