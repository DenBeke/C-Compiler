package Compiler;

/**
 * Assert wrapper
 */
public class Assert {

    public static void Assert(boolean a) {

        if(!a) {
            System.out.println("Assertion failed!");
            System.out.println( Thread.currentThread().getStackTrace()[2] );
            System.exit(1);
        }

    }
}
