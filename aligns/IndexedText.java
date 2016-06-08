package sprax.aligns;

import java.util.ArrayList;

import sprax.Spaces;

public 

class IndexedText
{
    ArrayList<Paragraph>    mParagraphs;
    ArrayList<Sentence>     mSentences;
    ArrayList<Clause>       mClauses;
    ArrayList<Word>         mWords;
    public int numParagraphs()  { return mParagraphs.size(); }
    public int numSentences()   { return mSentences.size(); }
    public int numWords()       { return mWords.size(); }

    
    IndexedText(String paraStrings[]) 
    {
      int numParas = paraStrings.length;
      mParagraphs = new ArrayList<Paragraph>(numParas);
      mSentences  = new ArrayList<Sentence>();
      mClauses    = new ArrayList<Clause>();
      mWords      = new ArrayList<Word>();

      for (int j = 0; j < numParas; j++) {
        Paragraph pg = new Paragraph(paraStrings[j], j, mSentences.size(), mClauses.size(), mWords.size());
        mParagraphs.add(pg);
        mSentences.addAll(pg.mSentences);
        mClauses.addAll(pg.mClauses);
        mWords.addAll(pg.mWords);
      }
    }


    @Deprecated
    public String getSentenceStr(int idx, int len)
    {
        if (idx < 0)
            return Spaces.get(len);
        return mParagraphs.get(idx).getSubStr(len);
    }
    /**
     * Returns the beginning of the indexed string, 
     * or empty space if the index is invalid.
     */
    public static String getBegStr(String strs[], int idx, int begLen)
    {
        if (idx < 0)
            return Spaces.get(begLen);
        else if (idx >= strs.length)
            return Spaces.get(begLen);
        
        String str = strs[idx];
        int strLen = str.length();
        if (begLen > strLen)
            return str + Spaces.get(begLen - strLen);
        return str.substring(0, begLen);
    }
    
    public static String getBegStr(String str, int begLen) {
        if (str == null)
            return Spaces.get(begLen);
        int strLen = str.length();
        if (begLen > strLen)
            return str + Spaces.get(begLen - strLen);
        return str.substring(0, begLen);        
    }
    
    static boolean isWordInitiator(char ch) {
        return ('a' <= ch && ch <= 'z'  ||
                'A' <= ch && ch <= 'Z'  || 
                '0' <= ch && ch <= '9'  || 
                128 <= ch && ch <= 165  ||
                '#' <= ch && ch <= '%'  ||
                255 <  ch
        );
    }
    
    static boolean isWordInterior(char ch) {
        return ('a' <= ch && ch <= 'z'  ||
                'A' <= ch && ch <= 'Z'  || 
                '0' <= ch && ch <= '9'  || 
                128 <= ch && ch <= 165  ||
                '#' <= ch && ch <= '%'  ||
                255 <  ch
        );
    }
    
    static boolean isWordTerminator(char ch) {
        return (ch < '#'   || 
                ('(' <= ch && ch < '-')     || 
                ('.' <= ch && ch < '0')     || 
                (':' <= ch && ch < '@')     ||
                ('Z' <  ch && ch < 'a')     || 
                ('z' <  ch && ch < 128)     || 
                (165 <  ch && ch < 256)
        );
    }
       
    static boolean isSentenceTerminator(char ch) {
        return (ch == '.' || ch == '?' || ch == '!'); 
    }
       
    /**
     * @return True if this single character marks an end of paragraph.
     */
    static boolean isParagraphTerminator(char chr) {
        return (12 <= chr && chr <= 14); 
    }
       
    public int parseParagraphString(String str)
    {
      int numWordsBefore = mWords.size();
      char chrPrv = ' ';  // space
      int kBeg = 0, kEnd = 0, strLen = str.length();
      for (int j = 0; j < strLen; j++) {
          char chr_j = str.charAt(j);
          
          // If current state is not-in-a-word, find a word-initiator to change it to in-a-word.
          if (chrPrv == ' ') {
              if (isWordInitiator(chr_j)) {
                  kBeg = j;
                  kEnd = j + 1;
                  chrPrv = chr_j;
              } else if (isSentenceTerminator(chr_j) || isParagraphTerminator(chr_j)) {
                  //add sentence
                  //a sentence is a starting index and a substring (end index == start + length) AND it can store its own ordinal position
                  if (isParagraphTerminator(chr_j)) {
                      //end paragraph
                      //change state to include not-in-a-paragraph
                  }
              }
          }
          else {                              // Current state is in-a-word ...
              if ((isWordInterior(chr_j))) {    // ... and we can extend the word          
                  kEnd++;
              }
              else {                            // ... no, we cannot extend it, so terminate it.
                  mWords.add(new Word(str.substring(kBeg, kEnd)));
                  chrPrv = ' ';                   // ... and change state to not-in-a-word
              }
          }
      }
      // If the input ended in non-whiteSpace, add this last word.
      if (chrPrv != ' ')
          mWords.add(new Word(str.substring(kBeg, kEnd)));
      return mWords.size() - numWordsBefore;
    }
    
    public static void main(String[] args) { TextAlignment.unit_test(); }

}
