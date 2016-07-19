// ArrayAlgo.java       Author: Sprax LInes   2011.11
package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class Arrays2dRotate
{
    /** 
     * Rotate a square 2D-array of int-values clockwise, element by element.
     * @param A     the square 2D-array
     */
    public static void rotateClockwise(int A[][])
    {
        if (A == null || A.length < 2)
            return;
        rotateClockwise(A, 0, 0, A.length);
    }

    /**
     * Rotate a square 2D-array (or sub-array) of int-values clockwise.
     * 
     * @param array the square 2D-array
     * @param begRow index of the first row in the square (sub)array to be rotated
     * @param begCol index of the first column in the square (sub)array to be rotated
     * @param size the length of the sides of the whole array, or of the
     *            square sub-array that is to be rotated.
     *            Note: We do not check but simply assume that every row in A has the same length.
     */
    public static void rotateClockwise(int array[][], int begRow, int begCol, int size) {
        if (array == null || array.length < 2)
            return;
        int endRow = begRow + size;
        int endCol = begCol + size;
        if (begRow < 0 || begCol < 0 || endRow > array.length || endCol > array[0].length)
            throw new IllegalArgumentException("bad offset(s) or size");
        
        for (int row = begRow, col = begCol; begRow < --endRow && begCol < --endCol; ++begRow, ++begCol) {
            
            // Save upper left corner value and shift leftmost column up one place
            int tmp = array[begRow][begCol];
            for (; row < endRow; row++)
                array[row][col] = array[row + 1][col];
            
            // Shift bottom row left
            for (; col < endCol; col++)
                array[row][col] = array[row][col + 1];
            
            // Shift rightmost column up
            for (; row > begRow; row--)
                array[row][col] = array[row - 1][col];
            
            // Shift top row right, but leave one place open
            for (; col > begCol+1; col--)
                array[row][col] = array[row][col - 1];
            
            // Put saved upper left corner value in its rotated place (shifted 1 right)
            array[row][col] = tmp;
        }            
    }
    
    
	public static int unit_test(int level) 
	{
		String   testName = Arrays2dRotate.class.getName() + ".unit_test";  
		Sz.begin(testName);;  
		int numWrong = 0;
		
        int iA[][] = {
                {  1,  2,  3,  4,  5 },
                {  6,  7,  8,  9, 10 },
                { 11, 12, 13, 14, 15 },
                { 16, 17, 18, 19, 20 },
                { 21, 22, 23, 24, 25 },
        };
        
        int iB[][] = {
                {  6,  7,  2,  3,  4 },
                { 11, 12,  7,  8,  5 },
                { 16, 17, 13,  9, 10 },
                { 21, 18, 19, 14, 15 },
                { 22, 23, 24, 25, 20 },
        };
        
        Sx.putsArray("Original  2D array:\n", iA);
        rotateClockwise(iA);
        Sx.putsArray("RotatedCW 2D array:\n", iA);
        numWrong += ArrayDiffs.sumOfSquaredDifferences(iA, iB);

		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}
}

