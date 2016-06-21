/*
 * Median
 * 
 * Copyright (c) 2001, 2002, 2003 Marco Schmidt.
 * All rights reserved.
 */

package sprax.questions;

/** 
 * Can the space requirements specified by items be packed into the specified bins?
 * Finding the optimal solution is combinatorial NP-hard; that is, even deciding if a
 * given solution is minimal is NP-complete. See https://en.wikipedia.org/wiki/Bin_packing_problem
 * But just deciding whether a given set of items can be packed into a given set of bins
 * is NOT NP-complete, but can be solved in Theta(NlogN), as by the First-Fit algorithm.
 */
public interface IBinPack
{
    /** 
     * Can the space requirements specified by items be packed into the specified bins?
     */
    public boolean canPack(int[] bins, int[] items);
}
