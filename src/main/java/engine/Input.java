package engine;

import org.joml.Vector2f;
import rendering.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Input
{
    private static long inputSource;

    private static final int KEY_FIRST = GLFW_KEY_SPACE;
    private static final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private static final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static final Vector2f deltaMousePosition = new Vector2f();
    private static final Vector2f prevMousePosition = new Vector2f();
    private static float lastUpdateTime = Time.currentTime();

    public static boolean isKeyDown(int keyCode)
    {
        return glfwGetKey(inputSource, keyCode) == 1;
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return isKeyDown(keyCode) && !keys[keyCode];
    }

    public static boolean isKeyReleased(int keyCode)
    {
        return !isKeyDown(keyCode) && keys[keyCode];
    }

    public static boolean isMouseDown(int button)
    {
        return glfwGetMouseButton(inputSource, button) == 1;
    }

    public static boolean isMousePressed(int button)
    {
        return isMouseDown(button) && !mouseButtons[button];
    }

    public static boolean isMouseReleased(int button)
    {
        return !isMouseDown(button) && mouseButtons[button];
    }

    public static Vector2f getMousePosition()
    {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        glfwGetCursorPos(inputSource, xPos, yPos);
        return new Vector2f((float) xPos[0], (float) yPos[0]);
    }

    public static Vector2f getDeltaMousePosition()
    {
        return deltaMousePosition;
    }

    public static void update(Window window)
    {
        inputSource = window.getWindowId();

        for (int i = KEY_FIRST; i < GLFW_KEY_LAST; i++) {
            keys[i] = isKeyDown(i);
        }

        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            mouseButtons[i] = isMouseDown(i);
        }

        glfwPollEvents();

        if (Time.currentTime() - lastUpdateTime >= 1.0f) {
            Vector2f currentPos = getMousePosition();
            deltaMousePosition.set(new Vector2f(currentPos).sub(prevMousePosition));
            prevMousePosition.set(currentPos);
            lastUpdateTime = Time.currentTime();
        }

    }
}
