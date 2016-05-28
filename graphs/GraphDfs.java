package sprax.graphs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class GraphDfs<T extends Vertex>
{
    Graph<T> mGraph;
    T        mSource;
    Set<T>   mMarked;
    
    public GraphDfs(Graph<T> graph, T source)
    {
        mGraph = graph;
        mSource = source;
        mMarked = new HashSet<T>();
        dfs(source);
    }
    
    /**
     * recursive depth-first search from a single vertex
     */
    void dfs(T vert)
    {
        mMarked.add(vert);
        for (T neighbor : mGraph.getEdges(vert)) {
            boolean marked = mMarked.contains(neighbor);
            if (!marked)
                dfs(neighbor);
        }
    }
    
    /** Is the specified vertex connected to mSource? */
    public boolean isConnected(T vert)
    { // If this vertex was marked, it is connected to mSource
        return mMarked.contains(vert);
    }
    
    public int numConnected()
    {
        return mMarked.size();
    }
}

class DepthFirstSearchPath<T extends Vertex>
{
    Graph<T>  mGraph;
    T         mSource;
    Map<T, T> mPreVert;
    Set<T>    mMarked;
    
    public DepthFirstSearchPath(Graph<T> graph, T source)
    {
        mGraph = graph;
        mSource = source;
        mPreVert = new HashMap<T, T>();
        mMarked = new HashSet<T>();
        dfs(source);
    }
    
    /**
     * recursive depth-first search from a single vertex
     */
    void dfs(T vert)
    {
        mMarked.add(vert);
        for (T neighbor : mGraph.getEdges(vert)) {
            boolean marked = mMarked.contains(neighbor);
            if (!marked) {
                mPreVert.put(vert, neighbor);
                dfs(neighbor);
            }
        }
    }
    
    public Iterable<T> getPath(T vert)
    {
        if (isConnected(vert)) {
            Stack<T> path = new Stack<T>();
            for (T step = vert; step != mSource; step = mPreVert.get(step))
                path.push(step);
            path.push(mSource);
            return path;
        }
        return null;
    }
    
    public boolean isConnected(T vert)
    { // If this vertex was marked, it is connected to mSource
        return mMarked.contains(vert);
    }
    
    public int connectedComponentSize()
    {
        return mMarked.size();
    }
}
