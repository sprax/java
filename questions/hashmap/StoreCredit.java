package sprax.questions.hashmap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StoreCredit
{
    StoreCredit(String inputFile) throws IOException
    {
        System.out.format("Reading inputFile: %s\n", inputFile);
        ArrayList<String> input = readFileIntoList(inputFile);
        int numCases = Integer.parseUnsignedInt(input.get(0));
        for (int j = 0; j < numCases; j++) {
            int credit = Integer.parseUnsignedInt(input.get(1 + j*3));
            int nItems = Integer.parseUnsignedInt(input.get(2 + j*3));
            int prices[] = spaceSeparatedNumbers(input.get(3 + j*3));
            assert(nItems == prices.length);
            solveCase(j + 1, credit, prices);
        }
    }
    
    void solveCase(int caseNum, int credit, int prices[])
    {
        HashMap<Integer, Integer> priceToIndex = new HashMap<>();
        for (int j = 0; j < prices.length; j++) {
            int cmp = credit - prices[j];
            if (priceToIndex.containsKey(cmp)) {
                // Solution format uses 1-based indexing:
                int indexA = 1 + priceToIndex.get(cmp);
                int indexB = 1 + j;
                System.out.format("Case #%d: %d %d\n", caseNum, indexA, indexB);
                return;
            } else {
                priceToIndex.put(prices[j], j);
            }
        }
        System.out.format("Case #%d: Found no solution!\n", caseNum);        
    }
    
    static int[] spaceSeparatedNumbers(String numbers) {
        String numStrs[] = numbers.split(" ");
        int numInts[] = new int[numStrs.length];
        int j = 0;
        for (String str : numStrs) {
            numInts[j++] = Integer.parseInt(str);
        }
        return numInts;
    }
    
    static ArrayList<String> readFileIntoList(String path) throws IOException
    {
        String line;
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    
    public static void main(String[] args) throws IOException
    {
        String fileSpec = "src/sprax/questions/hashmap/StoreCreditSmall.txt";
        if (args.length > 0)
            fileSpec = args[0];
        StoreCredit sc = new StoreCredit(fileSpec);
    }
    
}
