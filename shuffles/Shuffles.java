package sprax.shuffles;

import sprax.arrays.ArrayAlgo;
import sprax.series.RunningMaxTest;
import sprax.Sx;
import sprax.Sz;

public class Shuffles {
    /**
     * Question :
     * Suppose we have an array like
     * 1,2,3,4,5,a,b,c,d,e where we have always even number of elements.First half of the elements
     * are integers and second half are alphabets we have to change it to like
     * 1,a,2,b,3,c,4,d,5,e in place i.e no use of any extra space, variables are allowed ..
     *
     */

    /** in-shuffle solution:

    Step 1) Find an m such that 2m+1 = 3**k <= 2n < 3**(k+1)
    Step 2) Right cyclic shift A[m+1 ... n+m] by m.
    Step 3) Foreach i = 0 to k - 1, considering only the Array[1...2m] start at 3i and 'follow the cycle'.  
    Step 4) Recurse  on A[2m+1...2n] 
     */


    public static void inShuffleArray(char[] A)
    {
        int len = A.length;
        if (len % 2 == 1 || len < 10) {
            Sx.puts("inShuffArray: A.length must be even and >= 10");
            return;
        }

        int m = 0;
        for (int k = 2, pow3_k = 9, pow3_kp1 = 27; k < 8; k++) { 
            if (pow3_k <= len && len < pow3_kp1) {
                m = (pow3_k - 1) / 2;
                break; 
            }
        } // TODO: what if 3**8 isn't big enough?


    }


    public static void mixArray(char[] A, int start, int end){
        assert((end - start + 1) % 2 == 0);
        if (start +1 >= end)
            return;

        // swap every element after start with (end - start)/2 th item after it
        // 1 2 3 4 5 a b c d e  becomes 1 a b c d 2 3 4 5 e
        int distance = (end - start) / 2;
        for(int i = start + 1; i + distance < end; i++) {
            arraySwap(A, i, i + distance); 
        }

        // swap element back to origin place except head and tail
        // 1 a b c d 2 3 4 5 e becomes 1 a 2 3 4 b c d 5 e
        distance = (end - start - 2) / 2;
        for(int i = start + 2; i + distance < end - 1; i++) {
            arraySwap(A, i, i + distance);
        }

        // recursive call to mix the rest
        mixArray(A, start + 2, end - 2);
    }

    public static void test_mixArrayShuffle(char A[])
    {
        Sx.putsArray("mixArray(", A, ") ==>");
        mixArray(A, 0, A.length -1);
        Sx.putsArray("         ", A);
    }


    public static void arraySwap(char[] A, int i, int j) {
        char temp = A[i];
        A[i] = A[j];
        A[j] = temp;
        return;
    }

    public static int test_nonRandomShuffle()
    {
        char[] A = "123456789abcdefghi".toCharArray();
        test_mixArrayShuffle(A);
        int len = 21;

        char[] B = new char[len*2];
        //      // pre-shuffled
        //      for (int j = len*2; --j >= 0; ) {
        //        B[j--] = (char)(j + 'a');
        //        B[j]   = (char)(j + 'A');
        //      }

        // un-shuffled
        for (int j = 0; j < len; j++)
            B[j] = (char)(j + 'A');
        for (int j = 0; j < len; j++)
            B[j+len] = (char)(j + 'a');
        test_mixArrayShuffle(B);

        inShuffleArray(B);
        return 0;
    }    

    public static int unit_test() 
    {
        String testName = RunningMaxTest.class.getName() + ".unit_test";
        Sz.begin(testName);
        Sx.puts(ArrayAlgo.class.getName() + ".unit_test");  
        int numWrong = 0;

        numWrong += test_nonRandomShuffle();

        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }

}
