package sprax.eval;

public class HashCode implements EvaluateInterface<Integer,Object>
{	
    @Override
    public Integer evaluate(Object value) {
        return Integer.valueOf(value.hashCode());
    }
}
