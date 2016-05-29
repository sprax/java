package sprax.sorts;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class MergeSortTest
{
    
    private int[]            numbers;
    private final static int SIZE     = 8192 * 8;
    private final static int RAND_MAX = 1024;
    
    @Before
    public void setUp() throws Exception {
        numbers = new int[SIZE];
        Random generator = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = generator.nextInt(RAND_MAX);
        }
    }
    
    public long testMergeSort()
    {
        MergeSort sorter = new MergeSort();
        long startTime = System.currentTimeMillis();
        sorter.sort(numbers);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Mergesort " + elapsedTime);
        
        for (int i = 0; i < numbers.length - 1; i++) {
            if (numbers[i] > numbers[i + 1]) {
                fail("Should not happen");
            }
        }
        assertTrue(true);
        return elapsedTime;
    }
    
    public long testStandardSort()
    {
        long startTime = System.currentTimeMillis();
        Arrays.sort(numbers);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Standard Java sort " + elapsedTime);
        
        for (int i = 0; i < numbers.length - 1; i++) {
            if (numbers[i] > numbers[i + 1]) {
                fail("Should not happen");
            }
        }
        assertTrue(true);
        return elapsedTime;
    }
    
    @Test
    public void itWorksRepeatably() {
        for (int i = 0; i < 200; i++) {
            numbers = new int[SIZE];
            Random generator = new Random();
            for (int a = 0; a < numbers.length; a++) {
                numbers[a] = generator.nextInt(RAND_MAX);
            }
            MergeSort sorter = new MergeSort();
            sorter.sort(numbers);
            for (int j = 0; j < numbers.length - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    fail("Should not happen");
                }
            }
            assertTrue(true);
        }
    }
    
    public static void unit_test() throws Exception
    {
        String testName = MergeSortTest.class.getName() + ".unit_test";
        System.out.format("BEGIN %s\n\n", testName);
        
        int status = 0;
        MergeSortTest mst = new MergeSortTest();
        mst.setUp();
        long stdSortTime = mst.testStandardSort();
        mst.setUp();
        long mrgSortTime = mst.testMergeSort();
        if (mrgSortTime > 0) {
            System.out.println("time ratio standard/merge sort:  " + (float) stdSortTime
                    / mrgSortTime);
        }
        
        System.out.format("\nEND %s: %s\n", testName, (status == 0 ? "PASS" : "FAIL"));
    }
    
    public static void main(String[] args) throws Exception
    {
        unit_test();
    }
    
}
