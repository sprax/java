package sprax.strings;

import java.util.HashMap;

import sprax.Sx;

/**
 * Are stringA and stringB anagrams of each other?  That is, are they
 * composed of the same letters and letter counts as each other,
 * only permuted?  
 */
public class Anagrams 
{
    public static boolean anagramsOneMap(String stringA, String stringB)
    {
        if (stringA == null || stringB == null || stringA.length() != stringB.length())
            return false;
        HashMap<Character, Short> mapA = new HashMap<Character, Short>();
        for (char charA : stringA.toCharArray()) {
            Short freqA = mapA.get(charA);
            if (freqA == null)
                freqA = 0;
            mapA.put(charA, ++freqA);
        }
        for (char charB : stringB.toCharArray()) {
            Short freqB = mapA.get(charB);
            if (freqB == null || --freqB < 0)
                return false;
            mapA.put(charB, freqB);
        }
        for (Short sh : mapA.values())
            if (sh != 0)
                return false;
        return true;
    }
    
    /**
     * anagramsMap(stringA, stringB)
     * Are stringA and stringB anagrams of each other?  That is, are they
     * composed of the same letters and letter counts as each other,
     * only permuted?  In this restrictive version, nothing is ignored:
     * not whitespace, capitalization, or non-letter characters such 
     * as punctuation.  The input strings need not be composed of words
     * or syllables or even be pronounceable.  
     * 
     * Implemented using HashMap<Character, Short>
     * 
     * @param stringA
     * @param stringB
     * @return true if the two strings are strict anagrams of each other.
     */
    public static boolean anagramsTwoMaps(String stringA, String stringB)
    {
        if (stringA == null || stringB == null || stringA.length() != stringB.length())
            return false;
        HashMap<Character, Short> mapA = new HashMap<Character, Short>();
        HashMap<Character, Short> mapB = new HashMap<Character, Short>();
        for (char charA : stringA.toCharArray()) {
            Short freqA = mapA.get(charA);
            if (freqA == null)
                freqA = 0;
            mapA.put(charA, ++freqA);
        }
        for (char charB : stringB.toCharArray()) {
            Short freqB = mapB.get(charB);
            if (freqB == null)
                freqB = 0;
            mapB.put(charB, ++freqB);
            Short freqA = mapA.get(charB);
            if (freqA < freqB)  // this char appears in stringB more times than in stringA
                return false;
        }
        // Now we know that every character in stringB is also in stringA,
        // and occurs in stringA at least as many times as it does in stringB.
        // Do we also need to check if every letter in stringA also appear the 
        // same number of times in stringB?
        // No, because we already checked that they have the same length.
        // We could check this explicitly, or, just compare key set lengths
        // as strings.  So we don't even need to check the key set sizes.
        if (mapA.size() != mapB.size())
            throw new IllegalStateException("different key set sizes"); 
        
        //        for (char c : mapA.keySet()) {
        //            if (mapA.get(c) != mapB.get(c))
        //                return false;
        //        }
        return true;
    }
    
    
    
