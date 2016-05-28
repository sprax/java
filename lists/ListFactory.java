package sprax.lists;

public class ListFactory<T extends SinList>
{
    
    public static SinList fromArray(final int[] array) {
        return SinList.fromArray(array);
    }
    
    public static SinList fromArray(final char[] array) {
        return SinList.fromArray(array);
    }
    
    public static SinList fromArray(final Character[] array) {
        return SinList.fromArray(array);
    }
    
    public static SinList fromString(final String string) {
        return SinList.fromString(string);
    }
    
    public SinList fromStringA(final String string) {
        return SinList.fromString(string);
    }
    
    public static void main(String[] args) {
        System.out.println("Alphabet to 5");
        SinList list = new SinList(SinLink.initAlphabetAppend(5));
        SinList.print(list);
    }
    
}
