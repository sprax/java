package sprax.singleton;

import java.lang.reflect.Constructor;

public class ReflectionKilledTheSingleton 
{ 
    public static void test_singletonKiller(Object instanceOne) 
    {
        Object instanceTwo = null;
        try {
            Constructor<?>[] constructors = instanceOne.getClass().getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                // The code below will destroy the singleton pattern
                constructor.setAccessible(true);
                try {
                    instanceTwo = constructor.newInstance(instanceOne);
                } catch (IllegalArgumentException iae) {
                    try {
                        instanceTwo = constructor.newInstance();
                    } catch (Exception ex) {
                        System.out.println("Second call of newInstance also fails: " + ex);
                    }
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String simpleName = instanceOne.getClass().getSimpleName();
        System.out.println("Can reflection kill this singleton?  " + instanceOne.getClass().getName());
        System.out.println("instanceOne className/hashCode: " + simpleName + "/" + instanceOne.hashCode());
        if (instanceTwo != null)
        {
            System.out.println("instanceTwo className/hashCode: " + simpleName + "/" + instanceTwo.hashCode());
            System.out.println("Yes; a second instance of " + simpleName + " was created via reflection.");
        }
        else
        {
            System.out.println("No; instanceTwo of " + simpleName + " is null.");
        }
        System.out.println();
    }

    public static void unit_test() 
    {
        test_singletonKiller(BillPughSingleton.getInstance());
        test_singletonKiller(DoubleCheckSingleton.getInstance());
        test_singletonKiller(EnumSingleton.getInstance());
    }

    public static void main(String[] args) 
    { unit_test(); }
}