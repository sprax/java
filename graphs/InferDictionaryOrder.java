package sprax.graphs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import sprax.arrays.Arrays1d;
import sprax.sprout.Sx;

public class InferDictionaryOrder
{
    /**
     * Imperfect: only finds one merged chain, starting with the chain from the words' first
     * letters, not from the whole graph. Also does not correctly handle prepending, i.e.
     * singleOrder[LAST] == mergedOrder[FIRST]
     */
    protected static ArrayList<Character> orderFromTwoWayMergesBUST(final String[] words,
            final int numLetters, final int maxWordLength)
    {
        if (words == null || numLetters < 1)
            return null;
        
        ArrayList<Character> mergedOrder = null; // the answer to return
        
        // separate pass through dictionary for each word length
        for (int m = 0; m < maxWordLength; m++)
        {
            ArrayList<Character> singleOrder = new ArrayList<Character>();
            Character newLetter, oldLetter = null;
            String oldWord = "";
            for (String word : words)
            {
                if (word.length() > m)
                {
                    newLetter = word.charAt(m);
                    if (newLetter != oldLetter && oldWord != null)
                    {
                        singleOrder.add(newLetter);
                    }
                    oldWord = word;
                    oldLetter = newLetter;
                }
                else
                {
                    oldWord = null;
                    oldLetter = null;
                }
            }
            if (singleOrder.size() == numLetters)
            {
                return singleOrder;
            }
            else if (m == 0)
            {
                mergedOrder = new ArrayList<Character>(singleOrder);
            }
            else
            {
                // Merge singleOrder into mergedOrder and return it if complete.
                // This algorithm is very limited: the mergedOrder and singleOrder
                // must intersect at 2 points, or else we cannot know where to insert
                // the new letter(s) in the single chain. Instead, we'll be left with
                // a DAC good only for topological sorting.
                oldLetter = null;
                for (int j = 0; j < singleOrder.size(); j++, oldLetter = newLetter)
                {
                    newLetter = singleOrder.get(j);
                    if (!mergedOrder.contains(newLetter))
                    {
                        int mergedIdx = mergedOrder.indexOf(oldLetter) + 1;
                        if (mergedIdx == mergedOrder.size() - 1)
                        {
                            // Add all the rest of singleOrder to the end of mergedOrder
                            mergedOrder.addAll(singleOrder.subList(j, singleOrder.size()));
                        }
                        else if (mergedIdx >= 0)
                        {
                            int singleIdx = j;
                            while (++j < singleOrder.size())
                            {
                                if (mergedOrder.contains(singleOrder.get(j)))
                                {
                                    while (singleIdx < j)
                                    {
                                        mergedOrder.add(mergedIdx++, singleOrder.get(singleIdx++));
                                    }
                                    
                                }
                            }
                        }
                    }
                    if (mergedOrder.size() == numLetters || maxWordLength <= m + 1)
                    {
                        return mergedOrder;
                    }
                }
            }
        }
        
        return mergedOrder;
    }
    
    /**
     * Graph node: Character key and its set of antecedents
     * 
     * @author Sprax Lines
     */
    protected class CharAndPriors
    {
        char               symbol;
        HashSet<Character> priors;
        
        CharAndPriors(char sym)
        {
            symbol = sym;
            priors = new HashSet<Character>();
        }
    }
    
    /**
     * Graph node: Character key and its set of descendants.
     * 
     * @author Sprax Lines
     */
    protected class CharAndPosts
    {
        char               symbol;
        HashSet<Character> descendants;
        
        CharAndPosts(char sym)
        {
            symbol = sym;
            descendants = new HashSet<Character>();
        }
    }
    
