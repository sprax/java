package sprax.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import sprax.Sx;
import sprax.graphs.InferDictionaryOrder.ArrayListBigFirstComparator;

public class GraphBfsPath<T extends Vertex>
{
    Graph<T>  mGraph;
    T         mSource;
    Set<T>    mMarked;
    Map<T, T> mInVert;
    
    public GraphBfsPath(Graph<T> graph, T source)
    {
        mGraph = graph;
        mSource = source;
        mMarked = new HashSet<T>();
        mInVert = new HashMap<T, T>();
        bfs();
    }
    
    /**
     * recursive depth-first search from a single vertex
     */
    protected void bfs()
    {
        LinkedList<T> queue = new LinkedList<T>();
        mMarked.add(mSource);
        queue.add(mSource);
        do {
            T vert = queue.remove();
            for (T neighbor : mGraph.getEdges(vert)) {
                Boolean marked = mMarked.contains(neighbor);
                if (!marked) {
                    mMarked.add(neighbor);
                    mInVert.put(vert, neighbor);
                    queue.add(neighbor);
                }
            }
        } while (!queue.isEmpty());
    }
    
    public boolean isConnected(T vert)
    { // If this vertex was marked, it is connected to mSource
        return mMarked.contains(vert);
    }
    
    public int numConnected()
    {
        return mMarked.size();
    }
    
    /**
     * Get shortest path length (number of edges) from source to this vert.
     * 
     * @param vert
     * @return
     */
    public Iterable<T> getPath(T vert)
    {
        if (isConnected(vert)) {
            Stack<T> path = new Stack<T>();
            for (T step = vert; step != mSource; step = mInVert.get(step))
                path.push(step);
            path.push(mSource);
            return path;
        }
        return null;
    }
    
    static int unit_test(int level)
    {
        Sx.puts("test_BVGraph:");
        
        ArrayList<Vertex> vertArray = new ArrayList<Vertex>();
        Vertex vexA = new Vertex(0);
        vertArray.add(vexA);
        Vertex vexB = new Vertex(2);
        vertArray.add(vexB);
        Vertex vexC = new Vertex(4);
        vertArray.add(vexC);
        Vertex vexD = new Vertex(6);
        vertArray.add(vexD);
        Vertex vexE = new Vertex(8);
        vertArray.add(vexE);
        Vertex vexF = new Vertex(1);
        vertArray.add(vexF);
        Vertex vexG = new Vertex(3);
        vertArray.add(vexG);
        Vertex vexH = new Vertex(5);
        vertArray.add(vexH);
        Graph<Vertex> bvg = new Graph<Vertex>(vertArray);
        bvg.addEdge(vexA, vexB);
        bvg.addEdge(vexB, vexC);
        bvg.addEdge(vexC, vexD);
        bvg.addEdge(vexD, vexE);
        bvg.addVert(vexE);
        bvg.findAndShowVertPath(vexA, vexB);
        bvg.findAndShowVertPath(vexB, vexA);
        bvg.findAndShowVertPath(vexA, vexC);
        bvg.findAndShowVertPath(vexB, vexD);
        bvg.findAndShowVertPath(vexB, vexF);
        bvg.findAndShowVertPath(vexD, vexA);
        
        Sx.puts("BFS search in the presence of cycles?");
        boolean bMarking = true;
        bvg.addEdge(vexC, vexA);
        bvg.findAndShowVertPath(vexA, vexE, bMarking);
        bvg.findAndShowVertPath(vexB, vexF, bMarking);
        
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test(1);
    }
}
