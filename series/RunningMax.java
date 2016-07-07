package sprax.series;

public interface RunningMax 
{
    public int addNumAndReturnMax(int num);
    public int getSize();
    public int getMax();
    public Object[] getWindow();
}
