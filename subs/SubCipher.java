package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class SubCipher 
{
    String cipherFileName;
    String corpusFileName;
    EnTextCounter cipherTextCounter;
    EnTextCounter corpusTextCounter;
    
    SubCipher() {
        this("cipher.txt", "corpus.txt");
    }
    
    SubCipher(String cipherFileName, String corpusFileName) 
    {
        this.cipherFileName = cipherFileName;
        this.corpusFileName = corpusFileName;
        cipherTextCounter = new EnTextCounter(cipherFileName);
        corpusTextCounter = new EnTextCounter(corpusFileName);
    }


    public static int unit_test(int level) 
    {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
    
        SubCipher mySC = new SubCipher();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }


     public static void main(String[] args) {
        unit_test(1);
    }

}
