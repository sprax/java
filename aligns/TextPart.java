package sprax.aligns;

import java.util.ArrayList;
import java.util.List;

import sprax.sprout.Spaces;
import sprax.sprout.Sx;

interface TextPartInterface // TODO: why keep this non-public interface?
{
    /**
     * @return The underling string that spans this whole part
     */
    public String   getString();
    /**
     * @return The beginning of the underling string
     */
    public String   getSubStr(int length);
    public String   getSubStr(int maxLen, int minLen);
    /**
     * @return The count of its immediate sub-parts. Examples: 
     * From a paragraph, return the sentence count.
     * From a sentence, return the clause count.
     * From a clause, return the word count.
     */
    public int      getNumParts();
    public int      getNumWords();
    public int      getNumChars();
    
    public void     putsParts();
}

public abstract class TextPart implements TextPartInterface
{
    String mString;
    @Override
    public String getString() {
        return mString;
    }
    @Override
    public String getSubStr(int len) {
        if (len > getString().length())
            return getString();
        return getString().substring(0, len);
    }    
    @Override
    public String getSubStr(int maxLen, int minLen) {
      int strLen = getString().length();
        if (maxLen < strLen)
            return getString().substring(0, maxLen);
        return getString() + Spaces.get(minLen - strLen);
    }
    @Override
    public int getNumChars()       { return mString.length(); }
}

class TextPartsAlignment<T extends TextPart> extends SequenceAlignment<T>
{
//    TextPartsAlignment(T[] sa, T[] sb) {
//        super(sa, sb);
//    }
    TextPartsAlignment(ArrayList<T> sa, ArrayList<T> sb) {
        super(sa, sb);
    }
    T getPartA(int idx) {
      return mSeqA.get(idx);
    }
    T getPartB(int idx) {
      return mSeqB.get(idx);
    }
//    @Override
//    public void printPairIndicesOut(IndexPair ip, int indent, int maxLen, int minLen)
//    {
//        if (indent > 0)
//            Sx.print(StringOfSpaces.get(indent));
//        Sx.format("<%2d | %s> <%s | %2d>\n"
//                , ip.mIdxA
//                , (ip.mIdxA < 0 ? StringOfSpaces.get(minLen) : getPartA(ip.mIdxA).getSubStr(maxLen, minLen))
//                , (ip.mIdxB < 0 ? StringOfSpaces.get(minLen) : getPartB(ip.mIdxB).getSubStr(maxLen, minLen))
//                , ip.mIdxB
//        );
//    }
//    @Override
//    public void printPairs(int indent, int maxLen, int minLen)
//    {
//        for (IndexPair pair : mIndexPairs) {
//            printPairIndicesOut(pair, indent, maxLen, minLen);
//        }
//    }
    
    public static <V extends TextPart> void printPair(IndexPair ip, List<V> vA, List<V> vB
            , int indent, int maxLen, int minLen)
    {
        if (indent > 0)
            Sx.print(Spaces.get(indent));
        Sx.format("<%2d | %s> <%s | %2d>\n"
                , ip.mIdxA
                , (ip.mIdxA < 0 ? Spaces.get(minLen) : vA.get(ip.mIdxA).getSubStr(maxLen, minLen))
                , (ip.mIdxB < 0 ? Spaces.get(minLen) : vB.get(ip.mIdxB).getSubStr(maxLen, minLen))
                , ip.mIdxB
        );
    }
    public static <V extends TextPart> void printPairs(ArrayList<IndexPair> indexPairs, List<V> vA, List<V> vB
            , int indent, int maxLen, int minLen)
    {
        for (IndexPair cp : indexPairs) {
            printPair(cp, vA, vB, indent, maxLen, minLen);
        }
    }    
    

    public static void main(String[] args) { TextAlignment.unit_test(); }
}
