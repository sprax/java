package sprax.series;

import java.util.Random;

import sprax.Sx;
import sprax.Sz;

public class RunningMaxTest 
{
    static Random sRandom = new Random();

    public static int test_RunningMaxVerbose(RunningMax rm, int[] series, String name, int verbose)
    {
        int max = -1;
        for (int num : series)
        {
            if (verbose > 1)
                Sx.printArray(name, rm.getWindow());
            max = rm.addNumAndReturnMax(num);
            if (verbose > 1)
                Sx.puts("\t+ " + num + "\t=>  " + max);
        }
        if (verbose > 0)
        {
            Sx.format("    %s:  size %d,  max %d \t: ", name, rm.getSize(), rm.getMax());
            Sx.putsArray(rm.getWindow());
        }
        return max;
    }

    public static int test_time_RunningMax(RunningMax rm, int[] series, String name)
    {
        int max = -1;
        long begTime = System.currentTimeMillis();
        for (int num : series)
        {
            max = rm.addNumAndReturnMax(num);
        }
        long endTime = System.currentTimeMillis();
        long runTime = endTime - begTime;
        Sx.format("test_time_RunningMax: %s(%d)  length %d  max %d: time %d MS\n"
                , name, rm.getSize(), series.length, max, runTime);
        return max;
    }

    public static void test_smallSeries(RunningMax rm)
    {
        int[] smallSeries = { -1, 1, 0, -2, 2, -3, 2, 0, 0, 0, 0, 0, 2, 4, -4, 5, -5, 4, 4, 3, 2, 1 };
        String name = String.format("%s(%d)", rm.getClass().getSimpleName(), rm.getSize());
        test_RunningMaxVerbose(rm, smallSeries, name, 2);
    }

    public static void test_largeSeries(RunningMax rm, int[] largeSeries)
    {
        String name = String.format("%s(%d)", rm.getClass().getSimpleName(), rm.getSize());
        test_RunningMaxVerbose(rm, largeSeries, name, 1);
    }

    public static void unit_test(int largeNum)
    {
        String testName = RunningMaxTest.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        test_smallSeries(new RunningMaxLazyRescan(3));
        test_smallSeries(new RunningMaxTreeMap(3));

        int[] largeSeries = new int[largeNum];
        for (int j = 0; j < largeNum; j++)
        {
            largeSeries[j] = sRandom.nextInt(199) - 99;
        }

        test_largeSeries(new RunningMaxLazyRescan(7), largeSeries);
        test_largeSeries(new RunningMaxTreeMap(7),    largeSeries);

        RunningMax rm;
        int size = 10;
        rm = new RunningMaxLazyRescan(size); 
        test_time_RunningMax(rm, largeSeries, rm.getClass().getSimpleName() );

        rm = new RunningMaxTreeMap(size); 
        test_time_RunningMax(rm, largeSeries, rm.getClass().getSimpleName() );

        size = 100;
        rm = new RunningMaxLazyRescan(size); 
        test_time_RunningMax(rm, largeSeries, rm.getClass().getSimpleName() );

        rm = new RunningMaxTreeMap(size); 
        test_time_RunningMax(rm, largeSeries, rm.getClass().getSimpleName() );
        
        Sz.end(testName, 0);

    }

    public static void main(String[] args) { unit_test(9000000); }
}
