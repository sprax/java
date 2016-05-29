package sprax.trees;

public interface BstInterface<Key extends Comparable<Key>, Val>
{
    public BstInterface<Key, Val> left();    
    public BstInterface<Key, Val> right();
    public Key key();
    public Val val();
}

