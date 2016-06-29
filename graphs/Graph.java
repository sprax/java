package sprax.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import sprax.containers.Reversed;
import sprax.sprout.Sx;

class Vertex
{
    public final int mValue;
    
    Vertex(int data)
    {
        mValue = data;
    }
    
    @Override
    public String toString() {
        return String.format("%d", mValue);
    }
    
};

public class Graph<V>
{
    int            mNumEdges;
    Set<V>         mVerts;
    Set<V>         mMarks;
    /** Edges represented as a map from vertices to sets of adjacency vertices */
    Map<V, Set<V>> mEdges;
    
    /**
     * Construct new graph with no edges. Any vertices in verts will be add3ed.
     */
    Graph(ArrayList<V> verts)
    {
        if (verts != null && verts.size() > 0)
            mVerts = new HashSet<V>(verts); // defensive copy
        else
            mVerts = new HashSet<V>();
        mEdges = new HashMap<V, Set<V>>();
        mMarks = new HashSet<V>();
    }
    
    int getNumEdges()
    {
        return mNumEdges;
    }
    
    int getNumVerts()
    {
        return mVerts.size();
    }
    
    /**
     * Returns the graph's set of vertices
     */
    Set<V> getVerts()
    {
        return mVerts;
    }
    
    /**
     * get the set of vertices adjacent to the specified vertex
     */
    Set<V> getEdges(V vert)
    {
        return mEdges.get(vert);
    }
    
    /**
     * get the set of vertices adjacent to the specified vertex
     */
    Set<V> adj(V vert)
    {
        return getEdges(vert);
    }
    
    /** Add a vertex */
    public boolean addVert(V vert)
    {
        return mVerts.add(vert);
    }
    
    /**
     * Add an edge by specifying two vertices. If a vertex is not already in the graph, it too is
     * added.
     * 
     * @param vertA
     * @param vertB
     */
    public void addEdge(V vertA, V vertB)
    {
        addVert(vertA);
        addVert(vertB);
        
        Set<V> edgesA = mEdges.get(vertA);
        if (edgesA == null) {
            edgesA = new HashSet<V>();
            mEdges.put(vertA, edgesA);
        }
        if (edgesA.add(vertB))
            mNumEdges++;
        
        Set<V> edgesB = mEdges.get(vertB);
        if (edgesB == null) {
            edgesB = new HashSet<V>();
            mEdges.put(vertB, edgesB);
        }
        if (edgesB.add(vertA))
            mNumEdges++;
    }
    
    boolean containsEdge(V vertA, V vertB)
    {
        Set<V> edgesA = mEdges.get(vertA);
        if (edgesA != null)
            return edgesA.contains(vertB);
        return false;
    }
    
    boolean containsVert(V vert)
    {
        return mVerts.contains(vert);
    }
    
    boolean findBfsVertexPathMarking(LinkedList<V> path, final V start, final V end)
    {
        path.clear();
        path.add(start);
        return findBfsVertexPathMarkingRecurse(path, start, end);
    }
    
    boolean findBfsVertexPathMarkingRecurse(LinkedList<V> path, final V now, final V end)
    {
        
        V lastVert = path.get(path.size() - 1);
        if (lastVert == end)
            return true;
        
        Set<V> nexts = mEdges.get(lastVert);
        if (nexts != null) {
            
            for (Iterator<V> it = nexts.iterator(); it.hasNext();) {
                V vert = it.next();
                
                if (mMarks.contains(vert) == false) {
                    mMarks.add(vert);
                    path.add(vert);
                    boolean found = findBfsVertexPathMarkingRecurse(path, vert, end);
                    if (found)
                        return found;
                    else
                        path.remove(vert);
                }
                
            }
        }
        
        return false;
    }
    
    /**
     * Breadth-first search for a path from start vertex to end vertex. If found, the first such
     * path is placed in the path argument (a vector of vertices), and the function returns true. If
     * no such path is found, the functions returns false, and the supplied path will be empty.
     *
     * Beware of loops; this method only works on directed acyclic graphs
     */
    boolean findBfsVertexPath(LinkedList<V> path, V start, V end)
    {
        boolean found = false;
        path.clear();
        Queue<V> vertQ = new LinkedList<V>();
        Map<V, V> preVerts = new HashMap<V, V>();
        vertQ.add(start);
        while (!vertQ.isEmpty()) {
            V vert = vertQ.remove();
            Set<V> nexts = mEdges.get(vert);
            if (nexts != null) {
                for (V it : nexts) {
                    preVerts.put(it, vert);
                    if (it == end) {
                        found = true;
                        break;
                    }
                    vertQ.add(it);
                }
                if (found) {
                    path.add(end);
                    for (vert = preVerts.get(end); vert != start; vert = preVerts.get(vert))
                        path.add(vert);
                    path.add(start);
                    return true;
                }
            }
        }
        return false;
    }
    
    void findAndShowVertPath(V vexA, V vexB)
    {
        findAndShowVertPath(vexA, vexB, true);
        return;
    }
    
    boolean findAndShowVertPath(V vertA, V vertB, boolean bMarking)
    {
        Sx.format("DFS Vert Path from %s to %s\n", vertA, vertB);
        LinkedList<V> path = new LinkedList<V>();
        boolean found = false;
        if (bMarking)
            found = findBfsVertexPathMarking(path, vertA, vertB);
        else
            found = findBfsVertexPath(path, vertA, vertB);
        if (found) {
            Sx.puts("YES  ");
            if (!path.isEmpty()) {
                // iterate path in reverse
                for (V vert : Reversed.reversed(path)) {
                    Sx.puts(" : " + vert);
                }
            }
        } else {
            Sx.puts("-none-");
        }
        Sx.puts();
        return found;
    }
    
    static int test_BVGraph()
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
    
    public static int unit_test()
    {
        String testName = Graph.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        // test_BVGraph();
        
        GraphBfs.test_GraphBfs();
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}