package sprax.models;

import sprax.sprout.Sx;

public class Histarea
{
    static int histarea_opt(int histogram[], int length)
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
        
        int histogram[] = { 100, -1, 2, 3, -4, 4, 4, 2, 7, 0, 400 };
        int length = histogram.length;
        
        int area = histarea(histogram, length);
        System.out.format("%s got area %d from histogram:\n", programName, area);
        for (int j = 0; j < length; j++)
            System.out.format("%d  ", histogram[j]);
        System.out.println();
    }
}
