package rendering;

import engine.Debug;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.glfw.GLFW.*;

public class Window
{
    private final long windowId;
    private int width;
    private int height;

    public Window(String winTitle, int width, int height)
    {
        this.width = width;
        this.height = height;

        if (!glfwInit()) {
            Debug.failure("Failed to initialize GLFW!");
        }

        // sets debug messenger callback
        glfwSetErrorCallback((err, msg) -> Debug.error(String.format("[GLFW] %s", GLFWErrorCallback.getDescription(msg))));

        // window preferences
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_RESIZABLE);

        // creates window
        windowId = glfwCreateWindow(width, height, winTitle, 0, 0);
        if (windowId == 0) {
            Debug.failure("Failed to create window!");
        }

        glfwMakeContextCurrent(windowId);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // centres window on screen
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            glfwSetWindowPos(windowId, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        glfwSetWindowSizeCallback(windowId, (window, w, h) -> {
            glViewport(0, 0, w, h);
        });

        glfwShowWindow(windowId);
    }

    public boolean isOpen()
    {
        return !glfwWindowShouldClose(windowId);
    }

    public void update()
    {
        glfwSwapBuffers(windowId);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup()
    {
        glfwDestroyWindow(windowId);
        glfwTerminate();
    }

    public float getAspectRatio()
    {
        return (float) width / height;
    }

    public long getWindowId()
    {
        return windowId;
    }
}
