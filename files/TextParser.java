package sprax.files;

import sprax.sprout.Sx;

public class TextParser 
{
    int  mNumBooks       =  0;   // total number of words stored in the trie
    int  mNumChapters    =  0;   // total number of words stored in the trie
    int  mNumSections    =  0;   // total number of words stored in the trie
    int  mNumParagraphs  =  0;   // texts delimited by sections and/or indentations
    int  mNumSentences   =  0;   // texts delimited by periods, question marks, exclamation marks et al.
    int  mNumClauses     =  0;   // texts delimited by sentences or commas, m-dashes, etc.
    int  mNumPhrases     =  0;   // small word combos
    int  mNumWords       =  0;   // total number of words in the text

    protected String mTextFilePath  = null;  // Un-set at beginning of loading; set at end.
    
    TextParser(String textFilePath) {
        mTextFilePath = textFilePath;
    }

        
    public static int unit_test() 
    {
        String  testName = TextParser.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");    
        
        //final String textFilePath = "text/DonQ/_DonQuixote_EnGutNew_1.txt";
        final String textFilePath = FileUtil.getTextFilePath("mobydickChapter1.txt");

        
        TextParser tp = new TextParser(textFilePath);
        TextFileReader tfr = new TextFileReader(textFilePath);

        // TODO: use tp !
        Sx.puts("\n\t  readFileIntoStringCollector<StringBuffer>:");
        StringBufferStringCollector mySBSC = new StringBufferStringCollector();
        int numStrLines = tfr.readIntoStringCollector(mySBSC);
        if (numStrLines == 0) {
          System.out.println("<file empty>");
        } else {
          System.out.println("<found " + numStrLines + " lines>");
          System.out.println( mySBSC.mStringBuffer.toString() );
        }

        System.out.println("\n\t  readFileIntoStringBuffer:");
        StringBuffer contents = TextFileReader.readFileIntoStringBuffer(tp.mTextFilePath);
        if (contents == null || contents.length() == 0) {
          System.out.println("<file empty>");
        } else {
          System.out.println(contents.toString());
        }
        
        
   
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(); }
    
}
