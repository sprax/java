package sprax.robopaths;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;


import javax.vecmath.Point2d;

import sprax.graphs.Graph;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Problem:
 * Given a rectangle with lower-left coordinates (x0, y0) and upper-right 
 * coordinates (x1, y1) and N sensors at coordinates {(m,n)} inside the 
 * rectangle, find a path from the left side to the ride side that avoids
 * all sensors.   Each sensor can sense in a circular region of radius R 
 * about its center (m,n).  Avoiding the regions around all sensors, the
 * path must reach from left side of rectangle to its right side (i.e. it
 * can start from any point with x coordinate x0 and y coordinate 
 * satisfying y0 < y < y1.  The path may end at any point (x, y) with x = x1
 * and y0 < y < y1.
 *  
 * Write an algorithm to find path (possibly shortest but not necessary) 
 * from start to end as described above. 
 * Note: all coordinates are represented as floating point numbers 
 * (choose type float or double).<br>
 * <blockquote><pre>
 *    Example:                                    (x1, y1)
 *    |----------------------------------------------|    
 *    |....................O.......O.................|end 
 *    |......O...............O.......................|    
 *    |........................O........O............|    
 *    |start.........O...............................|    
 *    |...................O................O.........|    
 *    |..........O.................O.................|    
 *    |----------------------------------------------|    
 * (x0, y0)
 * </pre></blockquote>
 * @author Sprax Lines
 */
public class AStarPath
{
    final Point2d corner0, corner1, sensors[];
    final double width, height, radius;
    final Rectangle2D.Double rect;
    final int minGridPathLength;
    final LinkedList<GridCell> minimalGridPath;
    final Point2d minimalCoordPath[];
    final double minGeomPathLength;
    
    double cellSize;
    int rows, cols, grid[][];
    Queue<GridCell> waveFront;
    
    /** Strategies:
     *  1) Discretize: Get an approximate solution by dividing the rectangle
     *  into a grid with cell size <= R, the radius of the sensors.  Then use
     *  ordinary wavefront algorithm, where all cells on the far right have 
     *  distance 0 from the goal, and sensor cells are treated as obstacles.
     *  NOTE 1: Using a cell size or approximately R or greater is very optimistic.
     *  If there is any doubt whether a path exists (because the sensors may
     *  be close enough together to form a blockade), a smaller cell size will
     *  help.  
     *  NOTE 2: Since the sensors are not actually constrained to be in cell 
     *  centers, this solution remains only approximate even if the cell size
     *  is made small compared to R.
     *  Thus:
     *  1a) Same as (1) but use cell size R/3, so that each sensor becomes
     *  an obstacle spanning 5 cells (Center, East, South, West, North).
     *  1b...) Same as (1) but use cell size R/5, etc...
     *  
     *  2) Potential function:
     *  2a) V(x,y) = 1 if distance(S, x, y) <= R or 0, where S is the set of 
     *  sensor coordinates.  The distance can be made easier to compute by
     *  sorting S first (by x then y), and noting that 
     *  (x**2 + y**2)**0.5 <= |x| + |y|
     *  2b) To avoid numerical edge cases, it may be better use an interpolated
     *  potential function: V(S) = Float.MAX_VALUE, and V(x,y) = R/distance(S,x,y)
     *  for (x,y) in R - S, so that V ~= 1.0 at sensor area boundaries. 
     *  
     *  Then, discretize the space as before, but this time we have not 
     *  implicitly assumed that sensors reside in cell centers, so any 
     *  blockades will be represented more accurately.  
     *  
     *  3) Create a Voronoi diagram based on the sensor points.
     *  Add segments along the top and bottom edges of the rectangle.
     *  Remove any edges that are (partly) within the radius R from any sensor
     *  (i.e. from the nearest sensor).  
     *  Search for a path along the remaining edges connecting left and
     *  right sides of the rectangle.  
     *  To get a minimal path, add up the edge lengths of each path,
     *  and select the one with a minimal sum.
     *  
     *  4) Bug algorithms: follow implicit walls around sensors and groups
     *  of sensors (blockades).
     *  
     *  5) Elastic path repelled from sensors, computed by relaxation.
     *  
     *  6) Some kind of dual graph constructed from the graph of sensors.
     */
    
    
    /** Ctor */
    public AStarPath(Point2d r0, Point2d r1, Point2d sensorPoints[], double sensorRadius) 
    {
        corner0 = r0;
        corner1 = r1;
        width = r1.x - r0.x;
        height = r1.y - r0.y;
        radius = sensorRadius;
        if (radius <= 0.0 || width < radius || height < radius)
            throw new IllegalArgumentException("bad dimensions");
        rect = new Rectangle2D.Double(r0.x, r0.y, width, height);
        sensors = Arrays.copyOf(sensorPoints, sensorPoints.length);     // defensive copy
        
        //Don't need to sort for grid algo:
        //Comparator<Point2d> comp = new ComparePointXY();
        //Arrays.sort(sensors, comp);
        
        createGrid();
        markSensorsInGrid();
        Sx.format("Sensors marked in a grid with %d rows and %d columns...\n",  rows, cols);
        //Sx.putsArray(grid);
        markGoalsInGrid();
        Sx.putsArray("Sensors and Goals marked:\n", grid);
        markDistancesInGrid();
        Sx.putsArray("Distances marked:\n", grid);
        int minRow = findMinDistanceRow();
        minimalGridPath = GridPath.pathToNearestGoal(grid, rows, cols, minRow, 0);
        minGridPathLength = minimalGridPath.size();
        minimalCoordPath = new Point2d[minGridPathLength];
        int j = 0;
        for (GridCell cell : minimalGridPath) {
            minimalCoordPath[j++] = new Point2d(rect.x + cell.col*cellSize, rect.y + cell.row*cellSize);
        }
        // Fix-up last point to be exactly on the right boundary:
        minimalCoordPath[minGridPathLength - 1].x = corner1.x;
        
        // Approximate geometric length using Manhattan distance (scaled grid distance):
        minGeomPathLength = minGridPathLength * cellSize;
    }
    
    
    float AStarDistance(Graph<Point2d> graph, Point2d start, Point2d goal)
    {
        final float INFINITY = Float.MAX_VALUE;
        
        // The set of nodes already evaluated.
        Set<Point2d> closedSet = new HashSet<>();
        
        
        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start node is known.
        Queue<Point2d> openSet = new PriorityQueue<>();
        openSet.add(start);   
        
        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        Map<Point2d, Point2d> cameFrom = new HashMap<>();
        
        
        // For each node, the cost of getting from the start node to that node,
        // with the unknown or default cost understood to be "infinity", i.e. MAX_FLOAT.
        Map<Point2d, Float> gScore = new HashMap<>();
        // The cost of going from start to start is zero.
        gScore.put(start, 0F);
        
        // For each node, the total cost of getting from the start node to the goal
        // by passing by that node. That value is partly known, partly heuristic 
        // with default value of Infinity.
        Map<Point2d, Float> fScore = new HashMap<>();
        
        // For the first node, that value is completely heuristic.
        double est = heuristic_cost_estimate(start, goal);
        fScore.put(start, (float)est);
        
        while (! openSet.isEmpty()) {
            Point2d current = openSet.remove();
            if (current == goal) {
                // success
                // return reconstruct_path(cameFrom, current)
                return fScore.get(current);
            }
            closedSet.add(current);
            Set<Point2d> neighbors = graph.getEdges(current);
            for (Point2d neighbor : neighbors) {
                if (! closedSet.contains(neighbor)) {
                    float tentative_score = gScore.getOrDefault(current, INFINITY) + (float) current.distance(neighbor);
                    if (! openSet.contains(neighbor))
                        openSet.add(neighbor);
                    else if (tentative_score < gScore.getOrDefault(neighbor, INFINITY)) {
                        // This path is the best until now. Record it.
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentative_score);
                        fScore.put(neighbor, gScore.get(neighbor) + (float) heuristic_cost_estimate(neighbor, goal));
                        
                    }
                }
            }
        }
        // failure
        return INFINITY;
    }
    
    /******************************
    function reconstruct_path(cameFrom, current)
        total_path := [current]
        while current in cameFrom.Keys:
            current := cameFrom[current]
            total_path.append(current)
        return total_path
     ****************************************************/
    
    double heuristic_cost_estimate(Point2d start, Point2d goal)
    {
        return start.distance(goal);
    }
    
    /** 
     * Use default grid cell size of C <= radius/3.0, which gives a minimal margin of safety.
     * If every sensor were located in the very center of a grid cell, then C = 2*R/5 would
     * be (barely) enough to contain a sensor inside a region 5 cells wide and tall.  But if
     * the sensor is off-center, it's radial reach may extend beyond those five cells.  For
     * example, if vertically centered but all the way right in the center cell, it's range
     * would extend half way into the next cell on the right.  So instead, use * C = R/3.
     * but approximate the sensor's circular domain by a region 7 cells across.
     * <p>
     * But to avoid differently sized (smaller) cells at the boundaries -- in particular, at
     * the vertical boundaries, where blockades need to be reckoned with, let's actually use
     * C = H/N where H = height and N is the maximum whole number s.t. height/N >= R/3.
     * Thus N = ifloor(3*H/R).
     * <pre> 
     *       _______                     
     *      _|_|_|_|_                     
     *    _|_|_|_|_|_|_                   
     *   |_|_|_|_|_|_|_|                  
     *   |_|_|_|S|_|_|_|   Even if sensor S is located on right boundary of the central             
     *   |_|_|_|_|_|_|_|   cell, its range reaches only to the right boundary of the
     *     |_|_|_|_|_|     right-most V-centered cell.
     *       |_|_|_|                        
     *   <---R--|--R--->                          
     *  </pre>
     */
    void createGrid() 
    {
        double approxCellSize = 3.0 * height / radius;
        double vertNumCells   = Math.floor(approxCellSize);
        double actualCellSize = height / vertNumCells;
        double horzNumCells   = Math.ceil(width/actualCellSize);
        rows = (int)Math.round(vertNumCells);
        cols = (int)Math.round(horzNumCells);
        grid = new int[rows][cols];
        cellSize = actualCellSize;
    }
    
    void markSensorsInGrid() 
    {
        for (Point2d ss : sensors) {
            int col = (int) Math.floor((ss.x - rect.x)/cellSize);   // remember x ~ column
            int row = (int) Math.floor((ss.y - rect.y)/cellSize);   // remember y ~ row
            markSensor(row, col);
        }
    }
    
    void markGoalsInGrid() 
    {
        waveFront = new LinkedList<GridCell>();
        for (int col = cols-1, row = 0; row < rows; row++) {
            if (grid[row][col] == 0) {    
                grid[row][col] = 1;
                addToWaveFront(row, col);               // Add only if cell is empty
            }
        }
    }
    
    int findMinDistanceRow() 
    {
        waveFront = new LinkedList<GridCell>();
        int minRow = 0, minDist = grid[0][0];
        for (int row = 1; row < rows; row++) {
            if (minDist > grid[row][0]) {    
                minDist = grid[row][0];
                minRow  = row;
            }
        }
        return minRow;
    }
    
    void markDistancesInGrid()
    {
        while (! waveFront.isEmpty()) {
            GridCell pt = waveFront.remove();
            int row = pt.row;
            int col = pt.col;
            int distance = grid[row][col] + 1;
            
            if (--col >= 0)                         // try West
                markDistance(row, col, distance);
            
            ++col;
            if (--row >= 0)                         // try North 
                markDistance(row, col, distance);
            
            if ((row += 2) < rows)                  // try South
                markDistance(row, col, distance);
            
            --row;
            if (++col < cols)                       // try East 
                markDistance(row, col, distance);
        }
    }
    
    private void markDistance(int row, int col, int distance) {
        if (grid[row][col] == 0) {
            grid[row][col] = distance;
            addToWaveFront(row, col);
        }
    }
    
    private void addToWaveFront(int row, int col)
    {
        waveFront.add(new GridCell(row, col)); 
    }
    
    void markSensor(int row, int col) 
    {
        if (row >= rows || col >= cols)
            return;
        markSensorRow(row    , Math.max(0, col - 3), Math.min(col + 4, cols), -1);
        int rr = row;
        if (++rr < rows) {
            markSensorRow(rr, Math.max(0, col - 3), Math.min(col + 4, cols), -1);
            if (++rr < rows) {
                markSensorRow(rr, Math.max(0, col - 2), Math.min(col + 3, cols), -1);
                if (++rr < rows) {
                    markSensorRow(rr, Math.max(0, col - 1), Math.min(col + 2, cols), -1);
                }
            }
        }
        rr = row;
        if (--rr >= 0) {
            markSensorRow(rr, Math.max(0, col - 3), Math.min(col + 4, cols), -1);
            if (--rr >= 0) {
                markSensorRow(rr, Math.max(0, col - 2), Math.min(col + 3, cols), -1);
                if (--rr >= 0) {
                    markSensorRow(rr, Math.max(0, col - 1), Math.min(col + 2, cols), -1);
                }
            }
        }
    }
    
    void markSensorRow(int row, int begCol, int endCol, int mark)
    {
        for (int col = begCol; col < endCol; col++) {
            grid[row][col] = mark;
        }
    }
    
    /** Translate distance back into readable characters */
    public static void printOnePoint2d(Point2d pt) {
        Sx.format("(%5.2f, %5.2f) ", pt.x, pt.y);
    }    
    
    public static int unit_test()
    {
        String testName = AStarPath.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        Point2d r0 = new Point2d( 1.0, 0.0);
        Point2d r1 = new Point2d(28.0, 14.0);
        double sensorRadius = Math.E;
        Point2d[] sensorPoints = {
                new Point2d(26.0, 11.0),
                new Point2d(25.0,  1.0),
                new Point2d(19.0, 11.0),
                new Point2d(17.0,  4.5),
                new Point2d(11.5,  8.5),
                new Point2d( 7.8,  3.5),
                new Point2d( 5.5, 12.5),
        };
        
        AStarPath acir = new AStarPath(r0, r1, sensorPoints, sensorRadius);
        Sx.format("\nMinimal distance grid path from left to right side of grid, length in cells %d:\n"
                , acir.minGridPathLength);
        Sx.putsList(acir.minimalGridPath, 5);
        Sx.format("\nMinimal distance coordinate path from left to right side, approx. geometric length %f:\n"
                , acir.minGeomPathLength);
        Sx.printArrayFolded(acir.minimalCoordPath, 5, AStarPath::printOnePoint2d);
        Sx.puts();
        
        int rows = acir.rows;
        int cols = acir.cols;
        int distWithPath[][] = new int[rows][cols];  // All 0s
        GridPath.addPathToArray(acir.minimalGridPath, distWithPath, rows, cols);        
        GridPath.addNegativeCellsToGrid(acir.grid, distWithPath, rows, cols);
        Sx.putsArray("Array with path and obstacles:\n", distWithPath, GridPath::printOneDistanceCell);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
    //// OTHER CLASSES ////
    
    /** compare x coordinates, then y coordinates if same x. */
    class ComparePointXY implements Comparator<Point2d> {
        @Override
        public int compare(Point2d pA, Point2d pB) {
            int xcomp = Double.compare(pA.x, pB.x);
            if (xcomp != 0)
                return xcomp;
            return Double.compare(pA.y, pB.y);
        }
        
    }
    
    //// TEST DATA ////
    
    
}
