package sprax.strings;

import sprax.Sx;

/*
TODO:
You have two arrays A and B of strings. In the array B all element 
are from A except one. ex:
A = {"abc", "bcd", "dpr"};
B = {"abc", "mnp", "bcd", "dpr"};
You have find out the string which is extra in B in O(n) time.
In the above example it is "mnp".
9
Country: India
Interview Type: In-Person
Tags: Microsoft » Algorithm  » Software Engineer / Developer
Question #11623959 (Report Dup) | Edit | History

how about hash table? hash all the strings from
shorter list and then check from longer list

With Sorting O(nlogn)
With Hash table O(n) provided there are no collisions. 
With linear probing and all colliding the (worst case) it will be O(n^2)

They asked for other approach than hashing. 
Suffix tree is one of the best way of string matching. 
With suffix tree we can get a O(n) solution. 
Can you think of any other way of doing it?



If we associate an integer value with every string (maybe a hash value) 
in 2 arrays and subtract the respective sums of hash values then we 
are left with the hash value of extra string and the string can be found
 by an extra pass in array B (assuming no collisions). Here extra space is O(1)



easy way to do it is iterate over 2 array and convert 1st array to list using 
asList() den use list.contains(each value of the 2nd).
It returns false if it is not present in 1st and present in 2 nd
*/


/** find the First Maximal Substring Of Unique Letters */
public class FmSoul
{   
   /**
   * The FM SOUL problem. 
   * return the start index and length of the first maximal non-repetitive 
   * substring, that is, of the first longest substring that contains no more 
   * than one of each character.  Loosely speaking, we seek the string's SOUL,
   * or Substring Of Unique Letters.
   * @param str
   * @return int[2]==[startIndex, length] of 1st maximal substring of unique chars
   */
  public static final int NUM_CHARS = 256;
  public static int[] longestSubStringOfUniqueChars(String str)
  {
    // Using 0-based positions of chars in string, we must initialize
    // the lastPos array to positions < 0.
    int  lastPos[] = new int[NUM_CHARS];
    for (int j = 0; j < NUM_CHARS; j++)
      lastPos[j] = -1;
    
    int  curBeg = 0, maxBeg = 0;
    int  curLen = 0, maxLen = 0;
    char arr[] = str.toCharArray();
    for (int j = 0; j < arr.length; j++) {
      char cL = arr[j];
      if (lastPos[cL] >= 0) {
        // The letter cL at j also appeared at lastPos[cL], so the current
        // substring of unique letters ends at j-1, and the next candidate
        // for a maximal SOUL begins at lastPos[cL] + 1.
        
        // If the SOUL that just ended because of a repeated char is longer 
        // than the previous longest, save its starting position and length.
        if (maxLen < curLen) {
          maxBeg = curBeg;
          maxLen = curLen;
        }
        curBeg = lastPos[cL] + 1;
        curLen = j - curBeg + 1;
      } else {
        curLen++;
      }
      lastPos[cL] = j;
    }
    // If the SOUL that just ended because we reached the end of the input
    // string is the longest, save it as above.
    if (maxLen < curLen) {
      maxLen = curLen;
      maxBeg = curBeg;
    }
    int    ret[] = {maxBeg, maxLen};
    return ret;
  }
  
  public static int[] longestSubStringOfUniqueCharsOrd(String str)
  {
    // Using ordinal positions of chars in the string, that is, 
    // 1-based indexing of lastOrdPos, we can initialize the
    // lastOrdPos array to all 0's
    int  lastOrdPos[] = new int[NUM_CHARS]; // initialized to all 0's
    int  curBeg = 1, maxBeg = 1;            // ordinal pos starts at 1, not 0
    int  curLen = 0, maxLen = 0;            // lengths are still absolute
    char arr[] = str.toCharArray();
    for (int j = 1; j <= arr.length; j++) {
      char cL = arr[j-1];
      if (lastOrdPos[cL] > 0) {
        // The letter cL at j also appeared at lastOrdPos[cL], so the current
        // substring of unique letters ends at curPos-1, and the next candidate
        // for a maximal SOUL begins at lastOrdPos[cL] + 1.
        
        // If the SOUL that just ended because of a repeated char is longer 
        // than the previous longest, save its starting position and length.
        if (maxLen < curLen) {
          maxLen = curLen;
          maxBeg = curBeg;
        }
        curBeg = lastOrdPos[cL] + 1;  // same as for 0-based indexing
        curLen = j - curBeg + 1;      // same as for 0-based indexing
      } else {
        curLen++;
      }
      lastOrdPos[cL] = j;      
    }
    // If the SOUL that just ended because we reached the end of the input
    // string is the longest, save it as above.
    if (maxLen < curLen) {
      maxLen = curLen;
      maxBeg = curBeg;
    }
    int    ret[] = {maxBeg - 1, maxLen};   // return 0-based index, not the ordinal!
    return ret;
  }
  
  public static int test_longestSubStringOfUniqueChars(String str) 
  {
    int jz0[] = longestSubStringOfUniqueChars(str);
    System.out.format("0: The FM SOUL in %s is %s at position %d, length %d\n"
        , str, str.substring(jz0[0], jz0[0]+jz0[1]), jz0[0], jz0[1]);
    int jz1[] = longestSubStringOfUniqueCharsOrd(str);
    System.out.format("1: The FM SOUL in %s is %s at position %d, length %d\n"
        , str, str.substring(jz1[0], jz1[0]+jz1[1]), jz1[0], jz1[1]);
    
    return Math.abs(jz0[0] - jz1[0]) + Math.abs(jz0[1] - jz1[1]);
  }
  
  public static int test_fm_soul() 
  {
      String  testName = FmSoul.class.getName() + ".test_fm_soul";
      Sx.puts(testName + " BEGIN");    
      
      // FM SOUL
      Sx.puts("The FM SOUL detector: find the First Maximal Substring Of Unique Letters\n" 
              + "(chars, actually) in any given string.");
      String str = "abcabc";
      test_longestSubStringOfUniqueChars(str);
      test_longestSubStringOfUniqueChars(str + "dd");
      test_longestSubStringOfUniqueChars(str + "ded");
      test_longestSubStringOfUniqueChars(str + "dedEDCBA");
      
      Sx.puts(testName + " END");    
      return 0;
  }  
  
  public static void test_append() 
  {
      StringBuffer sb = new StringBuffer();
      String fullStr = "fullStr";
      String nullStr = null;
      String emptyStr = "";
      sb.append(fullStr).append('.');
      sb.append(emptyStr).append('.');
      sb.append(nullStr).append('.');
      Sx.puts(sb);
  }  
  
  public static int unit_test() 
  {
      String  testName = FmSoul.class.getName() + ".unit_test";
      Sx.puts(testName + " BEGIN");    
      
      test_append();
      test_fm_soul();
      
      Sx.puts(testName + " END");    
      return 0;
  }  
  
  public static void main(String[] args) 
  {
      unit_test();
  }  
}
