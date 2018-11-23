package sprax.models;

import sprax.sprout.Sx;

public class Histarea
{
    static int histarea_opt(int histogram[], int length)
    {
        if (length < 3)
            return 0;
        int area = 0;
        int lenMinus1 = length - 1;
        int maxFromLeft[] = new int[lenMinus1];
        int leftMax = maxFromLeft[0] = histogram[0];
        for (int j = 1; j < lenMinus1; j++) {  // Skip the first and last entries.
            int height = histogram[j];
            if (leftMax < height)
                leftMax = height;
            maxFromLeft[j] = leftMax;
        }
        int rightMax = histogram[lenMinus1];
        for (int j = lenMinus1; --j > 0;) {   // Skip the last and first entries.
            int height = histogram[j];
            if (rightMax < height)
                rightMax = height;
            leftMax = maxFromLeft[j];
            if (rightMax > leftMax)
                area += leftMax - height;
            else
                area += rightMax - height;
        }
        return area;
    }

    static int histarea(int histogram[], int length)
    {
        int area = 0;
        if (length > 2) {
            int maxFromLeft[] = new int[length];
            int height, maxHeight = Integer.MIN_VALUE;
            for (int j = 0; j < length; j++) {  // Don't skip the last entry.
                height = histogram[j];
                if (maxHeight < height)
                    maxHeight = height;
                maxFromLeft[j] = maxHeight;
            }
            maxHeight = Integer.MIN_VALUE;
            for (int j = length; --j > 0;) {   // Do skip the first entry.
                height = histogram[j];
                if (maxHeight < height)
                    maxHeight = height;
                if (maxHeight > maxFromLeft[j])
                    area += maxFromLeft[j] - height;
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
            int histogram[] = histograms[i];
            int length = histogram.length;
            for (int j = 0; j < length; j++)
                System.out.format("% 4d ", histogram[j]);
            int area = histarea(histogram, length);
            int areo = histarea_opt(histogram, length);
            System.out.format("=> %s: % 5d,  %s: % 5d\n", "def", area, "opt", areo);
        }
    }
}