    /**
     * anagramsArray(stringA, stringB)
     * Are stringA and stringB anagrams of each other?  That is, are they
     * composed of the same letters and letter counts as each other,
     * only permuted?  In this permissive version, everything is ignored
     * except the letters : whitespace, capitalization, or non-letter 
     * characters such as punctuation are all filtered out.  
     * The input strings need not be composed of words or syllables 
     * or even be pronounceable.  
     * 
     * Implemented using just an array.
     * 
     * @param stringA
     * @param stringB
     * @return 0, if stringB is an anagram of stringA; otherwise, the 
     * ordinal position of the first letter in stringB (not counting 
     * extraneous characters) that violates anagrammicity.  A return value
     * of -1 indicates that at least one of the input strings is null or empty.
     */
    public static int anagramsArray(String stringA, String stringB)
    {
      if (stringA == null || stringB == null)
        return -1;
      
      short countA[] = new short[26];
      short countB[] = new short[26];
      for (char charA : stringA.trim().toUpperCase().toCharArray()) {
        charA -= 'A';
        if (0 <= charA && charA < 26)
          countA[charA]++;
      }
      char charrayB[] = stringB.trim().toUpperCase().toCharArray();
      for (int j = 0; j < charrayB.length; j++) {
        char charB = charrayB[j];
        charB -= 'A';
        if (0 <= charB && charB < 26) {
          countB[charB]++;
          if (countA[charB] < countB[charB])
            return j+1;
        }
      }
      // By now we know every letter in stringB also appears at least as many
      // times in stringA.
      // But does every letter in stringA also appear the same number
      // of times in stringB?  We don't find this out until after we have
      // counted all the letters in stringB, that is, at position == stringB.length.
      for (int j = 0; j < 26; j++) {
        if (countA[j] != countB[j])
          return charrayB.length;
      }
      return 0;
    }
    
    public static int wrong(boolean result, boolean expected) 
    {
        return result == expected ? 0 : 1;
    }
    
    public static int unit_test(int lvl) 
    {
        int numWrong = 0;
        String shortName = Anagrams.class.getSimpleName();
        String  testName = Anagrams.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        
        String stringA = "A collection of Sherlock Holmes detective stories.";
        String stringB = "Coveted crime classics tell of hooknose title-hero.";
        String stringC = "Heroic slack scion tells of vetoed chrome title: CEO!"; // extra C instead of S
        String stringD = "I am not worthy!";
        String stringE = "What moronity!";
        
        String stringF = "aaaa";
        String stringG = "aaa";
        System.out.format("%s: Is \"%s\" a strict anagram for \"%s\"?\n", shortName, stringF, stringG);
        boolean isAnagram = anagramsTwoMaps(stringF, stringG);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No." );
        if (isAnagram != anagramsOneMap(stringF, stringG))
            System.out.println("anagramsOneMap disagrees with anagramsTwoMaps!");
        
        stringF = "aabbbb";
        stringG = "aaabbb";
        System.out.format("%s: Is \"%s\" a strict anagram for \"%s\"?\n", shortName, stringF, stringG);
        isAnagram = anagramsTwoMaps(stringF, stringG);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No." );
        if (isAnagram != anagramsOneMap(stringF, stringG))
            System.out.println("anagramsOneMap disagrees with anagramsTwoMaps!");
        
        System.out.format("%s: Is \"%s\" a strict anagram for \"%s\"?\n", shortName, stringE, stringD);
        isAnagram = anagramsTwoMaps(stringA, stringB);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No." );
        
        System.out.format("%s: Is \"%s\" a loose anagram for \"%s\"?\n", shortName, stringE, stringD);
        int diffPos = anagramsArray(stringA, stringB);
        isAnagram = (diffPos == 0);
        System.out.println( isAnagram ? "Yes." : "No, look at the letter at position " + diffPos );
        numWrong += wrong(isAnagram, true);
        
        System.out.format("%s: Is \"%s\" a strict anagram for \"%s\"?\n", shortName, stringB, stringA);
        isAnagram = anagramsTwoMaps(stringA, stringB);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No." );
        
        System.out.format("%s: Is \"%s\" a loose anagram for \"%s\"?\n", shortName, stringB, stringA);
        diffPos = anagramsArray(stringA, stringB);
        isAnagram = (diffPos == 0);
        numWrong += wrong(isAnagram, true);
        System.out.println( isAnagram ? "Yes." : "No, look at the letter at position " + diffPos );
        
        System.out.format("%s: Is \"%s\" a loose anagram for \"%s\"?\n", shortName, stringC, stringA);
        diffPos = anagramsArray(stringA, stringC);
        isAnagram = (diffPos == 0);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No, look at the letter at position " + diffPos );
        
        Sx.format("\nEND %s:  %s\n", testName, (numWrong == 0 ? "PASS" : "FAIL"));    
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
