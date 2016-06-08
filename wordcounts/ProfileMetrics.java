
package sprax.wordcounts;

import java.io.IOException;
import java.util.stream.Stream;


/**
 * TODO:
 * Remove access to hashed count implementations -- use get*Presence and get*Size instead, 
 *    and use get*Keys for profile-to-profile comparison.
 * TODO:   
 * Add storage estimates to showCounts
 */


/**
 * Computes a distance between two counter-based profiles using differences in word and/or word-tuple frequencies.
 * Single-word and word-tuple distances are computed separately and may be combined into a weighted sum.
 * For now, the weights are heuristic.
 */
public class ProfileMetrics
{
    // TODO: Devise a real basis for setting the weights (either empirical or theoretical).
    public final static double sWordsWeight =  1.0;
    public final static double sPairsWeight =  7.0;
    public final static double sTrebsWeight = 17.0;
    public final static double sWeightTotal = sWordsWeight + sPairsWeight + sTrebsWeight;

    /** distance from a "new" sentence stream to an existing profile, specialized for a hash counter */
    public static double sentencesToProfileDistance(Stream<String> sentences, HashCounterProfile profile)
    {
        CounterProfile<Integer> tempProfile = new HashCounterProfile(sentences);
        return partialProfileDistance(tempProfile, profile);
    }

    /** distance from a "new" sentence stream to an existing profile, specialized for a string counter */
    public static double sentencesToProfileDistance(Stream<String> sentences, StringCounterProfile profile)
    {
        CounterProfile<String> tempProfile = new StringCounterProfile(sentences);
        return partialProfileDistance(tempProfile, profile);
    }

    /** distance from a "new" sentence stream to an existing profile, specialized for a string counter */
    public static <K> double partialProfileDistance(CounterProfile<K> tempProfile, CounterProfile<K> profile)
    {
        double wordsDistance = partialDistance(tempProfile.getWordCounts(), profile.getWordCounts());
        double pairsDistance = partialDistance(tempProfile.getPairCounts(), profile.getPairCounts());
        double trebsDistance = partialDistance(tempProfile.getTrebCounts(), profile.getTrebCounts());
        return weightedDistanceSum(wordsDistance, pairsDistance, trebsDistance);
    }

    /** distance from a "new" sentence stream to an existing profile, specialized for bloom filter */
    public static double sentencesToProfileDistance(Stream<String> sentences, BloomFilterProfile profile) {
        return sentences.mapToDouble(profile::distanceFromWordsPairsAndTriads).average().getAsDouble();
    }

    public static <K> double counterProfileToProfileDistance(CounterProfile<K> profA, CounterProfile<K> profB)
    {
        double wordsDistance = wordFrequencyDistance(profA, profB);
        double pairsDistance = pairFrequencyDistance(profA, profB);
        double triadsDistance = triadFrequencyDistance(profA, profB);
        return weightedDistanceSum(wordsDistance, pairsDistance, triadsDistance);
    }

    protected static double weightedDistanceSum(double wordsDistance, double pairsDistance, double trebsDistance)
    {
        return (wordsDistance * sWordsWeight +
                pairsDistance * sPairsWeight +
                trebsDistance * sTrebsWeight ) / sWeightTotal;
    }

    public static <K> double counterToCounterDistance(TupleCounter<K> countsA, TupleCounter<K> countsB)
    {
        double partialDistanceAB = partialFrequencyDifference(countsA, countsB);
        double partialDistanceBA = partialFrequencyDifference(countsB, countsA);
        double distance = Math.sqrt(partialDistanceAB + partialDistanceBA);
        return distance;
    }

    /** Very simple measure, increased by anything found in countsA but not in countsB. */
    protected static <K> double partialDistance(TupleCounter<K> countsA, TupleCounter<K> countsB)
    {
        int diff = 0;
        for (K keyA : countsA.getKeys()) {
            int countA = countsA.getCount(keyA);
            if (countA > 0 && countsB.getCount(keyA) == 0) {
                diff += countA;
            }
        }
        return (double) diff / countsA.getSize();
    }


