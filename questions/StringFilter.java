/**
 * StringFilter
 * Sprax Lines 2016
 */
package sprax.questions;

/**
 * Use to filter text file or collection of strings one line at a time.
 * @author Sprax Lines 
 */
@FunctionalInterface
public interface StringFilter
{
    /**
     * Processes one string
     * @param string
     * @return true to continue filtering, false to stop.
     */
    boolean filterString(String string);
} 

