package sprax.aligns;

import java.util.ArrayList;
import java.util.List;

import sprax.Sx;

/**
 * TODO: Next Steps
 * 0) Try more modern texts
 * 1) Distinguish phrases within sentences and/or clauses, and each comprising one or more words.
 * 2) Use lower-level matching and alignment to correct higher level segmentation
 *    a) Add default greater weighting for a match at the beginning (1.75x?)
 *    b) Add default greater weighting for a match at the end (1.24x?)
 *    c) Make this depend on the languages?  
 * 3) Use lower-level matching and alignment to correct higher level alignment (if this is different?)
 * 4) Weight transpositions by distance, e.g. C*Score/Distance.
 *    a) Special consideration for Germanic verb-last constructions?
 * 5) Add a real thesaurus for words
 * 6) Add at least a toy thesaurus for phrases
 * 7) Add a bilingual thesaurus for words
 * 8) Recognize parts of speech, and divide thesauri accordingly
 *    a) Precedence: Verbs, Nouns, Adjectives, Adverbs, Prepositions, Articles?
 * 9) Recognize phrase types
 * 10) Use grammar.
 *    a) Extend "grammar checking" methods to segment and type phrases and sentences.
 *    b) Check "re-paired" target sentences for correctness and correspondence with source sentences.
 * 
 * 
 * TODO: The similarity of a pair of sentences to another is a function of:
 * 1) global metrics such as their relative lengths in chars, words, clauses, and idiomatic phrases
 * 2) the similarity score from the alignment of their clauses (coarse, fuzzy, or exact?)
 * 3) the sum of semantic similarity scores from their pairs of corresponding clauses
 *      a) where correspondence comes directly from linear alignment, or
 *      b) where correspondence allows for permutations in their order
 * 4) the sum of inverse frequency score for (nearly) exact matches of rare words or phrases,
 *    that is, matching words or phrases that are deemed not merely close but actually equivalent
 *    should be weighted in inverse proportion to their commonness.  Matches of rare words, such
 *    as rare names or other proper nouns. count more than matches of commonplace words, such as 
 *    "the", "and", or "was".  For example, "Mt. McKinley" and "Denali" might mean the same thing,
 *    but they are not the same name; they are semantically similar but nominally dissimilar.
 *    Likewise, "loud", "noisy" and "obstreperous" may all be synonyms, but the first two are
 *    common and the last is rare, so finding "obstreperous" in two potentially corresponding
 *    phrases counts for more than finding "loud" in one and "noisy" in the other.
 *    
 * TODO: The similarity of two paragraphs to each other is a function of:
 * 1) global metrics such as their relative lengths in chars, words, clauses, and sentences.
 * 2) coincidence with sentence of sentence boundaries.  This is "external" rather
 *    than "internal" similarity -- two paragraphs in different texts are similar
 *    if they are position nearly the same way relative to the sentences that occur
 *    before and after them.
 * 3) the weight sum of normalized similarity scores of their sentences.  
 *    The 1st sentences are the most important, then the 2nd, 3rd, etc.
 *    
 *  More TODO’s:
    A) Recognize semantically connected phrases that are contained inside long
      clauses or that span multiple short clauses. 
          o For example, two nouns or two adjectives separated by a conjunction:
           “dangers and perils”, “right and requisite”, and “fast or slow”
          o Matching “he hit upon the strangest notion” <-> “he conceived the strangest notion” 
            might be easy using matching words + synonyms
          o Aligning “roaming the world over” <-> “travel about the world” might be harder
    B) Look for nearby transpositions within and between such phrases
          o “eternal renown and fame” <-> “eternal fame and renown”
          o … “peril and danger” … <-> … “perils and dangers” … .
    C) Use fuzzier matching in general (easy)
    D) Add a bottom-up correction step after the initial top-down parsing 
      and alignment, then iterate.
          o For example: parse and align sentences, then compare the clauses and words of nearby sentences, and use those results re-align the sentences.  Repeat.
    E) Where the alignment scores are bad, try adding anchors and relaxation.
          o For example, “Emperor of Trebizond” appears once in each text, so the 2 phrases clearly match, their containing clauses almost certainly match, and their entire containing sentences *probably* match. 
          o Find another anchor pair and you can sort of “adaptively” interpolate the alignment between these pairs.
    F) Decide if adding a crude machine translation step is in any way useful for finding the alignment.  So far, no.  (I tried Google Translate, and it would leave out verbs, and small changes in input can produce large changes in output.) 
 *
 * @author sprax
 *
 */


public class TextAlignment
{
    IndexedText mTextA;
    IndexedText mTextB;
    
    ArrayList<IndexPair>  mParagraphPairs;
    ArrayList<IndexPair>  mSentencePairs;
    List<IndexPair>       mWordIndexPairs;
    
    SentencesAlignment    mSentencesAlignment;
    ParagraphsAlignment   mParagraphsAlignment;
    
    TextAlignment(IndexedText textA, IndexedText textB)
    {
        mTextA = textA;
        mTextB = textB;
       
        mSentencesAlignment = new SentencesAlignment(mTextA.mSentences, mTextB.mSentences);
        mParagraphsAlignment = new ParagraphsAlignment(mTextA.mParagraphs, mTextB.mParagraphs);
        
        mSentencesAlignment.doAlignment();
        mParagraphsAlignment.doAlignment();
    }

