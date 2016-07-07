package sprax.eval;

import java.util.List;

/** Exercise: make an immutable version of this class, named MadeImmutable */
public class MakeImmutable 
{	
    private String name;
    private List<String> accounts;
    
    public MakeImmutable(String name, List<String> accounts) {
        this.name = name;
        this.accounts = accounts;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }
}
