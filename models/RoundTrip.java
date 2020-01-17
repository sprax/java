package sprax.models;

import sprax.sprout.Sx;

public class RoundTrip
{
    /**
     * TODO: If the tank is not initially empty, but contains X gallons
     * of fuel, how does that change the problem below? [Not so much.]
     *
     * What if the MPG is not fixed for the whole route, but only
     * for each station? [It effectively just changes the distances.]
     */

    /**
     * Given a circular truck route with N fuel stations numbered 0 through N-1,
     * what is the first station from which a truck can complete the route,
     * assuming that its tank can hold enough fuel to drive the whole route,
     * but that it is initially empty. You are given two sets of data:
     *
     * 1. Gallons of fuel that each station will give.
     * 2. Miles from that station to the next.
     *
     * Assume a fixed MPG between all stations,
     * and that the truck stops and fuels up at every station.
     * Calculate the first point from where a truck will be able to
     * complete the route.
     *
     * Give O(N) solution. You may use O(N) extra space [but you don't
     * actually need it].
     *
     * HINT: For a solution to exist, the total number of gallons G
     * times the MPG must be >= total distance D: G*mpg >= D.
     * If that condition is met, then the solution is the starting index
     * of the first contiguous sub-set of stations wherein the fuel
     * never runs out between stations, i.e. where G(t)*mpg - D(t) >= 0
     * for all time t. Of course, you don't really need to represent
     * continuous time, but you do have to regard the array of
     * stations as wrapping around from N-1 to 0. Or do you?
     *
     * If you think about it correctly, you don't need to wrap
     * around in your actual calculation (which would, in a sense,
     * give an O(2*N) solution), but only in the conceptualization
     * you use to set up the calculation.
     */
    public static int circleStart(final double fuel[], final double dist[], double mpg)
    {
        int length = fuel.length;
        if (length != dist.length) {
            System.out.format("cirlceStart: different array lenghts %d and %d\n"
                    , fuel.length, dist.length);
            return -2;
        }
        double excessGasHere, excessGasTemp = 0.0, excessGasTotal = 0.0;
        int start = 0;
        // 1st pass: get local and total excesses of fuel compared to miles
        // This is very similar to Kadane's algorithm.
        for (int j = 0; j < length; j++) {
            excessGasHere = fuel[j] - dist[j] / mpg;
            excessGasTemp += excessGasHere;
            excessGasTotal += excessGasHere;
            if (excessGasTemp < 0.0) {  // If, having started at start, we would not
                excessGasTemp = 0.0;    // get enough fuel here to make it to the next
                start = j + 1;          // station, then try tne next possible starting 
            }                           // point, and reset the running excess to zero.
        }
        // For any station k s.t. old start < k < new start, we'd also run out
        // of fuel before arriving at station j+1, because the excess from old
        // start to k had to be >= 0, so starting at k instead of old start
        // could only result in less excess at j, that is, not enough fuel to
        // get to station j + 1.
        if (excessGasTotal < 0.0) { // Less than enough total fuel for total idistance?
            return -1;                // Then no starting point will work.
        }
        // Still here?  We know there is enough fuel, so there is
        // definitely a solution, and start has now been set to the first
        // such solution.  The proof is an easy argument by contradiction.
        return start;
    }

    public static int test_circleStart(int numPoints, int numTrials)
    {
        double fuel[] = new double[numPoints], fuelTot = 0;
        ;
        double dist[] = new double[numPoints], distTot = 0;
        for (int j = 0; j < numPoints; j++) {
            fuel[j] = 1.0 + Math.cos(j);
            dist[j] = 1.0;
            fuelTot += fuel[j];
            distTot += dist[j];
        }
        fuel[3] += fuel[0];
        fuel[2] -= fuel[0];
        if (distTot > fuelTot) {
            double dif = distTot - fuelTot + 0.01;
            fuel[numPoints - 1] += dif;
            fuelTot += dif;
        }
        double mpg = 1.0;
        int start = circleStart(fuel, dist, mpg);
        Sx.putsArray("circleStart fuel:", fuel, " (" + fuelTot + ")");
        Sx.putsArray("        distance:", dist, " (" + distTot + ") got start = " + start);

        double G[] = {  1,  2,  3,  4,  5 };
        double D[] = {  4,  1,  2,  3,  4 };
        //double F[] = { -3, -2, -1,  0,  1 };      // excess starting at index 0
        //double H[] = {      1,  2,  3,  4,  1 };  //        starting at index 1
        start = circleStart(G, D, mpg);
        Sx.putsArray("circleStart fuel:", G, " (" + fuelTot + ")");
        Sx.putsArray("        distance:", D, " (" + distTot + ") got start = " + start);
        return Math.min(0, start);
    }

    public static int unit_test()
    {
        int numPoints = 7;
        int numTrials = 5;
        return test_circleStart(numPoints, numTrials);
    }

    public static void main(String[] args) {
        unit_test();
    }

}