    /**
     * Constructs priors graph using a boolean to control the inner loop.
     * 
     * @param words
     * @param arrayListComparator
     * @param verbose
     * @return
     */
    public static ArrayList<ArrayList<Character>> orderFromPriorsGraphBoolean(
            final String[] words,
            Comparator<ArrayList<?>> arrayListComparator,
            int verbose)
    {
        Map<Character, HashSet<Character>> adjMap = new HashMap<Character, HashSet<Character>>();
        
        // Get all precedences; hashing prevents duplicates
        String oldWord = "";
        int oldWordLen = 0;
        for (final String newWord : words)
        {
            boolean foundFirstDiff = false;
            for (int j = 0; j < newWord.length(); j++)
            {
                char newChar = newWord.charAt(j);
                HashSet<Character> priors = adjMap.get(newChar);
                if (priors == null)
                {
                    priors = new HashSet<Character>();
                    adjMap.put(newChar, priors);
                }
                if (foundFirstDiff == false && oldWordLen > j)
                {
                    char oldChar = oldWord.charAt(j);
                    if (oldChar != newChar)
                    {
                        priors.add(oldChar);
                        foundFirstDiff = true;
                    }
                }
            }
            oldWord = newWord;
            oldWordLen = newWord.length();
        }
        if (verbose > 1)
            printGraph(adjMap);
        
        ArrayList<ArrayList<Character>> sortedChains = sortPriorsGraphTopological(adjMap);
        
        // Sort the list of chains by size, longest first
        sortedChains.sort(arrayListComparator);
        return sortedChains;
    }
    
    public static ArrayList<ArrayList<Character>> orderFromPriorsGraphClever(
            final String[] words,
            Comparator<ArrayList<?>> arrayListComparator,
            int verbose)
    {
        HashMap<Character, HashSet<Character>> incGraph = createPriorsGraph(words);
        if (verbose > 1)
            printGraph(incGraph);
        
        ArrayList<ArrayList<Character>> sortedChains = sortPriorsGraphTopological(incGraph);
        
        // Sort the list of chains by size, longest first
        sortedChains.sort(arrayListComparator);
        return sortedChains;
    }
    
    /**
     * The "priors" graph is just a directed graph (it should be acyclic) wherein an edge from Y to
     * X means that X precedes Y in lexicographic order. If you think of that relationship as X ->
     * Y, then you may conceive of the adjacency set of an item and a bunch of arrows pointing into
     * Y, so it's like a set of incident edges, but really, a DAG is more abstract than that, so you
     * can picture it as arrows into or out of vertices, just be reversing the direction of the
     * relationship. Defining the edge (Y, X) as "X sorts before Y", or
     * "X precedes Y in the ordering at hand" makes the result of a simple topological come out
     * already in the final order, without any need of a stack to reverse it.
     * 
     * @param words
     * @return
     */
    public static HashMap<Character, HashSet<Character>> createPriorsGraph(final String[] words)
    {
        HashMap<Character, HashSet<Character>> graph = new HashMap<Character, HashSet<Character>>();
        
        // Get all precedences; hashing prevents duplicates
        String oldWord = "";
        int oldWordLen = 0;
        for (final String newWord : words)
        {
            int newWordLen = newWord.length();
            int j = 0, commonLen = Math.min(oldWordLen, newWordLen);
            for (; j < commonLen; j++)
            {
                char newChar = newWord.charAt(j);
                char oldChar = oldWord.charAt(j);
                if (newChar != oldChar)
                {
                    HashSet<Character> priors = graph.get(newChar);
                    if (priors == null)
                    {
                        priors = new HashSet<Character>();
                        graph.put(newChar, priors);
                    }
                    priors.add(oldChar);
                    j++;
                    break;
                }
            }
            for (; j < newWordLen; j++)
            {
                char newChar = newWord.charAt(j);
                HashSet<Character> priors = graph.get(newChar);
                if (priors == null)
                {
                    priors = new HashSet<Character>();
                    graph.put(newChar, priors);
                }
            }
            
            oldWord = newWord;
            oldWordLen = newWord.length();
        }
        return graph;
    }
    
    protected static void printGraph(Map<Character, HashSet<Character>> adjMap)
    {
        for (char key : adjMap.keySet())
        {
            Set<Character> priors = adjMap.get(key);
            Sx.putsArray("priors for key " + key + ": ", priors.toArray());
        }
    }
    
