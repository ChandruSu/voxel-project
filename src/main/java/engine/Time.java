package engine;

public class Time
{
    public static float currentTime()
    {
        return (float) System.nanoTime() / 1e+6f;
    }
}
