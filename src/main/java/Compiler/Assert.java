package Compiler;

/**
 * Assert wrapper
 */
public class Assert {

	public static void Assert(boolean a) {

		if(!a) {
			// System.out.println("[ERROR] Assertion failed: " +
			// Thread.currentThread().getStackTrace()[2] );
			System.out.println((char) 27 + "[31m"
					+ "[ERROR] Assertion failed: "
					+ Thread.currentThread().getStackTrace()[2] + (char) 27
					+ "[0m");
			System.exit(1);
		}
	}
}
