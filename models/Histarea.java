///@file: histarea.java
/// Example command to build and run this program:
///     javac Histarea.java && java Histarea

package sprax.models;
import java.lang.Math;
import java.util.Stack;

/// Compute the area trapped between the columns of a histogram.
/// Making this class generic <T extends Number> is not worthwhile, 
/// because Number does not give you overloaded arithmetic operators:
/// https://stackoverflow.com/questions/3873215/can-i-do-arithmetic-operations-on-the-number-baseclass
public class Histarea
{
    /// An elementary solution, not "overly" optimized.
    static int histarea_basic(int histo[], int length)
    {
        int area = 0;
        if (length > 2) {
            int leftMaxes[] = new int[length];      // 1 more than really needed
            int maxHeight = Integer.MIN_VALUE;      // Could init leftMax[0] = maxHeight = histo[0]
            for (int j = 0; j < length; j++) {      // Could skip the last entry.
                leftMaxes[j] = maxHeight = Math.max(maxHeight, histo[j]);
            }
            int rightMax = Integer.MIN_VALUE;   // Could init maxHeight = histo[length - 1] and start loop at j=length-1
            for (int j = length; --j >= 0;) {   // Could skip the first (j = 0) entry.
                rightMax = Math.max(rightMax, histo[j]);
                area += Math.min(rightMax, leftMaxes[j]) - histo[j];
            }
        }
        return area;
    }

    // Two loops, optimized only by minimizing the loop lengths and operations
    static int histarea_loops(int histo[], int length)
    {
        if (length < 3)
            return 0;
        int area = 0;
        int lenMinus1 = length - 1;
        int leftMaxes[] = new int[lenMinus1];
        int leftMax = leftMaxes[0] = histo[0];
        for (int j = 1; j < lenMinus1; j++) {  // Skip the first and last entries.
            int height = histo[j];
            if (leftMax < height)
                leftMax = height;
            leftMaxes[j] = leftMax;
        }
        int rightMax = histo[lenMinus1];
        for (int j = lenMinus1; --j > 0;) {   // Skip the last and first entries.
            int height = histo[j];
            if (rightMax < height)
                rightMax = height;
            leftMax = leftMaxes[j];
            if (rightMax > leftMax)
                area += leftMax - height;
            else
                area += rightMax - height;
        }
        return area;
    }


  /// Single pass with a stack for minimal back-tracking
  /// Worst-case complexity is Theta(N**2) space and Theta(N) more space.
  /// For example:
  /// [7, 5, 6, 4, 5, 3, 4, 2, 3, 1, 2, 0, 2, 1, 3, 2, 4, 3, 5, 4, 6, 5, 7]
  /// But the expected complexity on uniformly random input is more like
  /// O(N log N) time and O(log N) more space,
  /// because at each new index M, with value H[M],
  /// the probability that you must backtrack at all is basically 1/2,
  /// and then the probability that you must pop the stack and backtrack again,
  /// after adding the area trapped by the closest drop point, is also 1/2,
  /// and so on.  Since the probability that you must continue backtracking
  /// is cut in half at each step, the sum of those probabilities increases
  /// as log2(N).  The added complexity for each backtracking step is constant
  /// (you just multiply the lesser bounding height by the difference in indices),
  /// so the total expected complexity is bounded by O(N log2 N), at least for
  /// a uniformly "random" histogram.
  ///
  /// Note that if you do a point-wise sum instead of a single multiply
  /// at each backtracking step, the expected complexity will be O(N * (log2 N)**2),
  /// that is, N times the square of log2(N).  That is because at each backtracking
  /// step starting from index M, the probability that you must backtrack all the
  /// way to index J, where 0 < J < M, is the probability that H[J] >= H[M] times
  /// the probability that H[K] < H[M] for all K where J < K < M,
  /// which is basically (1/2)**(M - J - 1).  Once again, the sum of a series
  /// of terms bound by inverse powers of 2 grows as log2(N).
  ///
  /// Would "typical" histogram data be better or worse for this algorithm
  /// than "random" data?  My guess is better, due to less variance.
  /// Time series such as stock prices are likely to show trends at
  /// various sample scales, which tends to reduce the need for backtracking.
    static int histarea_stack(int histogram[], int length)
    {
        if (length < 3)
            return 0;
        int area = 0;
        Stack<Integer> dropStack = new Stack<>();
        int heightPrev = histogram[0];
        for (int j = 1; j < length; j++) {       // Skip the first and last entries.
            int heightHere = histogram[j];
            if (heightHere < heightPrev) {
                dropStack.push(j);
            }
            else if (heightHere > heightPrev) {
                while ( ! dropStack.empty()) {
                    int jLeft = dropStack.peek();
                    int heightLeft = histogram[jLeft - 1];
                    if (heightHere > heightLeft) {
                        // System.out.format("\nAdding %d at j=%d", (j - jLeft)*(heightLeft - heightPrev), j);
                        area += (j - jLeft)*(heightLeft - heightPrev);
                        heightPrev = heightLeft;
                        dropStack.pop();
                    } else {
                        // System.out.format("\nAdding %d at j=%d", (j - jLeft)*(heightHere - heightPrev), j);
                        area += (j - jLeft)*(heightHere - heightPrev);
                        break;
                    }
                }
            }
            heightPrev = heightHere;
        }
        // System.out.println();
        return area;
    }
    // default values:
    static final String defProgramName = "histarea";

    public static void main(String[] args)
    {
        final String programName = defProgramName;
        System.out.println(Histarea.class.getName() + ".main called by: " + programName);
        int histograms[][] = {
            {   10,     0,    20,    30,    0,    60,    10,    50,    70,     0 },
            {    0,   -10,    20,    30,  -40,    40,    40,    20,    70,     0 },
            {   -1,     2,    32,    32,   -4,     4,    44,     2,    38,     0 },
            {   -1,    34,     3,   -14,    4,     0,    -8,    17,     0,    -1 },
            {   10,     0,    20,    30,    0,    60,    40,    50,    30,    70 },
            {   16,     3,     1,     2,   -2,     2,     1,     3,     2,    14 },
            { 8, 6, 7, 5, 6, 4, 5, 3, 4, 2, 3, 1, 2, 4, 2, 1, 3, 2, 4, 3, 5, 4, 6, 5, 7, 6, 8, 7}
        };
        for (int i = 0; i < histograms.length; i++) {
            int histo[] = histograms[i];
            int length = histo.length;
            for (int j = 0; j < length; j++)
                System.out.format("% 4d ", histo[j]);
            int area_b = histarea_basic(histo, length);
            int areo_l = histarea_loops(histo, length);
            int area_s = histarea_stack(histo, length);
            System.out.format("=> basic: % 5d,  loops: % 5d,  stack: % 5d\n"
                             , area_b, areo_l, area_s);
        }
    }
}
