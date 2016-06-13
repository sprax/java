package sprax.sorts;

import java.util.LinkedList;
import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class Merge<E extends Comparable<? super E>>
{
    public static <E extends Comparable<E>> LinkedList<E> mergeSort(LinkedList<E> m)
    {
        if (m.size() <= 1)
            return m;
        
        int middle = m.size() / 2;
        LinkedList<E> left = new LinkedList<E>();
        for (int i = 0; i < middle; i++)
            left.add(m.get(i));
        LinkedList<E> right = new LinkedList<E>();
        for (int i = middle; i < m.size(); i++)
            right.add(m.get(i));
        
        right = mergeSort(right);
        left = mergeSort(left);
        LinkedList<E> result = merge(left, right);
        
        return result;
    }
    
    public static <E extends Comparable<E>> LinkedList<E> merge(LinkedList<E> left,
            LinkedList<E> right)
    {
        LinkedList<E> result = new LinkedList<E>();
        
        while (!left.isEmpty() && !right.isEmpty()) {
            //change the direction of this comparison to change the direction of the sort
            if (left.peek().compareTo(right.peek()) <= 0)
                result.add(left.remove());
            else
                result.add(right.remove());
        }
        
        result.addAll(left);
        result.addAll(right);
        return result;
    }
    
    public static int unit_test()
    {
        String testName = Merge.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int size = 40;
        int range = size * 4;
        LinkedList<Integer> nums = new LinkedList<Integer>();
        Random rng = new Random();
        System.out.println(size + " random numbers:");
        for (int j = 0; j < size; j++) {
            int n = rng.nextInt(range);
            System.out.print(n + " ");
            nums.add(n);
        }
        System.out.println("\nmergeSorted:");
        LinkedList<Integer> sorted = Merge.mergeSort(nums);
        for (Integer n : sorted) {
            System.out.print(n + " ");
        }
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }   
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
