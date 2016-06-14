package sprax.numbers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;

import sprax.sprout.Sx;
import sprax.test.Sz;

/** Random iterator over a constant array of elements of type T */
public class RandomIterator<T> implements Iterator<T>
{
    private final T array[];
    private final Random rands;
    private final int arraySize;
    private int itSize;
    
    public RandomIterator(T array[], int iteratorSize, long seed)
    {
        if (array == null || array.length < 2)
            throw new IllegalArgumentException("bad array");
        this.array = array;
        this.arraySize = array.length;
        this.itSize = iteratorSize;
        this.rands = new Random(seed); 
    }
    
    /** always true */
    @Override
    public boolean hasNext() {
        return itSize > 0;
    }
    
    /** returns random element of underlying array */
    @Override
    public T next() {
        if (--itSize < 0)
            throw new java.util.NoSuchElementException("empty");
        return array[rands.nextInt(arraySize)];
    }
    
    public static int unit_test() 
    {
        String testName = RandomIterator.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Sx.puts("Iterable to Iterator:");
        ShowIt showIt = new ShowIt();
        Integer A[] = {1, 2, 3, 4, 5, 6};
        Iterable<Integer> iterable = Arrays.asList(A);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.forEachRemaining(showIt);
        if (iterator.hasNext())
            Sx.puts("\nhasNext is true");
        else {
            Sx.puts("\nhasNext is false, so calling next() gets this exception:");
            try {
                iterator.next();
            } catch (Exception ex) {
                Sx.puts("    " + ex);
            }
        }
        
        
        Sx.puts("RandomIterator nexting 10 (non-unique) values from 6:");
        long seed = System.currentTimeMillis();
        RandomIterator<Integer> rit = new RandomIterator<>(A, 10, seed);
        rit.forEachRemaining(showIt);
        Sx.puts();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        Sx.puts("Java 8 IntStream: random.ints().limit(10).forEach(System.out::println);");
        Random random = new Random();
        random.ints().limit(4).forEach(System.out::println);
        Sx.puts();

        unit_test();
    }
}

class ShowIt implements Consumer<Integer>
{
    @Override
    public void accept(Integer t) {
        Sx.printOne(t);
    }
}
