package Compiler;


/**
 * @brief Logger
 *
 * This class outputs logs within a given level (Log.level = 'MYLEVEL')
 * Available levels:
 *   - NONE
 *   - ALL
 *   - ERROR
 *   - WARNING
 *   - NOTICE
 *
 */
public class Log {

    public static String level       = new String("ALL"); // NONE, ALL, ERROR, WARNING, NOTICE


    /**
     * Log fatal error (program will be killed)
     *
     * @param message
     * @param line
     */
    public static void fatal(String message, int line) {
        if(!level.equals("NONE")) {
            System.out.println((char) 27 + "[31m"
                    + "[ERROR] line " + line + ": "
                    + message
                    + (char) 27
                    + "[0m");
            System.exit(1);
        }
    }


    /**
     * Log warning
     *
     * @param message
     * @param line
     */
    public static void warning(String message, int line) {
        if(!level.equals("NONE") && !level.equals("ERROR")) {
            System.out.println("[WARNING] line " + line + ": " + message);
        }
    }


    /**
     * Log notice
     *
     * @param message
     * @param line
     */
    public static void notice(String message, int line) {
        if(!level.equals("NONE") && !level.equals("ERROR") && !level.equals("WARNING")) {
            System.out.println("[NOTICE] line " + line + ": " + message);
        }
    }

}
