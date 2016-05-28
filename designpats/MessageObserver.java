package sprax.designpats;

import java.util.Observable;
import java.util.Observer;

import sprax.Sx;
import sprax.Sz;

/** Observer/Subscriber/Consumer */
public class MessageObserver implements Observer
{
    String mName;
    MessageObserver(String myName) { mName = myName; }
    
    @Override
    public void update(Observable subject, Object msg) 
    {
        String  methodName = MessageObserver.class.getName() + ".update";
        Sx.puts(methodName + " BEGIN");
        boolean changed = subject.hasChanged();
        Sx.puts(4, mName + " got updated msg(" + msg + ")  [hasChanged: " + changed + "]");
        Sx.puts(methodName + " END");
    }
       
    public static int unit_test()
    {
        String testName = MessageObserver.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        MessageBoard  subject   = new MessageBoard("first msg");
        Observer observerA = new MessageObserver("obsA");
        Observer observerB = new MessageObserver("obsB");
        subject.addObserver(observerB);
        subject.addObserver(observerA);
        subject.setMessage("newish msg");
        subject.setMessage("newest msg");
        
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}


/** Subject/Observable/Publisher/Producer */
class MessageBoard extends Observable
{
    private String mMsg;
    
    public MessageBoard(String msg)  { mMsg = msg; }
    public String getMessage()  { return mMsg; }
    public void setMessage(String msg) {
        String  methodName = MessageBoard.class.getName() + ".setMessage";
        Sx.puts(methodName + " BEGIN");
        
        boolean changed;
        if ( ! mMsg.equals(msg)) {
            mMsg = msg;
            changed = hasChanged();
            setChanged();
            changed = hasChanged();
            Sx.puts("hasChanged: " + changed);
            notifyObservers(msg);
        }
        changed = hasChanged();
        Sx.puts("hasChanged: " + changed);
        Sx.puts(methodName + " END");
    }
}