    protected static ArrayList<ArrayList<Character>> sortPriorsGraphTopological(
            Map<Character, HashSet<Character>> adjMap)
    {
        // each connected subgraph begins with a char that has no priors
        ArrayList<ArrayList<Character>> sortedChains = new ArrayList<ArrayList<Character>>();
        for (Iterator<HashMap.Entry<Character, HashSet<Character>>> it = adjMap.entrySet()
                .iterator(); it.hasNext();)
        {
            HashMap.Entry<Character, HashSet<Character>> entry = it.next();
            if (entry.getValue().isEmpty())
            {
                ArrayList<Character> chain = new ArrayList<Character>();
                sortedChains.add(chain);
                char head = entry.getKey();
                chain.add(head);
                it.remove();
                addToChainRecurse(adjMap, chain, head);
            }
        }
        return sortedChains;
    }
    
    public static ArrayList<ArrayList<Character>> orderFromDescendenceGraphClever(
            final String[] words,
            Comparator<ArrayList<?>> arrayListComparator,
            int verbose)
    {
        DescentGraph<Character> outGraph = createDescentGraph(words);
        if (verbose > 1)
            printGraph(outGraph.mAdjacencyMap);
        
        ArrayList<ArrayList<Character>> sortedChains = sortDescendantsGraphTopological(outGraph.mAdjacencyMap);
        
        // Sort the list of chains by size, longest first
        sortedChains.sort(arrayListComparator);
        return sortedChains;
    }
    
    static class DescentGraph<T>
    {
        Map<T, HashSet<T>> mAdjacencyMap;
        Map<T, Integer>    mInDegreeMap;
        
        DescentGraph(Map<T, HashSet<T>> adjMap, Map<T, Integer> inDegMap)
        {
            mAdjacencyMap = adjMap;
            mInDegreeMap = inDegMap;
        }
    }
    
    public static DescentGraph<Character> createDescentGraph(final String[] words)
    {
        if (words == null || words.length < 1)
            return null;
        
        // return value:
        HashMap<Character, HashSet<Character>> adjMap = new HashMap<Character, HashSet<Character>>();
        HashMap<Character, Integer> inDegMap = new HashMap<Character, Integer>();
        DescentGraph<Character> descentGraph = new DescentGraph<Character>(adjMap, inDegMap);
        
        // Get all precedences; hashing prevents duplicates
        String oldWord = words[0];
        int j, oldWordLen = oldWord.length();
        for (int k = 1; k < words.length; k++)
        {
            final String newWord = words[k];
            int newWordLen = newWord.length();
            int commonLen = Math.min(oldWordLen, newWordLen);
            for (j = 0; j < commonLen; j++)
            {
                char oldChar = oldWord.charAt(j);
                char newChar = newWord.charAt(j);
                if (newChar != oldChar)
                {
                    HashSet<Character> children = adjMap.get(oldChar);
                    if (children == null)
                    {
                        children = new HashSet<Character>();
                        adjMap.put(oldChar, children);
                    }
                    
                    if (children.add(newChar))
                    {
                        Integer newCharInDeg = inDegMap.get(newChar);
                        if (newCharInDeg == null)
                        {
                            inDegMap.put(newChar, 1);
                        }
                        else
                        {
                            inDegMap.put(newChar, 1 + newCharInDeg);
                        }
                    }
                    j++;
                    break;
                }
            }
            for (; j < newWordLen; j++)
            {
                char newChar = newWord.charAt(j);
                HashSet<Character> newPosts = adjMap.get(newChar);
                if (newPosts == null)
                {
                    newPosts = new HashSet<Character>();
                    adjMap.put(newChar, newPosts);
                }
            }
            
            oldWord = newWord;
            oldWordLen = newWord.length();
        }
        return descentGraph;
    }
    
