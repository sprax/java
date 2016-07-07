package sprax.eval;

public class UpperCase implements EvaluateInterface<String,String>
{
    public String evaluate(String value)
    {
        return value.toUpperCase();
    }
}
