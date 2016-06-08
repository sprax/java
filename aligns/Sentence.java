package sprax.aligns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sprax.Sx;

public class Sentence extends TextPart
{
    ArrayList<Clause>   mClauses;
    ArrayList<Word>     mWords;
    static Pattern sPatternSepClause = Pattern.compile("([,;�]|--+)");
    
    int     mClauseOffsets[];
    int     mWordOffsets[];
    
    public Sentence(final String str) {
        int len = str.length();
        while (IndexedText.isSentenceTerminator(str.charAt(--len)))
            ;
        mString = str.substring(0, len+1);
        parseStringToClauses(mString);
    }
    @Override
    public String toString() {
        return mString;
    }
    
    /**
     * TODO: Requires exactly 1 space between words.  Way too brittle!
     */
    public void parseStringToClauses(String sentenceStr) 
    {
        String clauseStrs[] = sPatternSepClause.split(sentenceStr);
        mClauses = new ArrayList<Clause>(clauseStrs.length);
        mWords   = new ArrayList<Word>();
        mClauseOffsets = new int[clauseStrs.length];
        for (int offset = 0, j = 0; j < clauseStrs.length; j++) {
            String cStr = clauseStrs[j].trim();
            if (cStr.length() < 1)
                //throw new IllegalArgumentException("Empty clause!");
                continue;
            mClauseOffsets[j] = offset;
            Clause clause = new Clause(cStr);
            mClauses.add(clause);
            
            mWords.addAll(clause.mWords);
            int end =  offset + clauseStrs[j].length();
//            {
//                int endSub =  mWords[j].length();
//                if (endSub > 16)
//                    endSub = 16;
//                Sx.format("%2d [%s|%s]\n", j, mWords[j].substring(0, endSub), str.substring(offset, offset+endSub));  
//            }
            offset = end + 1;
        }
    }
    
    public List<Clause> getClauses()
    {
        return mClauses;
    }    
    public void putsClauses()
    {
        for (Clause clause : mClauses) {
            Sx.puts(clause);
        }
        Sx.puts();
    }
    
    public int getNumClauses()     { return mClauses.size(); }

    @Override
    public int getNumParts() {
        return getNumClauses();
    }
    @Override
    public int getNumWords() {
        return mWords.size();
    }
    @Override
    public void putsParts() {
        putsClauses();
    }
}


class SentencesAlignment extends TextPartsAlignment<Sentence>
{
    SentencesAlignment(ArrayList<Sentence> sA, ArrayList<Sentence> sB)  { super(sA, sB); }
    
    @Override
    public float scoreMatrix(Sentence pA, Sentence pB) 
    {
        if (pA == null || pB == null) // insertion in B or deletion from A
            return 0;
        
        float score = 1F;
        if (pA.mString.startsWith("He scoured ")) {
            if (pB.mString.startsWith("He scoured ")) {
                score *= 1;
            }
        }
            
        float numA = pA.getNumClauses();
        float numB = pB.getNumClauses();
        float diff = (numA - numB)/(numA + numB + 1);   // TODO: hack?
        float drss = diff * diff;        // difference ratio squared
        if (drss > 0.125F)
            score -= 0.7F;
        else
            score -= drss;
        
        // TODO: normalize by dividing by totals (numA/totA & numB/totB) ?
        numA = pA.getNumWords();
        numB = pB.getNumWords();
        diff = (numA - numB)/(numA + numB + 1);   // TODO: hack?
        float drsWords = diff * diff;
        if (drsWords > 0.1)
            score -= 0.5F;
        else
            score -= drsWords;
        
        return  score;
    }    
    
    // TODO: get "by the might of his arm" to match "through the might of his arm"
    // TODO: also "saw" <-> "could already see"
    public static String sOrmsby1 = 
        "Already the poor man saw himself crowned by the might of his arm Emperor of Trebizond at least; "
        + "and so, " 
        + "led away by the intense enjoyment he found in these pleasant fancies, "
        + "he set himself forthwith to put his scheme into execution.";
    
    static String sRutherford1 =
        "The poor man could already see himself being crowned Emperor of Trebizond, "
        + "at the very least, "
        + "through the might of his arm; "
        + "and so, "
        + "possessed by these delightful thoughts and carried away by the strange pleasure that he derived from them, "
        + "he hastened to put into practice what he so desired.";
    
