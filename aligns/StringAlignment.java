package sprax.aligns;

@Deprecated
public class StringAlignment extends SequenceAlignment<Character>
{
    final String mStrA;
    final String mStrB;

    StringAlignment(String sa, String sb)
    {
        super(createCharacterArrayList(sa), createCharacterArrayList(sb));
        mStrA = sa;
        mStrB = sb;
    }

    //  char methods not so useful
    //    static Character[] createCharacterArray(char chA[]) {
    //        Character[] cA = new Character[chA.length];
    //        for (int j = 0; j < chA.length; j++) {
    //            cA[j] = chA[j];
    //        }
    //        return cA;
    //    }
    //    StringAlignment(Character[] sa, Character[] sb)
    //    {
    //        super(sa, sb);
    //        mStrA = new String();
    //    }
    //    StringAlignment(char[] sa, char[] sb)
    //    {
    //        super(createCharacterArray(sa), createCharacterArray(sb));
    //        mStrA = new String(sa);
    //        mStrB = new String(sb);
    //    }
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test(int level)
    {
        StringAlignment sa = new StringAlignment("ABCD", "ABCD");
        sa.test_alignment(level);
        sa = new StringAlignment("ABCD ACEF", "CDBAC");
        return sa.test_alignment(level);
    }
    
    public static void main(String[] args) {  unit_test(2); }
}

