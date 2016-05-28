package sprax.grids;

import java.util.Random;

import sprax.Sx;
import sprax.arrays.ArrayMaxPathSum;

/**
 * RectGrid<T> Grid of packed, connected nodes, generally 2-dimensional,
 * meaning that any node's neighbors can be visualized as lying next to
 * it in a regular, packed pattern a plane. Regular means spatially
 * uniform. Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.
 */
public class RectGrid8Int extends RectGrid8<Integer, GridNodeInt>
{
    static int            sDbg = 2;
    
    protected GridNodeInt mNodes[][];
    
    RectGrid8Int(int numRows, int numCols)         // single constructor
    {
        super(numRows, numCols);
    }                   // call base template constructor
    
    @Override
    GridNodeInt createNode(int row, int col) {
        return new GridNodeInt();
    }
    
    @Override
    GridNodeInt[] getNodes(int row) {
        return (GridNodeInt[]) mNodes[row];
    }
    
    @Override
    GridNodeInt[][] getNodes() {
        return (GridNodeInt[][]) mNodes;
    }
    
    @Override
    GridNodeInt getNode(int row, int col)
    {
        return (GridNodeInt) mNodes[row][col];  // TODO: don't like this cast
    }
    
    @Override
    void setNode(GridNodeInt node, int row, int col) {
        mNodes[row][col] = node;
    }
    
    @Override
    void createNodes() {  // FIXME
        // TODO Auto-generated method stub
    }
    
    public static int unit_test()
    {
        int stat = 0;
        Sx.puts("RectGrid8Int unit_test");
        int nRows = 5, nCols = 5, pathSumA, pathSumB, pathSumC, algoSumC;
        RectGrid8Int grid = new RectGrid8Int(nRows, nCols);
        int dataGrid[][] =
        { { 1, -2, 1, 2, -8, -6 }
                , { -1, 9, -3, 2, 1, 0 }
                , { 1, -6, 9, -7, 8, 6 }
                , { -9, 6, -5, 9, -1, -3 }
                , { -7, 2, 5, -4, 9, -5 }
        };
        grid.setData(dataGrid);
        grid.printData("RectGrid8Int");
        
        Random rng = new Random();  // i.e., java.util.Random.
        int begRow = 0;
        int dstRow = nRows - 1;
        int begCol = 0;
        int dstCol = nCols - 1;
        int maxPath[] = null;
        for (int j = 0; j < 8; j++) {
            if (sDbg > 0)
                System.out.format("maxPathDownRight        (%2d %2d)->(%2d %2d):  . . .\n"
                        , begRow, begCol, dstRow, dstCol);
            maxPath = grid.findMaxNodeWeightedPathDownRight(begRow, begCol, dstRow, dstCol);
            pathSumA = 0;
            for (int nodeVal, k = 0; k < maxPath.length; k += 2) {
                nodeVal = grid.mNodes[maxPath[k]][maxPath[k + 1]].getIntVal();
                System.out.format("Found path %2d: (%d %d).%d, \t sum %3d\n"
                        , k, maxPath[k], maxPath[k + 1], nodeVal, pathSumA + nodeVal);
                pathSumA += nodeVal;
            }
            System.out.format("maxPathDownRight        (%2d %2d)->(%2d %2d):  %3d\n"
                    , begRow, begCol, dstRow, dstCol, pathSumA);
            pathSumB = grid.findMaxNodeWeightedPathSumDownRight(begRow, begCol, dstRow, dstCol);
            System.out.format("maxPathSumDownRight     (%2d %2d)->(%2d %2d):  %3d\n"
                    , begRow, begCol, dstRow, dstCol, pathSumB);
            pathSumC = grid.findMaxNodeWeightedPathSumDownRightDiag(begRow, begCol, dstRow, dstCol);
            System.out.format("maxPathSumDownRightDiag (%2d %2d)->(%2d %2d):  %3d\n"
                    , begRow, begCol, dstRow, dstCol, pathSumC);
            algoSumC = ArrayMaxPathSum.findMaxNodeWeightedPathSumRectGridDownRightDiag(
                    grid.toIntArrays(), begRow, begCol, dstRow, dstCol);
            System.out.format("ArrayAlgo.findMaxNode...(%2d %2d)->(%2d %2d):  %3d\n"
                    , begRow, begCol, dstRow, dstCol, algoSumC);
            if (pathSumA != pathSumB) {
                stat -= 1;
                Sx.puts("    ERROR: RectGrid path and pathSum disagree (" + pathSumA + ") vs. ("
                        + pathSumB + ")");
            }
            if (pathSumC != algoSumC) {
                stat -= 1;
                Sx.puts("    ERROR: RectGrid and ArrayAlgo disagree (" + pathSumC + ") vs. ("
                        + algoSumC + ")");
            }
            begRow = rng.nextInt((nRows + 1) / 2);
            dstRow = rng.nextInt(nRows - begRow - 2) + begRow + 2;
            begCol = rng.nextInt((nCols + 1) / 2);
            dstCol = rng.nextInt(nCols - begCol - 2) + begCol + 2;
        }
        return stat;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
