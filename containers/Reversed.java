package sprax.containers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import sprax.sprout.Sx;

/**
 * 
 * @author Erick Robertson, on StackOverflow.
 *         http://stackoverflow.com/questions/1098117/can-one-do-a
 *         -for-each-loop-in-java-in-reverse-order
 *
 * @param <T>
 */
public class Reversed<T> implements Iterable<T>
{
    private final List<T> original;
    
    public Reversed(List<T> original)
    {
        this.original = original;
    }
    
    public Iterator<T> iterator()
    {
        final ListIterator<T> i = original.listIterator(original.size());
        
        return new Iterator<T>() {
            public boolean hasNext()
            {
                return i.hasPrevious();
            }
            
            public T next()
            {
                return i.previous();
            }
            
            public void remove()
            {
                i.remove();
            }
        };
    }
    
    public static <T> Reversed<T> reversed(List<T> original)
    {
        return new Reversed<T>(original);
    }
    
    public static int test_Reversed()
    {
        
        List<String> strList = new LinkedList<String>();
        strList.add("one");
        strList.add("two");
        strList.add("three");
        strList.add("four");
        
        Sx.puts("Forward:");
        Sx.putsList(strList);
        Sx.puts("Reversed:");
        for (String s : reversed(strList)) {
            Sx.puts(s);
        }
        return 0;
    }
    
    public static int unit_test()
    {
        String testName = Reversed.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        test_Reversed();
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}