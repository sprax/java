package sprax.eval;

public class Round implements EvaluateInterface<Integer,Float>
{	
    public Integer evaluate(Float value) 
    {
        return Integer.valueOf(Math.round(value));
    }    
}
