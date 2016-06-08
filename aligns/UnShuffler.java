package sprax.aligns;

import java.util.Arrays;
import java.util.Comparator;

import sprax.Sx;

/**
 * TODO: Not done!  This is analogous to unshuffling a natural image (like 
 * a photograph of a landscape) whose rows of pixels have been shuffled into
 * a random order.  See ocvRandomRows.cpp.
 * @author sprax
 *
 * @param <T>
 */
class ReverseComparator<T> implements Comparator<T>
{
  Comparator<T> mComparator;
  ReverseComparator (Comparator<T> comp) {
    mComparator = comp;
  }
  
  @Override
  public int compare(T one, T other) {
    return mComparator.compare(other, one);
  }
}

class AbsSumCmp implements Comparator<int[]>
{
  @Override
  public int compare(int[] aa, int[] bb) {
    int suma = 0, sumb = 0;
    if (aa.length == bb.length)
      for (int j = 0; j < aa.length; j++) {
        suma += aa[j] < 0 ? -aa[j] : aa[j];
        sumb += bb[j] < 0 ? -bb[j] : bb[j];
      }
    return suma - sumb;
  }
}

public class UnShuffler 
{
  static int data5x5[][] = {
    { 0, 0, 1, 0, 0 },
    { 0, 0, 1, 1, 0 },
    { 0, 1, 1, 1, 0 },
    { 0, 1, 1, 1, 1 },
    { 1, 1, 1, 1, 1 },
  };


  public static int unit_test() 
  {
    Comparator<int[]> absSumCmp = new AbsSumCmp();
    ReverseComparator<int[]> revCmp = new ReverseComparator<int[]>(absSumCmp);
    
    String  testName = UnShuffler.class.getName() + ".unit_test";
    Sx.puts(testName + " BEGIN");    
 
    Sx.putsArray("Before sort:\n", data5x5);
    Arrays.sort(data5x5, absSumCmp);
    Sx.putsArray("After sort:\n", data5x5);

    Arrays.sort(data5x5, revCmp);
    Sx.putsArray("After rev sort:\n", data5x5);

    Sx.puts(testName + " END");    
    return 0;
  }
  
  public static void main(String[] args) { unit_test(); }
}
