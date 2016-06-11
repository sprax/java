package sprax.sprout;

public class Spaces
{   
    private static String mString =
        "                                                                "; // 64 spaces
    //   1234567890123456789012345678901234567890123456789012345678901234
    public static String get(int len)
    {
        if (len <= 0) {
            return "";
        }
        for (;;) {
            if (mString.length() >= len) {
                return mString.substring(0, len);
            }
            mString += mString;
        }
    }
    
    public static void put(int len)
    {
        if (len > 0) {
            Sx.print(get(len));
        }
    }
    
    public static int unit_test()
    {
        String testName = Spaces.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN\n");
        
        System.out.format("Spaces:%s%d  %d\n", mString, mString.length(), mString.length());
        int len = 24;
        System.out.format("Spaces:%s%d  %d\n", get(len), len, mString.length());
        len *= 2;
        System.out.format("Spaces:%s%d  %d\n", get(len), len, mString.length());
        len *= 2;
        System.out.format("Spaces:%s%d  %d\n", get(len), len, mString.length());
        
        Sx.puts(testName + " END,  status: PASSED");
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
