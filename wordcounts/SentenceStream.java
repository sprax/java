
package sprax.wordcounts;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Cribbed from Holger's answer in:
 * http://stackoverflow.com/questions/31148693/java-8-stream-of-sentences
 */
public class SentenceStream extends Spliterators.AbstractSpliterator<String> implements Consumer<CharSequence> {

    public static Stream<String> sentences(Stream<? extends CharSequence> s) {
        return StreamSupport.stream(new SentenceStream(s.spliterator()), false);
    }

    public static Stream<String> sentencesFromFile(final String textFilePath) throws IOException {
        CharSequence seq = FileToCharSequence.loadFile(textFilePath);
        System.out.format("Loaded %s, length %d\n", textFilePath, seq.length());
        return SentenceStream.sentences(Stream.of(seq));
    }

    Spliterator<? extends CharSequence> source;
    CharBuffer buffer;
    BreakIterator iterator;

    public SentenceStream(Spliterator<? extends CharSequence> source) {
        super(Long.MAX_VALUE, ORDERED|NONNULL);
        this.source = source;
        iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        buffer = CharBuffer.allocate(512);
        buffer.flip();
    }

    @Override
    public boolean tryAdvance(Consumer<? super String> action) {
        for(;;) {
            int next = iterator.next();
            if(next != BreakIterator.DONE && next != buffer.limit()) {
                action.accept(buffer.subSequence(0, next-buffer.position()).toString());
                buffer.position(next);
                return true;
            }
            if(! source.tryAdvance(this)) {
                if(buffer.hasRemaining()) {
                    action.accept(buffer.toString());
                    buffer.position(0).limit(0);
                    return true;
                }
                return false;
            }
            iterator.setText(buffer.toString());
        }
    }

    @Override
    public void accept(CharSequence t) {
        buffer.compact();
        if(buffer.remaining() < t.length()) {
            CharBuffer bigger = CharBuffer.allocate(
                    Math.max(buffer.capacity()*2, buffer.position() + t.length()));
            buffer.flip();
            bigger.put(buffer);
            buffer = bigger;
        }
        buffer.append(t).flip();
    }


    public static int unit_test() 
    {
        String  testName = SentenceStream.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        //final String textFilePath = getTextFilePath("DonQuixote_EnGutenberg.txt";
        final String textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        try {
            Stream<String> sentences = sentencesFromFile(textFilePath);
            sentences.forEach(System.out::println);
        } catch (IOException e) {
            System.out.format("Exception loading %s\n", textFilePath);
            e.printStackTrace();
        }

        System.out.println(testName + " END");
        return 0;
    }

    public static void main(String[] args) {
        unit_test();
    }
}