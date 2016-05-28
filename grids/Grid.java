package sprax.grids;

import sprax.Sx;

/*
 * TODO: For maximal path problems, define both edge weights and node
 * weights, which gives 4 combinations:
 * maximal node-weighted path, maximal edge-weighted path,
 * max node plus edge weights, max node minus edge weights.
 * In a DAG, number of neighbors = inEdges + outEdges = 
 * num predecessor nodes + num successor nodes (topological ordering).  
 * 
 * For a down-and-right-directed rectangular grid, there are 
 * (numRows - 1)*(numCols) down edges |,
 * (numRows)*(numCols - 1) right edges --, and
 * (numRows - 1)*(numCols - 1) diagonal edges \ (going down and right), and
 * (numRows - 1)*(numCols - 1) diagonal edges / (going down and left).
 */

/**
 * A node that has data and 1 or more neighbors in a grid,
 * but does not distinguish its neighbors from each other spatially.
 * The ordering of its lists of neighbors is not expected to be used
 * in algorithms such as searching for a shortest path. The ordering
 * of the nodes lists of neighbors is consistent, but is not given
 * any special meaning, as in any neighbors to the left always precede
 * right any neighbors to the right, etc.
 * 
 * @see DiGridNode<T>: a directed node grid, which
 *      does distinguish left from right, up from down, etc.
 */
abstract class GridNode<T>
{
    T                       mData;
    public int              mNumNeighbors = 0;
    protected GridNode<T>[] mNeighbors;
    
    GridNode() {}                 // default constructor
    
    GridNode(T t) {
        mData = t;
    }      // generic constructor
    
    @Override
    public String toString() {       // generic toString: data only
        return mData.toString();
    }
    
    public T getData() {
        return mData;
    }
    
    public void setData(T t) {
        mData = t;
    }
    
    abstract public int getIntVal();
    
    abstract public void setIntVal(int val);
    
    public GridNode<T>[] getNeighbors() {
        return mNeighbors;
    }
    
    public void setNeighbors(GridNode<T>[] neighbors) {
        mNeighbors = neighbors;
    }
    
    public GridNode<T> getNeighbor(int idx) {
        return mNeighbors[idx];
    }
    
    public void setNeighbor(int idx, GridNode<T> gridNode) {
        mNeighbors[idx] = gridNode;
    }
    
}

abstract class GridNode2<T> extends GridNode<T>
{
    public static final int sMaxNumNeighbors = 2;
}

abstract class GridNode4<T> extends GridNode<T>
{
    public static final int sMaxNumNeighbors = 4;
}

abstract class GridNode8<T> extends GridNode2<T>
{
    public static final int sMaxNumNeighbors = 8;
}

/**
 * GridNodeChar: minimal concrete class extending GridNode<Character>
 */
class GridNodeChar extends GridNode<Character>
{
    @Override
    public int getIntVal() {
        return mData;
    }
    
    @Override
    public void setIntVal(int val) {
        mData = (char) val;
    }
}

/**
 * GridNodeInt: minimal concrete class extending GridNode<Integer>
 */
class GridNodeInt extends GridNode<Integer>
{
    @Override
    public int getIntVal() {
        return mData;           // promotes char to integer value
    }
    
    @Override
    public void setIntVal(int val) {
        mData = val;
    }
}

/**
 * DiGridNode<T> A node that has data and 1 or more neighbors in a grid,
 * and distinguishes its neighbors from each other spatially.
 * 
 * @see GridNode<T>, which does not distinguish left and right
 *      neighbors, up from down, etc.)
 */
abstract class DiGridNode<T> extends GridNode<T>
{
}

abstract class DiGridNode2<T> extends GridNode2<T>
{
    public static final int sMaxNumNeighbors = 2;
    GridNode<T>             mNeighbors[];
    
    public GridNode<T> lf() {
        return mNeighbors[0];
    } // left neighbor or null
    
    public GridNode<T> rt() {
        return mNeighbors[1];
    } // right neighbor or null
}

/**
 * DiGridNode4 grid node with max 4 neighbors: lf, rt, up, dn for left, right, up, down.
 * 
 * @author sprax
 *
 */
abstract class DiGridNode4<T> extends DiGridNode2<T>
{
    public static final int sMaxNumNeighbors = 4;
    
    public GridNode<T> up() {
        return mNeighbors[2];
    } // top neighbor or null
    
    public GridNode<T> dn() {
        return mNeighbors[3];
    } // bottom neighbor or null
}

abstract class DiGridNode8<T> extends DiGridNode4<T>
{
    public static final int sMaxNumNeighbors = 8;
    
    public GridNode<T> dl() {
        return mNeighbors[4];
    } // bottom left neighbor or null
    
    public GridNode<T> dr() {
        return mNeighbors[5];
    } // bottom right neighbor or null
    
    public GridNode<T> ul() {
        return mNeighbors[6];
    } // upper left neighbor or null
    
    public GridNode<T> ur() {
        return mNeighbors[7];
    } // upper right or null
    
    GridNode<T> nit(int k) {
        return mNeighbors[k];
    }
}

// ////////// RECTGRID<T> //////////////////////////////////////////////////
/**
 * Grid<T> Grid of packed, connected nodes, generally 2-dimensional,
 * meaning that any node's neighbors can be visualized as lying next to
 * it in a regular, packed pattern a plane. Regular means spatially
 * uniform. Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.
 */
public abstract class Grid<T, NodeT extends GridNode<T>>
{
    public static final int sMaxNumNeighbors = 0;
    
    // NodeT[][] mNodes; // Declare as much as possible in terms of the generic Node, not
    // GridNode<?>
    
    Grid(int numRows, int numCols)  // template constructor
    {
        setDimensions(numRows, numCols);
        createNodes();
        setNeighbors();
    }
    
    abstract NodeT createNode(int row, int col); // return type must be the generic node
    
    abstract void setDimensions(int numRows, int numCols);
    
    abstract void createNodes();    // calls createNode(row, col)
    
    abstract void setNeighbors();   // sets N <= sMaxNumNeighbors neighbors on each node
    
    public static int unit_test()
    {
        Sx.puts(Grid.class.getName() + ".unit_test");
        GridNodeInt gni = new GridNodeInt();
        GridNodeChar gnc = new GridNodeChar();
        gni.setIntVal(42);
        GridNodeInt neighbors[] = new GridNodeInt[8];
        gni.setNeighbors(neighbors);
        gni.setNeighbor(0, gni);
        gni.setNeighbor(1, gni);
        gni.setNeighbor(2, gni);
        gni.setNeighbor(3, gni);
        gnc.setNeighbors(new GridNodeChar[8]);
        gnc.setNeighbor(4, gnc);
        
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
