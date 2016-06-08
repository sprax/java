package sprax.aligns;


/**
 * Representation of a single word.
 * TODO:    Word-tuples: digram, trigram, tetragram, pentagram, n-gram, 
 *      OR, better: words2, words3, words4, words5.
 *  
 * @author sprax
 *
 */
public class Words // extends Tuple
{
    
    /************************************************************************
     * unit_test
     * 
     */
    public static int unit_test(int verbose)
    {

        ClausesAlignment.unit_test(2);

        return 0;
    }    
    
    public static void main(String[] args) { unit_test(2); }
}
