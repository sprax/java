/**
 * PrintOne
 * Sprax Lines 2016
 */
package sprax.sprout;

/**
 * @author Sprax Lines
 * Print one value with spacing, as if embedded in an array.
 * As a functional argument type, it can be targeted by method references 
 * or lambda expressions.
 */
@FunctionalInterface
public interface PrintOne<T>
{
    void printOne(T val);
}

