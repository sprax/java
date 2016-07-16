/*************************************************************************
 * https://www.hackerrank.com/challenges/insertionsort2
 *************************************************************************/
package sprax.sorts;

import java.util.Scanner;

import sprax.test.Sz;

public class InsertSortStdin<T extends Comparable<T>> implements SortInt
{
    @Override
    public void sort(int[] array) {
        insertionSortArray(array);
    }
    
    public static void insertionSortArray(int[] ar)
    {       
        for (int j = 1; j < ar.length; j++)
            insertSortHead(ar, j);
    }  
    
    public static void insertSortHead(int[] ar, int idx) {
        int val = ar[idx];
        while (--idx >= 0 && val < ar[idx]) {
            ar[idx+1] = ar[idx];
        }
        ar[idx+1] = val;
        printArray(ar);
    }
    
    public static int insertionSortArrayCountShifts(int[] ar)
    {       
        int shifts = 0;
        for (int j = 1; j < ar.length; j++)
            shifts += insertSortHeadCountShifts(ar, j);
        return shifts;
    }  
    
    public static int insertSortHeadCountShifts(int[] ar, int idx) {
        int val = ar[idx];
        int shifts = 0;
        while (--idx >= 0 && val < ar[idx]) {
            ar[idx+1] = ar[idx];
            shifts++;
        }
        ar[idx+1] = val;
        //printArray(ar);
        return shifts;
    }
    
    
    
    public static void sortStdin() 
    {
        try (Scanner in = new Scanner(System.in)) {
            int s = in.nextInt();
            int[] ar = new int[s];
            for(int i=0;i<s;i++){
                ar[i]=in.nextInt(); 
            }
            int shifts = insertionSortArrayCountShifts(ar);
            System.out.println(shifts);
        }    
    }
    
    private static void printArray(int[] ar) {
        for(int n: ar){
            System.out.print(n+" ");
        }
        System.out.println("");
    }
    
    public static int unit_test(int level) 
    {
        String testName = InsertSortStdin.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        int verbose = 1;
        
        InsertSortStdin<Integer> intSorter = new InsertSortStdin<>();
        numWrong += SortUtil.test_sort_random_int_array(intSorter, 32, 100, 0L, verbose);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(1);
    }
}


