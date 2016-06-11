package sprax.aligns;

import java.util.ArrayList;
import java.util.regex.Pattern;

import sprax.sprout.Spaces;
import sprax.sprout.Sx;

public class Paragraph extends TextPart
{
    static       int sDbg = 2;
    static final int sShortLen = 10;
    
    IndexedText         mParentText;
    ArrayList<Sentence> mSentences;   // To be obtained as a subList of mParentText.mSentences
    ArrayList<Clause>   mClauses;     // To be obtained as a subList of mParentText.mClauses
    ArrayList<Word>     mWords;       // To be obtained as a subList of mParentText.mWords
    
    int         mSentenceOffsets[];
    int         mSeqOrd = -1;   // Ordinal position in sequence (1, 2, 3, ...)
    int         mSentenceOffset;
    int         mClauseOffset;
    int         mWordOffset;
    

    Paragraph(String str, int idx, int sentenceOffset, int clauseOffset, int wordOffset) {
        mString = str;
        mSeqOrd = idx;
        mSentenceOffset = sentenceOffset;
        mClauseOffset = clauseOffset;
        mWordOffset = wordOffset;
        parseString2Sentences(str);
    }
    @Override
    public String toString() {  return mString; } 
    public String toShortString() { 
        int end = Math.min(sShortLen, mString.length());
        String padding = "";
        if (sShortLen > end)
            padding = Spaces.get(sShortLen - end);
        return String.format("%s%s",  mString.substring(0, end), padding); 
    }
    @Override
    public int getNumParts() {
        return getNumSentences();
    }
    @Override
    public int getNumWords()      { return mWords.size(); }
    public int getNumClauses()    { return mClauses.size(); }
    public int getNumSentences()  { return mSentences.size(); }
    
    /**
     * TODO: Requires exactly 2 spaces between sentences.  Way too brittle!
     * @param str
     */
    public void parseString2Sentences(String paragraphStr) 
    {
    ////String[] sentenceStrs = Pattern.compile("(  |\\. )").split(paragraphStr);
        String[] sentenceStrs = Pattern.compile("  ").split(paragraphStr);
        mSentenceOffsets = new int[sentenceStrs.length];
        mSentences       = new ArrayList<Sentence>(sentenceStrs.length);
        mClauses       = new ArrayList<Clause>();
        mWords           = new ArrayList<Word>();
        for (int offset = 0, j = 0; j < sentenceStrs.length; j++) {
            String sStr = sentenceStrs[j].trim();
            if (sStr.length() < 1)
                throw new IllegalArgumentException("Empty sentence!");
            Sentence sentence = new Sentence(sStr);
            mSentences.add(sentence);
            mSentenceOffsets[j] = offset;
            
            mClauses.addAll(sentence.mClauses);
            mWords.addAll(sentence.mWords);
            
            int endSent =  offset + sentenceStrs[j].length();
            
            if (sDbg > 5) {
                int endSub =  sentenceStrs[j].length();
                if (endSub > 16)
                    endSub = 16;
                Sx.format("%2d [%s|%s|%s]\n", j, sentenceStrs[j].substring(0, endSub)
                        , paragraphStr.substring(offset, offset+endSub), mSentences.get(j));  
            }
            offset = endSent + 2;
        }
    }

    public void putsSentences()
    {
        for (Sentence sentence : mSentences) {
            Sx.puts(sentence);
        }
        Sx.puts();
    }
    
    @Override
    public void putsParts() {
        putsSentences();
    }
}

class ParagraphsAlignment extends TextPartsAlignment<Paragraph>
{
    ParagraphsAlignment(ArrayList<Paragraph> sA, ArrayList<Paragraph> sB)  { super(sA, sB); }
    
    @Override
    public float scoreMatrix(Paragraph pA, Paragraph pB) 
    {
        if (pA == null || pB == null) // insertion in B or deletion from A
            return 0;
        
        float score = 1F;
        float numA = pA.getNumSentences();
        float numB = pB.getNumSentences();
        float diff = (numA - numB)/(numA + numB);   // TODO: beware division by 0
        float drsSents = diff * diff;        // difference ratio squared for Sentences
        if (drsSents > 0.125F)
            score -= 0.7F;
        else
            score -= drsSents;
        
        // TODO: normalize by dividing by totals (numA/totA & numB/totB) 
        numA = pA.getNumWords();
        numB = pB.getNumWords();
        diff = (numA - numB)/(numA + numB);   // TODO: beware division by 0
        float drsWords = diff * diff;
        if (drsWords > 0.33)
            score -= 0.6F;
        
        // Chapter title and heading comparison
        if (pA.mSeqOrd < 3 && pB.mSeqOrd < 3) {
            // All caps comparison
            String upperA = pA.mString.toUpperCase();
            String upperB = pB.mString.toUpperCase();
            if (upperA.equals(pA.mString) && upperB.equals(pB.mString)) {
                int minStrLen = Math.min(pA.mString.length(), pB.mString.length());
                if (minStrLen > 6)
                    minStrLen = 6;
                if ( ! pA.mString.substring(0, minStrLen).equals(pB.mString.substring(0, minStrLen)))
                    score -= 0.5F;
            }
            if (upperA.equals(pA.mString) || upperB.equals(pB.mString)) {
                if (upperA.charAt(0) == upperB.charAt(0))
                    score += 0.5F;            
            }
        } else {
            
            // Index comparison
            // TODO: Should be measured and possibly "relaxed" from both ends...
            numA = pA.mSeqOrd;
            numB = pB.mSeqOrd;
            diff = (numA - numB)/(numA + numB);
            float drsOrder = diff * diff;
            //Sx.format("%g  %g  [%s|%s]\n", diff, drsOrder, pA.getSubStr(24), pB.getSubStr(24));
            score -= drsOrder;
        }

        numA = pA.mSentenceOffset;
        numB = pB.mSentenceOffset;
        diff = (numA - numB)/(numA + numB + 1);
        float drss = diff < 0 ? -diff : diff;
        if (sDbg > 2) {
          Sx.format("Sentence offset: %d  %d  %g  %g  [%s|%s]\n"
              , pA.mSentenceOffset, pB.mSentenceOffset
              , diff, drss, pA.getSubStr(24), pB.getSubStr(24));
        }
        score -= drss;

        return  score;
    }    
    
    public static void main(String[] args) { TextAlignment.unit_test(); }
}
