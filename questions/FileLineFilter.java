package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import sprax.files.FileUtil;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Filter a text file one line at a time.
 */
public class FileLineFilter
{
    final String inputFile;

    
    FileLineFilter(String inputFile, StringFilter stringFilter)
    {
        if (inputFile == null)
            throw new IllegalArgumentException("null");
        
        this.inputFile = inputFile;

        System.out.format("Reading inputFile: %s\n", inputFile);
        filterFile(inputFile, stringFilter);
    }
    
    
    public static int filterFile(String path, StringFilter stringFilter)
    {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while((line = br.readLine()) != null) {
                if (! stringFilter.filterString(line))
                    break;
            }
        } catch (IOException iox) {
            Sx.puts("Exception reading " + path + ":\n" + iox);
            return -1;
        } catch (Exception ex) {
            Sx.puts("Exception filtering " + path + ":\n" + ex);
            return -2;
        }
        return 0;
    }
    
    public static StringFilter useArgsOrDefaults(String[] args)
    {
        char letters[];
        String fileName, filePath;
        if (args.length > 0) {
            filePath = args[0];
        } else {
            fileName = "words.txt";
            filePath = FileUtil.getTextFilePath(fileName);            
        }
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int j = 1; j < args.length; j++)
                sb.append(args[j]);
            letters = sb.toString().toCharArray();
        } else {
            char defaultLets[] = { 'w', 'g', 'd', 'a', 's', 'x', 'z', 'c', 'y', 't', 'e', 'i', 'o', 'b' };
            letters = defaultLets;
        }
        
        int letterCounts[] = new int[26];
        int offset = 'a';
        for (char ch : letters) {
            letterCounts[ch - offset]++;
        }
        
        StringFilter stringFilter = new StringPrinter();
        int stat = FileLineFilter.filterFile(filePath, stringFilter);
        Sx.puts(stringFilter);
        
        return stringFilter;
    }    
    
    public static int unit_test()
    {
        String testName = FileLineFilter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        String emptyArgs[] = new String[0];
        StringFilter stringFilter = useArgsOrDefaults(emptyArgs);
        ArrayList<String> defaultAns = new ArrayList<>();           // FIXME
        
        String expectAns[] = { "azotised", "bawdiest", "dystocia", "geotaxis",
                "iceboats", "oxidates", "oxyacids", "sweatbox", "tideways" };
        
        if (defaultAns.size() == expectAns.length) {
            for (int j = 0; j < expectAns.length; j++) {
                numWrong += Sz.oneIfFalse(expectAns[j].equals(defaultAns.get(j)));
            }
        } else {
            numWrong = 100;
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
        //useArgsOrDefaults(args);
    }
    
}


class StringPrinter implements StringFilter
{
    @Override
    public boolean filterString(String string) {
        Sx.print(string);
        Sx.print(" ");
        return true;
    }
}