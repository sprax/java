package sprax.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import sprax.sprout.Sx;

/**
 * Breadth-first search from a single vertex. Given a graph G and a source vertex v, visit all
 * vertices reachable from v in BFS order and map the resulting set of marked vertices to their
 * distance from the source vertex.
 *
 * @author sprax
 *
 * @param <T>
 */
public class GraphBfs<T extends Vertex>
{
    Graph<T> mGraph;
    T mSource;
    Map<T, Integer> mVertToDistance;
    Map<T, T> mVertToPrev;

    public GraphBfs(Graph<T> graph, T source)
    {
        if (graph == null || source == null) {
            throw new IllegalArgumentException("null graph or source");
        }

        mGraph          = graph;
        mSource         = source;
        mVertToDistance = new HashMap<T, Integer>();
        mVertToPrev     = new HashMap<T, T>();
        bfs();
    }

    /**
     * recursive breadth-first search from a single vertex, mapping distance.
     */
    protected void bfs()
    {
        int           dist  = 0;
        LinkedList<T> queue = new LinkedList<T>();
        queue.add(mSource);
        queue.add(null); // null is used as the depth-change marker
        do
        {
            T vert = queue.remove();
            if (vert == null) {
                dist++;
                queue.add(null);
            } else {
                mVertToDistance.put(vert, dist);
                for (T neighbor : mGraph.getEdges(vert)) {
                    if (!mVertToDistance.containsKey(neighbor)) {
                        mVertToPrev.put(neighbor, vert);
                        queue.add(neighbor);
                    }
                }
            }
        } while (queue.size() > 1);
    }

    public boolean isConnected(T vert)
    { // If this vertex was marked, it is connected to mSource
        return mVertToDistance.containsKey(vert);
    }

    /**
     * Return the distance from the source to this vertex,
     * if they are connected, or -1 if they are not connected.
     */
    public int distance(T vert)
    {
        Integer dist = mVertToDistance.get(vert);
        if (dist != null) {
            return dist;
        }
        return -1;
    }

    /**
     * Number of vertices connected to the source vertex, including the vertex itself.
     */
    public int numConnected()
    {
        return mVertToDistance.size();
    }

    Iterable<T> pathFromSource(T dest)
    {
        return null; // TODO
    }

    static int test_GraphBfs()
    {
        Sx.puts("test_GraphBfs:");

        ArrayList<Vertex> vertArray = new ArrayList<Vertex>();
        Vertex            vexA      = new Vertex(0);
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
        bvg.findAndShowVertPath(vexA, vexE);

        Sx.puts("BFS search in the presence of cycles?");
        boolean bMarking = true;
        bvg.addEdge(vexC, vexA);
        bvg.findAndShowVertPath(vexA, vexE, bMarking);
        bvg.findAndShowVertPath(vexB, vexF, bMarking);

        GraphBfs<Vertex> gbfs = new GraphBfs<Vertex>(bvg, vexA);
        Sx.puts(gbfs.distance(vexB));
        Sx.puts(gbfs.distance(vexC));
        Sx.puts(gbfs.distance(vexD));

        return 0;
    }

    public static int unit_test()
    {
        String testName = GraphBfs.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");

        GraphBfs.test_GraphBfs();

        Sx.puts(testName + " END");
        return 0;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