    protected static ArrayList<ArrayList<Character>> sortDescendantsGraphTopological(
            Map<Character, HashSet<Character>> mAdjacencyMap)
    {
        // each connected subgraph begins with a char that has no priors
        ArrayList<ArrayList<Character>> sortedChains = new ArrayList<ArrayList<Character>>();
        for (Iterator<HashMap.Entry<Character, HashSet<Character>>> it = mAdjacencyMap.entrySet()
                .iterator(); it.hasNext();)
        {
            HashMap.Entry<Character, HashSet<Character>> entry = it.next();
            if (entry.getValue().isEmpty())
            {
                ArrayList<Character> chain = new ArrayList<Character>();
                sortedChains.add(chain);
                char head = entry.getKey();
                chain.add(head);
                it.remove();
                addToChainRecurse(mAdjacencyMap, chain, head);
            }
        }
        
        for (ArrayList<Character> list : sortedChains)
        {
            Arrays1d.reverseListInPlace(list);
        }
        return sortedChains;
    }
    
    static private void addToChainRecurse(Map<Character, HashSet<Character>> adjMap,
            ArrayList<Character> chain, Character link)
    {
        for (char key : adjMap.keySet())
        {
            HashSet<Character> adj = adjMap.get(key);
            if (adj.contains(link))
            {
                adj.remove(link);
                if (adj.isEmpty())
                {
                    chain.add(key);
                    addToChainRecurse(adjMap, chain, key);
                }
            }
        }
    }
    
    /**
     * Enables sorting ArrayLists by size.
     * 
     * @author sprax
     */
    static class ArrayListBigFirstComparator implements Comparator<ArrayList<?>>
    {
        @Override
        public int compare(ArrayList<?> arg0, ArrayList<?> arg1)
        {
            return arg1.size() - arg0.size();
        }
    }
    
    protected static void putsChains(ArrayList<ArrayList<Character>> sortedChains)
    {
        int j = 0;
        for (ArrayList<Character> chain : sortedChains)
        {
            if (chain.size() > 1)
            {
                Sx.putsArray("	" + j++ + "	", chain);
            }
        }
    }
    
    public static ArrayList<ArrayList<Character>> orderUsingDescentGraphDfs(
            final String[] words,
            Comparator<ArrayList<?>> arrayListComparator,
            int verbose)
    {
        DescentGraph<Character> descentGraph = createDescentGraph(words);
        if (verbose > 1)
            printGraph(descentGraph.mAdjacencyMap);
        
        ArrayList<ArrayList<Character>> sortedChains = dfsTopSortPriorsGraph(descentGraph);
        
        // Sort the list of chains by size, longest first
        sortedChains.sort(arrayListComparator);
        return sortedChains;
    }
    
    public enum VisitedState
    {
        UNDISCOVERED, DISCOVERED, PROCESSED
    }
    
    public static ArrayList<ArrayList<Character>> dfsTopSortPriorsGraph(
            DescentGraph<Character> graph)
    {
        Map<Character, HashSet<Character>> adjMap = graph.mAdjacencyMap;
        Map<Character, Integer> inDegMap = graph.mInDegreeMap;
        ArrayList<ArrayList<Character>> sortedChains = new ArrayList<ArrayList<Character>>();
        HashMap<Character, VisitedState> visits = new HashMap<Character, VisitedState>(
                adjMap.size());
        for (Character ch : adjMap.keySet())
            visits.put(ch, VisitedState.UNDISCOVERED);
        for (Character ch : inDegMap.keySet())
            visits.put(ch, VisitedState.UNDISCOVERED);
        
        // each connected subgraph begins with a char that has no priors
        for (Character key : adjMap.keySet())
        {
            if (visits.get(key) == VisitedState.UNDISCOVERED)
            {
                Integer inDegree = inDegMap.get(key);
                if (inDegree == null || inDegree == 0)
                {
                    ArrayList<Character> chain = new ArrayList<Character>();
                    sortedChains.add(chain);
                    dfsTopSortRec(adjMap, chain, key, visits);
                }
            }
        }
        for (ArrayList<Character> list : sortedChains)
        {
            Arrays1d.reverseListInPlace(list);
        }
        return sortedChains;
    }
    
