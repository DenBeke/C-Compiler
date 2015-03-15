package Compiler;

/**
 * Assert wrapper
 */
public class Assert {

    public static void Assert(boolean a) {

        if(!a) {
            System.out.println("[ERROR] Assertion failed: " + Thread.currentThread().getStackTrace()[2] );
            System.exit(1);
        }

    }
}
