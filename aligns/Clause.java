package sprax.aligns;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;

public class Clause extends TextPart
{
    static Pattern sPatternSepWords = Pattern.compile("\\s+");
    
    List<Word>  mWords;
    
    int     mWordOffsets[];
    
    public Clause(final String str) {
        mString = str;
        parseStringToWords(str);
    }
    @Override
    public String toString() {
        return mString;
    }
    
    /**
     * TODO: Requires exactly 1 space between words.  Way too brittle!
     */
    public void parseStringToWords(final String sentenceStr) 
    {
        String wordStrs[] = sPatternSepWords.split(sentenceStr);
        mWords = new ArrayList<Word>(wordStrs.length);
        mWordOffsets = new int[wordStrs.length];
        for (int offset = 0, j = 0; j < wordStrs.length; j++) {
            mWordOffsets[j] = offset;
            Word word = new Word(wordStrs[j]);
            mWords.add(word);
            int end =  offset + wordStrs[j].length();
            //            {
            //                int endSub =  mWords[j].length();
            //                if (endSub > 16)
            //                    endSub = 16;
            //                Sx.format("%2d [%s|%s]\n", j, mWords[j].substring(0, endSub), str.substring(offset, offset+endSub));  
            //            }
            offset = end + 1;
        }
    }
    
    public void putsWords()
    {
        for (Word word : mWords) {
            Sx.puts(word);
        }
        Sx.puts();
    }
    
    @Override
    public int getNumParts() {
        return getNumWords();
    }
    @Override
    public int getNumWords() {
        return mWords.size();
    }
    @Override
    public void putsParts() {
        putsWords();
    }
    
}


class ClausesAlignment extends TextPartsAlignment<Clause>
{
    ClausesAlignment(ArrayList<Clause> sA, ArrayList<Clause> sB)  { super(sA, sB); }
    
    @Override
    public float scoreMatrix(Clause pA, Clause pB) 
    {
        if (pA == null || pB == null) // insertion in B or deletion from A
            return 0;
        
        float score = 1F;
        // FIXME
        if (pA.mString.startsWith("He scoured ")) {
            if (pB.mString.startsWith("He scoured ")) {
                score *= 1;
            }
        }
        
        // TODO: normalize by dividing by totals (numA/totA & numB/totB) ?
        float numA = pA.getNumWords();
        float numB = pB.getNumWords();
        float diff = (numA - numB)/(numA + numB + 1);   // TODO: hack?
        float drsWords = diff * diff;
        if (drsWords > 0.1)
            score -= 0.5F;
        else
            score -= drsWords;
        
        return  score;
    }    
    
    public static void printClauses(Sentence sA)
    {
        int num = 0;
        for (Clause clause : sA.mClauses) {
            Sx.format("%2d: %s\n", num++, clause);
        }        
        Sx.puts();
    }
    
    public static int unit_test(int level)
    {
        String  testName = Sentence.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        String textFilePath;
        
        Sx.puts("PREPOSITIONS: 1 WORD");
        textFilePath = FileUtil.getTextFilePath("En/Prepositions1.txt");
        TreeSet<String> preps1 = TextFileReader.readFileIntoTreeSet(textFilePath);
        for (String sp : preps1)
            Sx.puts(sp);
        Sx.puts();
        
        Sx.puts("PREPOSITIONS: 2 WORDS");
        textFilePath = FileUtil.getTextFilePath("En/Prepositions2.txt");
        TreeSet<String> preps2 = TextFileReader.readFileIntoTreeSet(textFilePath);
        for (String sp : preps2)
            Sx.puts(sp);
        Sx.puts();
        
        
        Sx.puts("PREPOSITIONS: 3 WORDS");
        textFilePath = FileUtil.getTextFilePath("En/Prepositions3.txt");
        TreeSet<String> preps3 = TextFileReader.readFileIntoTreeSet(textFilePath);
        for (String sp : preps3)
            Sx.puts(sp);
        Sx.puts();
        
        
        
        String strA = "The poor man could already see himself being crowned Emperor of Trebizond, " 
                + "at the very least, "
                + "through the might of his arm; "
                + "and so, "
                + "possessed by these delightful thoughts -- "
                + "and carried away by the strange pleasure that he derived from them –  "
                + "he hastened to put into practice what he so desired.";
        
        Sentence sA = new Sentence(strA);
        sA.putsParts();
        for (Clause clause : sA.getClauses()) {
            clause.putsParts();
        }
        Sx.puts();        

        for (Clause clause : sA.getClauses()) {
            for (Word word : clause.mWords) {
                String str = word.getString();
                if (preps1.contains(str)) {
                    Sx.puts();
                }
                Sx.print(str + " ");
            }
        }
        Sx.puts("\n");

        
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args)
    { 
        unit_test(2);
        //SentencesAlignment.unit_test(2);
    }
}