    @Deprecated
    public void alignParagraphs()   // use alignment machinery  
    {
        // FIXME
        int numA = mTextA.numParagraphs();
        int numB = mTextB.numParagraphs();
        int numParagraphs = Math.max(numA, numB);
        mParagraphPairs = new ArrayList<IndexPair>(numParagraphs);
        for (int j = 0; j < numParagraphs; j++) {
            if (j <= numA && j <= numB)
                mParagraphPairs.add(new IndexPair( j, j));
            else if (j > numA)
                mParagraphPairs.add(new IndexPair(-1, j));
            else
                mParagraphPairs.add(new IndexPair( j,-1));
        }
    }
    
    protected static TextAlignment createTextAlignment(int level, WordDifference wordDiff, LabelAndText ttA, LabelAndText ttB)
    {
        int paraLen = 44;
        int sentLen = 36;
        int clauseLen = 28;
        int wordLen = 12;
        String preSentence = "        ";
        String preClause = preSentence + preSentence;
        String preWord = preClause + preSentence;
        
        IndexedText textA = new IndexedText(ttA.mParagraphStrings);
        IndexedText textB = new IndexedText(ttB.mParagraphStrings);
        
        Sx.format("Authors:     %s vs. %s\n", ttA.mLabel, ttB.mLabel);
        Sx.format("Paragraphs:  %4d    %4d\n", textA.mParagraphs.size(), textB.mParagraphs.size());
        Sx.format("Sentences:   %4d    %4d\n", textA.mSentences.size(), textB.mSentences.size());
        Sx.format("Clauses:     %4d    %4d\n", textA.mClauses.size(), textB.mClauses.size());
        Sx.format("Words:       %4d    %4d\n", textA.mWords.size(), textB.mWords.size());
        
        TextAlignment ta = new TextAlignment(textA, textB);
        
        for (IndexPair ip : ta.mParagraphsAlignment.mIndexPairs) {
            ta.mParagraphsAlignment.printPairIndicesFirst(ip, "", paraLen, paraLen);
            
            if (level > 1 && ip.mIdxA > 1 && ip.mIdxB > 1) {
                Paragraph pgA = ta.mTextA.mParagraphs.get(ip.mIdxA);
                Paragraph pgB = ta.mTextB.mParagraphs.get(ip.mIdxB);
                SentencesAlignment sa = new SentencesAlignment(pgA.mSentences, pgB.mSentences);
                sa.test_alignment(0);
                if (level > 2) {
                    for (IndexPair sp : sa.mIndexPairs) {
                        sa.printPairIndicesFirst(sp, preSentence, sentLen, sentLen);
                        if (sp.mIdxA >= 0 && sp.mIdxB >= 0) {
                            if (level > 3 || sp.mIdxA == 1 && sp.mIdxB == 1) {
                                Sentence snA = pgA.mSentences.get(sp.mIdxA);
                                Sentence snB = pgB.mSentences.get(sp.mIdxB);
                                if (level > 4) {
                                    ClausesAlignment ca = new ClausesAlignment(snA.mClauses, snB.mClauses);
                                    ca.test_alignment(1);
                                    ca.printPairs(preClause, clauseLen, clauseLen);
                                    //Sx.puts();
                                    if (level > 5) {
                                        WordsAlignment wa = new WordsAlignment(snA.mWords, snB.mWords, wordDiff);
                                        wa.test_alignment(0);
                                        wa.printPairs(preWord, wordLen, wordLen);
                                    }
                                    SentencesAlignment.test_sentence_pair(snA, snB, wordDiff, level);
                                }
                            }
                        }
                    }
                }
            }
        }
        Sx.puts();
        return ta;
    }

    public static int unit_test() 
    {
        String  testName = TextAlignment.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN"); 
        
        Paragraph pg = new Paragraph("The one.  The two!  The three?", 0, 0, 0, 0);        
        Sx.puts(pg.getNumParts());
        
        WordDifferenceEnToy wordDiffToy = new WordDifferenceEnToy();
        WordDifferenceEnEs  wordDiffEnEs = new WordDifferenceEnEs();
        


        TextAlignment ta;
        //        ta = createTextAlignment(1, DonQ.EsCervantes, DonQ.EnJarvis);
        //        ta = createTextAlignment(1, DonQ.EsCervantes, DonQ.EnOrmsby);
        //        ta = createTextAlignment(1, DonQ.EsCervantes, DonQ.EnRutherford);
        //        ta = createTextAlignment(1, DonQ.EsCervantes, DonQ.EnGoogleEs);
        //        
        //        ta = createTextAlignment(1, DonQ.EnGoogleEs, DonQ.EnJarvis);
        //        ta = createTextAlignment(1, DonQ.EnGoogleEs, DonQ.EnOrmsby);
        //        ta = createTextAlignment(1, DonQ.EnGoogleEs, DonQ.EnRutherford);
        //        
        //        ta = createTextAlignment(1, DonQ.EnJarvis, DonQ.EnOrmsby);
        //        ta = createTextAlignment(1, DonQ.EnJarvis, DonQ.EnRutherford);
        ta = createTextAlignment(4, wordDiffToy, DonQ.EnOrmsby, DonQ.EnRutherford);
        ta = createTextAlignment(6, wordDiffEnEs, TextHungerGames.EnCollins, TextHungerGames.EsTello);
        
        //        ta = createTextAlignment(1, DonQ.EnGoogleEs, DonQ.EnGoogleEs);
        if (ta.mWordIndexPairs != null)
            Sx.puts(ta.mWordIndexPairs.size());
        
        Sx.puts(testName + " END");    
        return 0;
    }
    


    public static void main(String[] args) { unit_test(); }
    
}