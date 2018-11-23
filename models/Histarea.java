package sprax.models;
import java.util.Stack;
import sprax.sprout.Sx;

public class Histarea
{
    static int histarea_ops(int histogram[], int length)
    {
        if (length < 3)
            return 0;
        int area = 0;

        int lenMinus1 = length - 1;
        Stack<Integer> dropStack = new Stack<>();
        int heightPrev = histogram[0];
        for (int j = 1; j < lenMinus1; j++) {       // Skip the first and last entries.
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

    static int histarea_opt(int histo[], int length)
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


    static int histarea(int histo[], int length)
    {
        int area = 0;
        if (length > 2) {
            int leftMaxes[] = new int[length];
            int height, maxHeight = Integer.MIN_VALUE;
            for (int j = 0; j < length; j++) {  // Don't skip the last entry.
                height = histo[j];
                if (maxHeight < height)
                    maxHeight = height;
                leftMaxes[j] = maxHeight;
            }
            maxHeight = Integer.MIN_VALUE;
            for (int j = length; --j > 0;) {   // Do skip the first entry.
                height = histo[j];
                if (maxHeight < height)
                    maxHeight = height;
                if (maxHeight > leftMaxes[j])
                    area += leftMaxes[j] - height;
                else
                    area += maxHeight - height;
            }
        }
        return area;
    }
    
    // default values:
    static final String defProgramName = "histarea";
    
    public static void main(String[] args)
    {
        Sx.puts(Histarea.class.getName() + ".main");
        final String programName = args.length > 0 ? args[0] : defProgramName;
        int histograms[][] = {
                {    0,    -1,      2,     3,     -4,     4,     4,     2,     7,     0 },
                {   -1,     2,     32,    32,     -4,     4,    44,     2,    38,     0 },
                {   -1,    34,      3,   -14,      4,     0,    -8,    17,     0,    -1 },
        };
        for (int i = 0; i < histograms.length; i++) {
            int histo[] = histograms[i];
            int length = histo.length;
            for (int j = 0; j < length; j++)
                System.out.format("% 4d ", histo[j]);
            int area = histarea(histo, length);
            int areo = histarea_opt(histo, length);
            int ares = histarea_ops(histo, length);
            System.out.format("=> %s: % 5d,  %s: % 5d,  %s: % 5d\n", "def", area, "opt", areo, "stk", ares);
        }
    }
}
