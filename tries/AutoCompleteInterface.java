package sprax.tries;

import java.util.List;

/** 
 * Given some incomplete or in-progress text entry, return some
 * possible or most likely completions.  It may be useful to
 * think of the text as a query.
 *
 * There are three main use cases this API should support:
 * 
 * 1) The user has not yet typed enough.  
 * There may be many possible completions, so which are the most likely?
 *      a) complete words
 *      b) stems that are not complete words, and multiple possible completions as words
 *      c) *neither* -- stems that are not complete, yet only have one possible
 *          completion: don't display these.
 * 2) The user has typed too much.  The beginning may have been good,
 * but they kept going and ran off the end of the known world. 
 * Maybe it's a typo, maybe it's a new and very specific query that has 
 * never before been seen.  Can we distinguish a typo from other novelty?
 * How much (of the beginning) of the query should we use?
 * 3) Too much and too little, both.  The beginning was usable, the
 * middle not, but the end looks like a usable prefix again.
 * 
 * So there are at least three questions:
 * 1) What are the most probable completions of an under-specified query?
 * 2) Can typos be detected and repaired?
 * 3) How much of a non-matching query can be optimally used, by either 
 * repairing or ignoring the parts that do not match anything?
 * 
 * 
 * @author sprax
 *
 */
public interface AutoCompleteInterface 
{
    /** To return a list of all possible completions */
    public List<String> getPossible(String prefix);

    /** To return a list of at most <code>limit</code> possible completions */
    public List<String> getPossible(String prefix, int limit);

    /** To return a list of any probable completions */
    public List<String> getProbable(String prefix);

    /** To return a list of at most <code>limit</code> probable completions */
    public List<String> getProbable(String prefix, int limit);

    /** To return a list of at most <code>limit</code> matchable starts */
    public List<String> getProbablePrefixes(String prefix, int limit);

    /** To return a list of at most <code>limit</code> matchable finishes */
    public List<String> getProbableSuffixes(String prefix, int limit);

    /** To return a list of at most <code>limit</code> matchable finishes */
    public List<String> getProbableRepaired(String prefix, int limit);

}
