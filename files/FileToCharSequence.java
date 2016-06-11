package sprax.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import sprax.sprout.Sx;

public class FileToCharSequence {
    
    // Converts the contents of a file into a read-only CharSequence.
    public static CharSequence loadFile(String filename) throws IOException {
        try (FileInputStream input = new FileInputStream(filename)) {
            FileChannel channel = input.getChannel();

            // Create a read-only CharBuffer on the file
            ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
            CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
            return cbuf;
        }
    }

    
    public static int unit_test() 
    {
        String  testName = FileToCharSequence.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");    
        
        final String textFilePath = FileUtil.getTextFilePath("mobydickChapter1.txt");
        
        try {
            CharSequence seq = loadFile(textFilePath);
            System.out.format("Loaded %s, length %d, contents:\n\n", textFilePath, seq.length());
            System.out.println(seq.toString());
        } catch (IOException e) {
            System.out.format("Exception loading %s\n", textFilePath);
            e.printStackTrace();
        }

        Sx.puts(testName + " END");
        return 0;
    }
    

    public static void main(String[] args) {
        unit_test();
   }

}
