package sprax.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sprax.Sx;
import sprax.Sz;
import sprax.bits.BitsAlgo;

public class MadeImmutable 
{	
    private final String name;
    private final List<String> accounts;
    
    public MadeImmutable(String name, List<String> accounts) 
    {
        this.name = name;
        this.accounts = new ArrayList<String>(accounts);	// defensive copy
    }
    
    public String getName() {
        return name;
    }
    
    //public void setName(String name) {
    //	this.name = name;
    //}
    
    public List<String> getAccounts() {
        // return an unmodifiable list view of the list:
        return Collections.unmodifiableList(accounts);
    }
    
    ///public void setAccounts(List<String> accounts) {
    ///	this.accounts = accounts;
    ///}
    
    
    public static int unit_test() 
    {
        String testName =  MadeImmutable.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        String myName = "Chris";
        List<String> myAccounts = new ArrayList<String>();
        myAccounts.add("Cornell");
        myAccounts.add("Soundgarden");
        MadeImmutable cc = new MadeImmutable(myName, myAccounts);
        
        String gotName = cc.getName();
        gotName = "Kim";
        Sx.puts("gotName is changed to: " + gotName + ", but getName still returns: " + cc.getName());
        
        List<String> gotAccounts = cc.getAccounts();
        try {
            gotAccounts.add("Temple");
        } catch (Exception ex) {
            Sx.puts("Adding to the Collections.unmodifiableList gotAccounts fails: " + ex);
        }
        
        myAccounts.add("Dog");
        List<String> listAgain = cc.getAccounts();
        Sx.putsList("listAgain:", listAgain);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}

/*
class BrokenMadeImmutable extends MadeImmutable
{
	public BrokenMadeImmutable(String name, List<String> accounts) {
		super(name, accounts);
	}
}
 */