package sprax.aligns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import sprax.Spaces;
import sprax.Sx;
import sprax.arrays.ArrayFactory;


class IndexAndScore
{
    // Map.Entry<Integer, Integer> mEntry;
    int   mIndex;
    float mScore;
    IndexAndScore(int idx, float scr) {
        mIndex = idx;
        mScore = scr;
    }
    public String toString() {
        return String.format("{ %2d -> %.2g }", mIndex, mScore);
    }
}
public class GlobalSequenceAlignment<T> extends SequenceAlignment<T>
{
    GlobalSequenceAlignment(ArrayList<T> sA, ArrayList<T> sB) { super(sA, sB); }
    
    /**
     * This Scoring Matrix Function rewards character matches 
     * and penalizes differences.
     */
    @Override
    public float scoreMatrix(T tA, T tB) 
    {
        if (tA.equals(tB))
            return SA_COST_MATCH;   // Point identity
        else
            return SA_COST_MISMAT;  // Point difference
    }
    /**
     * Gap in A means insertion (new entry in B)
     * @return Cost of one point insertion
     */
    @Override
    public float scoreMatrix(char cA, T tB)
    {
        // The only possibility is (cA == SA_GAP)           
        return SA_COST_INSERT;
    }
    /**
     * Gap in B means deletion from A (entry present in A but missing from B)
     * @return Cost of one point deletion
     */
    @Override
    public float scoreMatrix(T tA, char cB)
    {
        // The only possibility is (cB == SA_GAP)           
        return SA_COST_DELETE;
    }
    