    static String sEnHungerGames1[] = {
        "Our part of District 12, nicknamed the Seam, is usually crawling with coal miners "
        + "heading out to the morning shift at this hour."
        , "Men and women with hunched shoulders, swollen knuckles, many who have long since stopped trying " 
        + "to scrub the coal dust out of their broken nails, the lines of their sunken faces."
        , "But today the black cinder streets are empty."
        , "Shutters on the squat gray houses are closed."
        , "The reaping isn�t until two."
        , "May as well sleep in."
        , "If you can."
    };
    static String sEsHungerGames1[] = {
        "Nuestra parte del Distrito 12, a la que solemos llamar la Veta, est� siempre llena a estas " 
        + "horas de mineros del carb�n que se dirigen al turno de ma�ana."
        , "Hombres y mujeres de hombros ca�dos y nudillos hinchados, muchos de los cuales ya ni siquiera "
        + "intentan limpiarse el polvo de carb�n de las u�as rotas y las arrugas de sus rostros hundidos."
        , "Sin embargo, hoy las calles manchadas de carboncillo est�n vac�as y las contraventanas de las " 
        + "achaparradas casas grises permanecen cerradas."
        , "La cosecha no empieza hasta las dos, as� que todos prefieren dormir hasta entonces...  si pueden."        
    };
    
    static String paraEn = TextHungerGames.paraEn_1[    TextHungerGames.paraEn_1.length -1  ];
    static String paraEs = TextHungerGames.paraEsTello[ TextHungerGames.paraEsTello.length-1];

    public static int test_sentence_pair(Sentence sA, Sentence sB, WordDifference wordDiff, int level) 
    {
        String  testName = Sentence.class.getName() + ".test_sentence_pair";
        Sx.puts(testName + " BEGIN");
        
        int clauseLen   = 32;
        int wordLen     = 10;
        
        ClausesAlignment ca = new ClausesAlignment(sA.mClauses, sB.mClauses);
        ca.test_alignment(2, "", clauseLen, clauseLen);
        if (level > 0) {
            WordsAlignment wa = new WordsAlignment(sA.mWords, sB.mWords, wordDiff);
            wa.test_alignment(3, 0, wordLen, wordLen);
            Sx.puts(sA);
            Sx.puts(sB);
        }

        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static int unit_test(int level)
    {
        String  testName = Sentence.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        Sentence sA = new Sentence(sOrmsby1);
        Sentence sB = new Sentence(sRutherford1);
        Sentence sC;
      //WordDifference wordDiffToy = new WordDifferenceEnToy();
        WordDifference wordDiffEnEs = new WordDifferenceEnEs();
        
        if (level > 4) {
          //test_sentence_pair(sA, sB, wordDiffToy, level);

          sA = new Sentence(sEnHungerGames1[1]);
          sB = new Sentence(sEsHungerGames1[1]);
          test_sentence_pair(sA, sB, wordDiffEnEs, level);

          sA = new Sentence(sEnHungerGames1[0]);
          sB = new Sentence(sEsHungerGames1[0]);
          test_sentence_pair(sA, sB, wordDiffEnEs, level);

          Paragraph pA = new Paragraph(paraEn, 0, 0, 0, 0);
          Paragraph pB = new Paragraph(paraEs, 0, 0, 0, 0);
          sA = pA.mSentences.get(0);
          sB = pB.mSentences.get(0);
          test_sentence_pair(sA, sB, wordDiffEnEs, level);
        }

        sA = new Sentence("I waited, and I'm still waiting.");
        sB = new Sentence("Esper�, y sigo esperando.");
        sC = new Sentence("Esper�, y estoy esperando todav�a!");
        test_sentence_pair(sA, sB, wordDiffEnEs, level);
        test_sentence_pair(sA, sC, wordDiffEnEs, level);

        
        Sx.puts(testName + " END");    
        return 0;
    }

    public static void main(String[] args) { unit_test(3); }
}

/****************************************************************************
<  0  -1>   I                      
<  1   0>   waited     = Esper�    
<  2   1>   and        = y         
<  3  -1>   I'm                    
<  4   2>   still      = sigo      
<  5   3>   waiting    = esperando 

    <  0  -1>   I                      
    <  1   0>   waited     = Esper�    
    <  2   1>   and        = y         
    <  3   2>   I'm        = estoy     
    <  4   4>   still      = todav�a   
    <  5   3>   waiting    = esperando 
*****************************************************************************/