    /**
     * Depth First Search ensures that each node is added to the chain only after all of its
     * descendants have been added. That way, the last (farthest away) is added first, before any
     * intermediaries. For example, the graph A -> B, A -> C, B -> C yields the add sequence C, B,
     * A. If pushed onto a stack S, then S.ToArray will yield the correct final order. If appended
     * to a list, the list must be reversed to give the right order. Not that adding B -> D leaves
     * the end of the order undetermined: the order could be either ABCD or ABDC. Adding the edge C
     * -> D would disambiguate the order to ABCD
     */
    private static boolean dfsTopSortRec(
            Map<Character, HashSet<Character>> graph,
            ArrayList<Character> chain,
            Character node,
            HashMap<Character, VisitedState> visits)
    {
        boolean returnNow = false;
        visits.put(node, VisitedState.DISCOVERED);
        HashSet<Character> children = graph.get(node);
        if (children != null)
        {
            for (Character child : children)
            {
                VisitedState childState = visits.get(child);
                if (childState == VisitedState.UNDISCOVERED)
                {
                    returnNow = processEdge(node, child);
                    if (returnNow)
                        return returnNow;
                    returnNow = dfsTopSortRec(graph, chain, child, visits);
                    if (returnNow)
                        return returnNow;
                }
                else if (childState == VisitedState.DISCOVERED)
                {
                    returnNow = processEdge(node, child);
                    if (returnNow)
                        return returnNow;
                }
            }
        }
        // Add this node to the chain only after all of its descendants have been added.
        chain.add(node);
        visits.put(node, VisitedState.PROCESSED);
        return returnNow;
    }
    
    private static boolean processEdge(Character node, Character prior)
    {
        return false;
    }
    
    public static void unit_test(int verbose)
    {
        Sx.puts(InferDictionaryOrder.class.getName() + ".unit_test");
        
        String[] wordsA = {
                "d",
                "dd",
                "decp",
                "decq",
                "deg",
                "dgi",
                "fgo",
                "fgpt",
                "fh",
                "ahp",
                "ai",
                "bil",
                "bim",
                "bimi",
                "bimiz",
                "bimir",
                "bimr",
                "bims",
                "bimt",
                "bin",
                "bio",
                "bx",
                "ccas",
                "ccau",
                "x",
                "ybe",
                "ybf",
                "ybf",
                "yc",
                "za",
                "zc" };
        ArrayList<Character> foundOrder = orderFromTwoWayMergesBUST(wordsA, 26, 7);
        Sx.putsArray("Input: ", wordsA);
        Sx.putsArray("Output:", foundOrder);
        
        ArrayListBigFirstComparator comp = new ArrayListBigFirstComparator();
        
        ArrayList<Character> alcA = new ArrayList<Character>();
        alcA.add('x');
        alcA.add('y');
        ArrayList<Character> alcB = new ArrayList<Character>(alcA);
        alcB.add('a');
        alcB.add('b');
        Sx.format("Sizes alcA & alcB: %d %d\n", alcA.size(), alcB.size());
        
        Sx.puts("comp.compare(alcA, alcB) give: " + comp.compare(alcA, alcB));
        
        ArrayList<ArrayList<Character>> sortedChains;
        
        Sx.puts("Testing orderFromPriorsGraphClever:");
        sortedChains = orderFromPriorsGraphClever(wordsA, comp, verbose);
        putsChains(sortedChains);
        
        Sx.puts("Testing orderFromPriorsGraphBoolean:");
        sortedChains = orderFromPriorsGraphBoolean(wordsA, comp, verbose);
        putsChains(sortedChains);
        
        Sx.puts("Testing orderFromDescendenceGraphClever:");
        sortedChains = orderFromDescendenceGraphClever(wordsA, comp, verbose);
        putsChains(sortedChains);
        
        Sx.puts("Testing orderFromPriorsGraphDfs:");
        sortedChains = orderUsingDescentGraphDfs(wordsA, comp, verbose);
        putsChains(sortedChains);
        
        Stack<Character> stack = new Stack<Character>();
        stack.push('A');
        stack.push('B');
        stack.push('C');
        Object[] array = stack.toArray();
        Sx.putsArray(array);
        array[0] = stack.pop();
        array[1] = stack.pop();
        array[2] = stack.pop();
        Sx.putsArray(array);
    }
    
    public static void main(String[] args)
    {
        unit_test(1);
    }
    
}
