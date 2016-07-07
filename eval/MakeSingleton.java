package sprax.eval;

public class MakeSingleton 
{
    private String message;
    
    public MakeSingleton() {
        this.message = "Hello world!";
    }
    
    public void saySomething() {
        System.out.println(message);
    }
    
}
