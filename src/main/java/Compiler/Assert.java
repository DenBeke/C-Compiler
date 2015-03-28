package Compiler;

/**
 * @brief Assert wrapper
 */
public class Assert {


    /**
     * Assert
     *
     * @param a: condition
     */
	public static void Assert(boolean a) {
		if(!a) {
            Log.fatal("[ERROR] Assertion failed: " + Thread.currentThread().getStackTrace()[2], Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
	}


    /**
     * Assert
     *
     * @param a:   condition
     * @param msg: message
     */
    public static void Assert(boolean a, String msg) {
        if(!a) {
            Log.fatal("[ERROR] Assertion failed '" + msg + "': " + Thread.currentThread().getStackTrace()[2], Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

}
