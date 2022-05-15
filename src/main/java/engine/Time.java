package engine;

import org.lwjgl.glfw.GLFW;

public class Time
{
    private static float prevFrameTime;
    private static float prevFPSTime;
    private static int FPSFrameCount;
    private static float deltaTime;

    public static void update()
    {
        if (currentTime() - prevFPSTime >= 5.0) {
            System.out.println((FPSFrameCount / 5.0) + " fps");
            FPSFrameCount = 0;
            prevFPSTime = currentTime();
        }

        FPSFrameCount++;

        deltaTime = (currentTime() - prevFrameTime);
        prevFrameTime = currentTime();
    }

    public static float getDeltaTime()
    {
        return deltaTime;
    }

    public static float currentTime()
    {
        // returns time in seconds
        return (float) GLFW.glfwGetTime();
    }
}