    /** Warning: This measure is not meaningful for small sample sizes. */
    protected static <K> double partialFrequencyDifference(TupleCounter<K> countsA, TupleCounter<K> countsB) 
    {
        double partial = 0.0, freqDiff;
        for (K keyA : countsA.getKeys()) {
            int countA = countsA.getCount(keyA);
            int countB = countsB.getCount(keyA);
            if (countB > 0) {
                freqDiff = 0.5 * ((double) countA / countsA.getSize() - (double) countB / countsB.getSize());
            } else {
                freqDiff = (double) countA / countsA.getSize();
            }
            partial += freqDiff * freqDiff;
        }
        return partial;
    }

    public static <K> double wordFrequencyDistance(CounterProfile<K> profA, CounterProfile<K> profB) 
    {
        return counterToCounterDistance(profA.getWordCounts(), profB.getWordCounts());
    }

    public static <K> double pairFrequencyDistance(CounterProfile<K> profA, CounterProfile<K> profB) 
    {
        return counterToCounterDistance(profA.getPairCounts(), profB.getPairCounts());
    }

    public static <K> double triadFrequencyDistance(CounterProfile<K> profA, CounterProfile<K> profB) 
    {
        return counterToCounterDistance(profA.getTrebCounts(), profB.getTrebCounts());
    }

    /** uses reflection and assumes a default constructor or init method */
	static class CounterProfileClassFactory
    {
        /** Makes any of subclass of WordTupleProfile */
        static <T extends WordTupleProfile> T makeProfile(Class<T> classT, String textFilePath) {
			return createAndInitialize(classT, textFilePath);
		}

		/** Makes any counter-based subclass of WordTupleProfile */
		static <K, T extends CounterProfile<K>> T makeCounterProfile(Class<T> classT, String textFilePath) {
			return createAndInitialize(classT, textFilePath);
		}