    public double scoreGaussianTab(int jA, int jB) 
    {
        int diff = jA - jB;
        int dif2 = diff*diff;
        switch(dif2) {
            case 0:
                return 1.0;
            case 1:
                return 0.990950289;
            case 4:
                return 0.964289580;
            case 9:
                return 0.921439481;
            case 16:
                return 0.864629194;
            case 25:
                return 0.796703464;
            case 36: 
                return 0.720887121;
            case 49:  
                return  0.640533063;
            case 64:  
                return  0.558881305;
            case 81:  
                return  0.478852027;
            case 100:  
                return  0.402890311;
            case 121:  
                return  0.332871076;
            case 144:  
                return  0.270065474;
            case 169:  
                return 0.215162096;
            case 196:  
                return  0.168331816;
            case 225:  
                return  0.129321400;
            case 256: 
                return  0.097561468;
            case 289: 
                return  0.072275300;
            case 324:
                return  0.052578152;
            case 361:
                return  0.037559905;
            case 400:
                return  0.026347978;
            default:
                return 0;
        }
    }    
    public double scoreGaussian(int jA, int jB) 
    {
        int diff = jA - jB;
        int dif2 = diff*diff;
        return Math.exp( -dif2 / (2F*55) );
    }

    
    /************************************************************************
     * test_InsDelRematch
     * TODO: If this functionality were controlled from a higher level in
     * a part-whole hierarchy, it could better treat multiple element runs 
     * (gaps and insertions) as internally related, which retains more of
     * the information already found by the previous alignment step.
     * For example, a sentence could treat an insertion or deletion of
     * several words in a row as the insertion or deletion of a clause
     * or a phrase, not just as a set of words that happened to be 
     * contiguous.
     */
    public void test_InsDelRematch(int verbose, String prefix, int maxLen, int minLen)
    {
        if (isMaximal())
            return;
        
        if (verbose > 3) {
            Sx.puts(prefix + ". . . test_InsDelRematch . . . . . . . . .");
            for (IndexPair pair : mIndexPairs) {
                if (pair.mIdxA < 0 || pair.mIdxB < 0)
                    printPairIndicesFirst(pair, prefix, maxLen, minLen);
            }
        }
        //////////////////////////////////////////////////////////////////
        // TODO: this should really be for phrases
        ArrayList<T> insDelA = new ArrayList<T>();
        ArrayList<T> insDelB = new ArrayList<T>();
        for (int end = mIndexPairs.size(), j = 0; j < end; ) {
            IndexPair ip = mIndexPairs.get(j);
            if (ip.mIdxA < 0) {
                int len = -ip.mIdxA;
                int ixB =  ip.mIdxB;
                if (len == 1) {
                    insDelB.add(mSeqB.get(ixB));
                    j++;
                } else {
                    insDelB.addAll(mSeqB.subList(ixB, ixB + len));
                    j += len;
                }
            }
            else if (ip.mIdxB < 0) {
                int len = -ip.mIdxB;
                int ixA =  ip.mIdxA;
                if (len == 1) {
                    insDelA.add(mSeqA.get(ixA));
                    j++;
                } else {
                    insDelA.addAll(mSeqA.subList(ixA, ixA + len));
                    j += len;
                }
            } 
            else {
                j++;
            }
        }
        if (verbose > 3) {
            Sx.putsArray(prefix + "insDelA: ", insDelA);
            Sx.putsArray(prefix + "insDelB: ", insDelB);
        }
        
        //////////////////////////////////////////////////////////////////////////////
        //  Deletes and Inserts in contiguous arrays or a set ////////////////////////
        //  TODO: Don't keep ALL of these!
        int deleted[] = new int[mNumDeletes];
        int inserts[] = new int[mNumInserts];        
        HashSet<Integer> deleteSet = new HashSet<Integer>(mNumInserts);
        ArrayList<Integer> insertList = new ArrayList<Integer>();
        for (int jDel = 0, jIns = 0, end = mIndexPairs.size(), j = 0; j < end; j++) {
            IndexPair ip = mIndexPairs.get(j);
            if (ip.mIdxA < 0) {
                insertList.add(ip.mIdxB);
                inserts[jIns++] = ip.mIdxB;
            }
            else if (ip.mIdxB < 0) {
                deleteSet.add(ip.mIdxA);
                deleted[jDel++] = ip.mIdxA;
            }
        }
        
        /** Only re-matched pairs -- you have to infer what they replace 
         * TODO: array or map or ???? */
        ArrayList<IndexPair>  reMatches_del2ins = new ArrayList<IndexPair>(); 
        Map<Integer, Integer> rePairMap_del2ins = new HashMap<Integer, Integer>();
        
        int jIns;
        for (int jDel : deleted) {
            double maxScore = -999999;
            int    maxIndex = -1;
            T tB, tA = mSeqA.get(jDel);
            for (int end = insertList.size(), j = 0; j < end; j++) {
                jIns = insertList.get(j);
                tB = mSeqB.get(jIns);
                double score = scoreMatrix(tA, tB) * scoreGaussian(jDel, jIns);
                if (maxScore < score) {
                    maxScore = score;
                    maxIndex = j;
                }
            }
            if (maxScore > 0.15F) {
                jIns = insertList.get(maxIndex);
                IndexPair rip = new IndexPair(jDel, jIns);
                reMatches_del2ins.add(rip);
                rePairMap_del2ins.put(jDel, jIns);
                insertList.remove(maxIndex);
            }
        }
        
        // Collate and show results.........................
        ArrayList<IndexPair> allRePairs = new ArrayList<IndexPair>();
        IndexPair repair = null;
        String extPrefix = prefix + Spaces.get(14);
        if (verbose > 3) {
            Sx.puts(prefix + "Rematch: Del => Ins matches:");
            for (IndexPair pair : reMatches_del2ins) {
                printPairIndicesFirst(pair, prefix, maxLen, minLen);
            }
            Sx.puts(prefix + "RePaIrEd sequence:");
            int jRem = 0;
            for (IndexPair pair : mIndexPairs) {
                if (pair.mIdxA < 0) {
                    if (rePairMap_del2ins.containsValue(pair.mIdxB)) {             
                        printPairIndicesFirst(pair, "UnIns: " + extPrefix, maxLen, minLen);
                    } else {
                        allRePairs.add(pair);
                        printPairIndicesFirst(pair, "Insert:     ", maxLen, minLen);                      
                    }
                } else if (pair.mIdxB < 0) {
                    if (rePairMap_del2ins.containsKey(pair.mIdxA)) {
                        repair = reMatches_del2ins.get(jRem++);
                        allRePairs.add(repair);
                        printPairIndicesFirst(repair, "ReMatch:    ", maxLen, minLen);
                        printPairIndicesFirst(pair, "UnDel: " + extPrefix, maxLen, minLen);
                    } else {
                        allRePairs.add(pair);
                        printPairIndicesFirst(pair, "Delete:     ", maxLen, minLen);                      
                    }
                } else {
                    allRePairs.add(pair);
                    printPairIndicesFirst(pair, prefix, maxLen, minLen);
                }
            }
        } else {
            int jRem = 0;
            for (IndexPair pair : mIndexPairs) {
                if (pair.mIdxA < 0) {
                    if ( ! rePairMap_del2ins.containsValue(pair.mIdxB)) {             
                        allRePairs.add(pair);
                    }
                } else if (pair.mIdxB < 0) {
                    if (rePairMap_del2ins.containsKey(pair.mIdxA)) {
                        repair = reMatches_del2ins.get(jRem++);
                        allRePairs.add(repair);
                    } else {
                        allRePairs.add(pair);
                    }
                } else {
                    allRePairs.add(pair);
                }
            }            
        }
        
        if (verbose > 1) {
            Sx.puts(". . . . allRePairs  . . . .");
            for (IndexPair pair : allRePairs) {
                printPairIndicesFirst(pair, prefix, maxLen, minLen);
            }
        }

        //////////////////////////////////////////////////////////////////////////////
        
        
        if (verbose > 3) {
            ///////////////////////////////////////////////////////////////////
            // Using Map.Entry for pairs.  This feels clumsy.  The TreeMap will 
            // sort indices that are already add in ascending order.
            // Should be s/th like TreeMap<Integer, PriorityQueue<T>>
            // Also, same INSERT can match multiple DELETES
            ///////////////////////////////////////////////////////////////////
            Map<Integer, T> deletedMap = new TreeMap<Integer, T>();
            Map<Integer, T> insertsMap = new TreeMap<Integer, T>();
            ArrayList<IndexPair> rematchesMap = new ArrayList<IndexPair>(); 
            for (int end = mIndexPairs.size(), j = 0; j < end; j++) {
                IndexPair ip = mIndexPairs.get(j);
                if (ip.mIdxA < 0)
                    insertsMap.put(ip.mIdxB, mSeqB.get(ip.mIdxB));
                else if (ip.mIdxB < 0)
                    deletedMap.put(ip.mIdxA, mSeqA.get(ip.mIdxA));
            }
            for (Map.Entry<Integer, T> del : deletedMap.entrySet()) {
                Map.Entry<Integer, T> maxEntry = null;
                float maxScore = -999999;
                for (Map.Entry<Integer, T> ins : insertsMap.entrySet()) {
                    float score = scoreMatrix(del.getValue(), ins.getValue());
                    if (maxScore < score) {
                        maxScore = score;
                        maxEntry = ins;
                    }
                }
                if (maxScore > 0.3F) {
                    IndexPair rip = new IndexPair(del.getKey(), maxEntry.getKey());
                    rematchesMap.add(rip);
                }
            }
            Sx.puts(prefix + "Rematch MAP: Del => Ins matches:");
            for (IndexPair pair : rematchesMap) {
                printPairIndicesFirst(pair, prefix, maxLen, minLen);
            }
        }
        
        //////////////////////////////////////////////////////////////////////////////
        if (verbose > 3) {    
            int nRows = insDelA.size();
            int nCols = insDelB.size();
            float[][] transposeScores = ArrayFactory.makeFloatArray(mRows, mCols);
            IndexAndScore[] maxScoresA = new IndexAndScore[nRows];
            for (int row = 0; row < nRows; row++) {
                int   maxIndex = -1;
                float maxScore = -99999;
                for (int col = 0; col < nCols; col++) {
                    float scr = scoreMatrix(insDelA.get(row), insDelB.get(col));
                    transposeScores[row][col] = scr;
                    if (maxScore < scr) {
                        maxScore = scr;
                        maxIndex = col;
                    }
                }
                maxScoresA[row] = new IndexAndScore(maxIndex, maxScore);
            }
            if (verbose > 4) {
                for (int row = 0; row < nRows; row++) {
                    // new IndexPair(row, maxScoresA[row].mIndex);
                    Sx.format(prefix + "Max rematch: %2d  %2d\n", row,  maxScoresA[row].mIndex);
                }
            }  
        }
        
        /////////////////////////////////////////////////////////////////////
        // Basic alignment on the leftovers, fwiw.
        /////////////////////////////////////////////////////////////////////
        if (verbose > 4) {
            if (insDelA.size() > 0 && insDelB.size() > 0) {
                SequenceAlignment<T> insDelAln = new SequenceAlignment<T>(insDelA, insDelB);
                insDelAln.test_alignment(verbose, prefix, maxLen, minLen);
            }
        }
      
    }
    
    
    @Override
    protected int test_alignment(int verbose, String prefix, int maxLen, int minLen)
    {
        super.test_alignment(verbose, prefix, maxLen, minLen);
        // We need both deletes and inserts in order to swap any.
        if (mNumDeletes > 0 && mNumInserts > 0)  
            test_InsDelRematch(verbose, prefix + "    ", maxLen, minLen);
        return 0;
    }
    
    /************************************************************************
     * test_GSA
     */
    public static int test_GSA(int verbose, String strA, String strB)
    {
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        GlobalSequenceAlignment<Character> glob = new GlobalSequenceAlignment<Character>(seqA, seqB);
        return glob.test_alignment(verbose, 0);
    }
    
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test(int verbose)
    {
        //        test_GSA(verbose, "ATCTGAT", "ATCTGAT");
        //        test_GSA(verbose, "ATCTGAT", "TGCATA");
        //        test_GSA(verbose, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
        //        test_GSA(verbose, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");
        //        test_GSA(verbose, "abcDEFxyz0JKL", "abcJKLxyz0DEF");
        //        test_GSA(verbose, "ABcDEfGH", "ABfDEcGH");
        test_GSA(verbose, "ABnDpEoGH", "ABoDEqGHn");
        SentencesAlignment.unit_test(2);

        
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(3); }
}

