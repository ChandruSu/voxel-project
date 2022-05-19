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
        if (currentTime() - prevFPSTime >= 1.0f) {
            System.out.println(FPSFrameCount + " fps");
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