		/** Makes any of subclass of WordTupleProfile */
		private static <T extends WordTupleProfile> T createAndInitialize(
				Class<T> classT, String textFilePath) {
			try {
				T instance = classT.newInstance();
				instance.addCorpus(textFilePath);
				return instance;
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

    public static <K> void test_counterProfileDistance(CounterProfile<K> profA, CounterProfile<K> profB) {
        double wordsDistance = ProfileMetrics.wordFrequencyDistance(profA, profB);
        System.out.format("Words profile distance:  %f\n", wordsDistance);

        double pairsDistance = ProfileMetrics.pairFrequencyDistance(profA, profB);
        System.out.format("Pairs profile distance:  %f\n", pairsDistance);

        double triadsDistance = ProfileMetrics.triadFrequencyDistance(profA, profB);
        System.out.format("Triads profile distance: %f\n", triadsDistance);

        double weightedDistance = weightedDistanceSum(wordsDistance, pairsDistance, triadsDistance);
        System.out.format("Weighted total profile distance: %f from weights %f, %f, %f\n", weightedDistance,
                sWordsWeight, sPairsWeight, sTrebsWeight);
    }

    /** 
     * Counter type profiles allow comparing profile to profile, which depends on retrieving the keys for all tuple counts.
     */
    public static <K, T extends CounterProfile<K>> void test_counterProfileOnThreeBooks(Class<T> counterProfileClass) 
    {
        String  testName = "ProfileMetrics.test_counterProfileOnThreeBooks";
        System.out.println(testName + " BEGIN");

        String 
        textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        ////CounterProfile<K> mobyDickProfile = CounterProfileFactory.makeProfile(profileType, textFilePath);
        CounterProfile<K> mobyDickProfile = CounterProfileClassFactory.makeCounterProfile(counterProfileClass, textFilePath);
        mobyDickProfile.showCounts(mobyDickProfile.getClass().getSimpleName() + " from " + textFilePath);

        textFilePath = ProfileUtil.getTextFilePath("Iliad.txt");
        CounterProfile<K> iliadProfile = CounterProfileClassFactory.makeCounterProfile(counterProfileClass, textFilePath);
        iliadProfile.showCounts(iliadProfile.getClass().getSimpleName() + " from " + textFilePath);

        System.out.println("Distance between profiles made from Moby Dick and the Iliad:");
        test_counterProfileDistance(mobyDickProfile, iliadProfile);

        textFilePath = ProfileUtil.getTextFilePath("Melville_Pierre.txt");
        CounterProfile<K> pierreProfile = CounterProfileClassFactory.makeCounterProfile(counterProfileClass, textFilePath);
        pierreProfile.showCounts(pierreProfile.getClass().getSimpleName() + " from " + textFilePath);

        System.out.println("Distance between Moby Dick and Pierre, both by Herman Melville:");
        test_counterProfileDistance(mobyDickProfile, pierreProfile);

        double selfDistance = counterProfileToProfileDistance(mobyDickProfile, mobyDickProfile);
        System.out.println("Distance between the Moby Dick profile and itself should be zero: " + selfDistance);
        System.out.println();

        textFilePath = ProfileUtil.getTextFilePath("mobydickChapter1.txt");
        CounterProfile<K> chapterOneProfile = CounterProfileClassFactory.makeCounterProfile(counterProfileClass, textFilePath);
        chapterOneProfile.showCounts(chapterOneProfile.getClass().getSimpleName() + " from " + textFilePath);

        System.out.println("The full (symmetric) difference between part and whole is likely non-zero:");
        test_counterProfileDistance(chapterOneProfile, mobyDickProfile);
        test_counterProfileDistance(mobyDickProfile, chapterOneProfile);

        double partialDistance = partialDistance(chapterOneProfile.getWordCounts(), mobyDickProfile.getWordCounts());
        System.out.println("But the partial word distance from part to whole should be zero: " + partialDistance);

        double profileDistance = partialProfileDistance(chapterOneProfile, mobyDickProfile);
        System.out.println("and likewise the whole partial distance from part to whole: " + profileDistance);
        System.out.println();

        System.out.println("Finally, is Moby Dick chapter 1 closer to Pierre or the Iliad?");
        double chapOneToIliad = partialProfileDistance(chapterOneProfile, iliadProfile);
        System.out.println("    to the Iliad: " + chapOneToIliad);

        double chapOneToPierre = partialProfileDistance(chapterOneProfile, pierreProfile);
        System.out.println("    to Pierre:    " + chapOneToPierre);

        System.out.println(testName + " END");
    }    

    /** 
     * Filter type profiles don't store the keys, so it is less obvious how to compare such profiles
     * with each other.  One indirect way is to compare them using a set of sample keys supplied externally.
     * We might as well apply the filter profile to a stream of whole sentences, which will be parsed into
     * keys in the usual way.  Any distances obtained this way are only partial distances, not symmetric.
     */
    public static <K> void test_filterProfileOnThreeBooks() 
    {
        String  testName = "ProfileMetrics.test_filterProfileOnThreeBooks";
        System.out.println(testName + " BEGIN");

        String textFilePath;
        
        textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        BloomFilterProfile mobyDickFilter = new BloomFilterProfile(textFilePath);

        try {
            Stream<String> sentences = SentenceStream.sentencesFromFile(textFilePath);
            double distance = sentencesToProfileDistance(sentences, mobyDickFilter);
            System.out.format("Distance between Moby Dick stream and saved Moby Dick filter: %f\n\n", distance);
        } catch (IOException e) {
            System.out.println("Error: Failed to reload file into sentence stream: " + textFilePath);
        }

        textFilePath = ProfileUtil.getTextFilePath("Iliad.txt");
        BloomFilterProfile iliadFilter = new BloomFilterProfile(textFilePath);
        iliadFilter.showCounts(iliadFilter.getClass().getSimpleName() + " from " + textFilePath);
        try {
            Stream<String> sentences = SentenceStream.sentencesFromFile(textFilePath);
            double distance = sentencesToProfileDistance(sentences, mobyDickFilter);
            System.out.format("Distance between Moby Dick stream and saved Iliad filter: %f\n\n", distance);
        } catch (IOException e) {
            System.out.println("Error: Failed to reload file into sentence stream: " + textFilePath);
        }

        textFilePath = ProfileUtil.getTextFilePath("Melville_Pierre.txt");
        BloomFilterProfile pierreFilter = new BloomFilterProfile(textFilePath);
        pierreFilter.showCounts(iliadFilter.getClass().getSimpleName() + " from " + textFilePath);
        try {
            Stream<String> sentences = SentenceStream.sentencesFromFile(textFilePath);
            double distance = sentencesToProfileDistance(sentences, mobyDickFilter);
            System.out.format("Distance between Moby Dick stream and saved Pierre filter: %f\n\n", distance);
        } catch (IOException e) {
            System.out.println("Error: Failed to reload file into sentence stream: " + textFilePath);
        }

        System.out.println(testName + " END");
    }    

    public static void unit_test() 
    {
        String  testName = ProfileMetrics.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");


        test_counterProfileOnThreeBooks(StringCounterProfile.class);
        test_counterProfileOnThreeBooks(HashCounterProfile.class);

        test_filterProfileOnThreeBooks();

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }
}

/************************ Test Results 2016.04.15: ************************

sprax.wordcounts.ProfileMetrics.unit_test BEGIN
ProfileMetrics.test_counterProfileOnThreeBooks BEGIN
Loaded text/MobyDick.txt, length 1215248
StringCounterProfile from text/MobyDick.txt
    input sentences: 10322,  input words: 212418
    input words: 212418,  unique words: 19237,  pair keys: 109581,  triad keys: 171158
    thus word repeats = 193181,  pair repeats ~ 92515,  triad repeats ~ 20616

Loaded text/Iliad.txt, length 1201890
StringCounterProfile from text/Iliad.txt
    input sentences: 7783,  input words: 192248
    input words: 192248,  unique words: 13577,  pair keys: 101582,  triad keys: 157887
    thus word repeats = 178671,  pair repeats ~ 82883,  triad repeats ~ 18795

Distance between profiles made from Moby Dick and the Iliad:
Words profile distance:  0.414936
Pairs profile distance:  0.014398
Triads profile distance: 0.004494
Weighted total profile distance: 0.023685 from weights 1.000000, 7.000000, 17.000000
Loaded text/Pierre.txt, length 910011
StringCounterProfile from text/Pierre.txt
    input sentences: 6988,  input words: 152742
    input words: 152742,  unique words: 15790,  pair keys: 82387,  triad keys: 126238
    thus word repeats = 136952,  pair repeats ~ 63367,  triad repeats ~ 12528

Distance between Moby Dick and Pierre, both by Herman Melville:
Words profile distance:  0.228268
Pairs profile distance:  0.010690
Triads profile distance: 0.004289
Weighted total profile distance: 0.015041 from weights 1.000000, 7.000000, 17.000000
Distance between the Moby Dick profile and itself should be zero: 0.0

Loaded text/mobydickChapter1.txt, length 12218
StringCounterProfile from text/mobydickChapter1.txt
    input sentences: 103,  input words: 2196
    input words: 2196,  unique words: 850,  pair keys: 1823,  triad keys: 1944
    thus word repeats = 1346,  pair repeats ~ 270,  triad repeats ~ 46

The full (symmetric) difference between part and whole is likely non-zero:
Words profile distance:  0.596568
Pairs profile distance:  0.023121
Triads profile distance: 0.016852
Weighted total profile distance: 0.041796 from weights 1.000000, 7.000000, 17.000000
Words profile distance:  0.596568
Pairs profile distance:  0.023121
Triads profile distance: 0.016852
Weighted total profile distance: 0.041796 from weights 1.000000, 7.000000, 17.000000
But the partial word distance from part to whole should be zero: 0.0
and likewise the whole partial distance from part to whole: 0.0

Finally, is Moby Dick chapter 1 closer to Pierre or the Iliad?
    to the Iliad: 0.8612477381392952
    to Pierre:    0.8062564060480588
ProfileMetrics.test_counterProfileOnThreeBooks END
ProfileMetrics.test_counterProfileOnThreeBooks BEGIN
Loaded text/MobyDick.txt, length 1215248
HashCounterProfile from text/MobyDick.txt
    input sentences: 10322,  input words: 212418
    input words: 212418,  unique words: 19237,  pair keys: 109273,  triad keys: 171078
    thus word repeats = 193181,  pair repeats ~ 92823,  triad repeats ~ 20696

Loaded text/Iliad.txt, length 1201890
HashCounterProfile from text/Iliad.txt
    input sentences: 7783,  input words: 192248
    input words: 192248,  unique words: 13577,  pair keys: 101389,  triad keys: 157851
    thus word repeats = 178671,  pair repeats ~ 83076,  triad repeats ~ 18831

Distance between profiles made from Moby Dick and the Iliad:
Words profile distance:  0.414936
Pairs profile distance:  0.014436
Triads profile distance: 0.004495
Weighted total profile distance: 0.023696 from weights 1.000000, 7.000000, 17.000000
Loaded text/Pierre.txt, length 910011
HashCounterProfile from text/Pierre.txt
    input sentences: 6988,  input words: 152742
    input words: 152742,  unique words: 15790,  pair keys: 82212,  triad keys: 126203
    thus word repeats = 136952,  pair repeats ~ 63542,  triad repeats ~ 12563

Distance between Moby Dick and Pierre, both by Herman Melville:
Words profile distance:  0.228268
Pairs profile distance:  0.010718
Triads profile distance: 0.004290
Weighted total profile distance: 0.015049 from weights 1.000000, 7.000000, 17.000000
Distance between the Moby Dick profile and itself should be zero: 0.0

Loaded text/mobydickChapter1.txt, length 12218
HashCounterProfile from text/mobydickChapter1.txt
    input sentences: 103,  input words: 2196
    input words: 2196,  unique words: 850,  pair keys: 1822,  triad keys: 1944
    thus word repeats = 1346,  pair repeats ~ 271,  triad repeats ~ 46

The full (symmetric) difference between part and whole is likely non-zero:
Words profile distance:  0.596568
Pairs profile distance:  0.023173
Triads profile distance: 0.016852
Weighted total profile distance: 0.041810 from weights 1.000000, 7.000000, 17.000000
Words profile distance:  0.596568
Pairs profile distance:  0.023173
Triads profile distance: 0.016852
Weighted total profile distance: 0.041810 from weights 1.000000, 7.000000, 17.000000
But the partial word distance from part to whole should be zero: 0.0
and likewise the whole partial distance from part to whole: 0.0

Finally, is Moby Dick chapter 1 closer to Pierre or the Iliad?
    to the Iliad: 0.8602777925784563
    to Pierre:    0.8044905279643806
ProfileMetrics.test_counterProfileOnThreeBooks END
ProfileMetrics.test_filterProfileOnThreeBooks BEGIN
Loaded text/MobyDick.txt, length 1215248
HashCounterProfile from text/MobyDick.txt
    input sentences: 10322,  input words: 212418
    input words: 212418,  unique words: 19237,  pair keys: 109273,  triad keys: 171078
    thus word repeats = 193181,  pair repeats ~ 92823,  triad repeats ~ 20696

Loaded text/MobyDick.txt, length 1215248
Loaded text/MobyDick.txt, length 1215248
Distance between Moby Dick stream and saved Moby Dick filter: 0.000000

Loaded text/Iliad.txt, length 1201890
BloomFilterProfile from text/Iliad.txt
    input sentences: 7783,  input words: 192248
    minimal unique word keys: 13577,  pair keys: 100911,  and triad keys: 153120

Loaded text/Iliad.txt, length 1201890
Distance between Moby Dick stream and saved Iliad filter: 0.332820

Loaded text/Pierre.txt, length 910011
BloomFilterProfile from text/Pierre.txt
    input sentences: 6988,  input words: 152742
    minimal unique word keys: 15790,  pair keys: 82138,  and triad keys: 124458

Loaded text/Pierre.txt, length 910011
Distance between Moby Dick stream and saved Pierre filter: 0.260376

ProfileMetrics.test_filterProfileOnThreeBooks END
sprax.wordcounts.ProfileMetrics.unit_test END

 ****************************************************************/
