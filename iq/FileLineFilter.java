package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    
    
    public static int unit_test()
    {
        String testName = FileLineFilter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
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