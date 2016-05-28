package sprax.lists;

public abstract class Link implements LinkInterface
{
    @Override
    public abstract Link getNext();
    
    public abstract void setNext(Link link);
    
    public abstract int getData();
}
