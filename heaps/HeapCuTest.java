package sprax.heaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import sprax.sprout.Sx;

public class HeapCuTest
{
    
    static class HeapCud extends HeapCu
    {
        private static final long serialVersionUID = 1L;
        
        HeapCud(Collection<?> c) { super(c); }
        
        public HeapCud(Comparator<?> comp) {
            super(comp);
        }
    }
    
    static class IntCmp implements Comparator<Integer>
    {
        @Override
        public int compare(Integer arg0, Integer arg1) {
            return arg0 != null ? arg0.compareTo(arg1) : 0;
        } 
    }
    
    public static void main(String[] args) 
    {
        Sx.puts(HeapCuTest.class.getName() + ".main");
        int count = 7;
        Collection<Integer> ali = new ArrayList<Integer>(count);
        for (int j = 0; j < count; j++)
            ali.add(count - j);
        
        Iterator<Integer> it = ali.iterator();
        while (it.hasNext())
            Sx.print(it.next());
        Sx.puts();
        
        Comparator<Integer> comp = new IntCmp();;
        HeapCu hcu = new HeapCud(comp);
        hcu.addAll(ali);      // calls rebuildHeap
        //hcu.rebuildHeap();
        
        Iterator<Integer> ih = (Iterator<Integer>) hcu.iterator();  // TODO: does not work.
        while (it.hasNext())
            Sx.print(it.next());
        Sx.puts();    
        
        for (int j = 0; j < count; j++)
            Sx.print(hcu.get(j));
        Sx.puts();
        
    }
    
}
