package engine;

public class Debug
{
    private static final String ERROR_PREFIX = "[error]";

    public static void error(String message)
    {
        System.err.printf("%s %s\n", ERROR_PREFIX, message);
    }

    public static void failure(String message)
    {
        error(message);
        System.exit(0);
    }
}
