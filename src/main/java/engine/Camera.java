package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import rendering.Window;

public class Camera
{
    private final Transform transform;

    private final float speed = 15f;
    private final float mouseSensitivity = 0.4f;
    private Vector3f camRotation = new Vector3f();

    public Camera(float x, float y, float z)
    {
        transform = new Transform(x, y, z);
    }

    public void wasd()
    {
        camRotation.x += Input.getDeltaMousePosition().y * Time.getDeltaTime() * mouseSensitivity * 1.66f;
        camRotation.y += Input.getDeltaMousePosition().x * Time.getDeltaTime() * mouseSensitivity;
        camRotation.x = MathUtils.clamp(camRotation.x, -MathUtils.RAD90 + 0.0001f, MathUtils.RAD90 - 0.0001f);
        transform.getRotation().set(new Quaternionf().rotateXYZ(camRotation.x, camRotation.y, 0.0f));

        // 3D movement
        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) transform.translate(transform.forwardXZ().mul(-speed * Time.getDeltaTime()));
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) transform.translate(transform.forwardXZ().mul( speed * Time.getDeltaTime()));
        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) transform.translate(transform.rightXZ().mul(-speed * Time.getDeltaTime()));
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) transform.translate(transform.rightXZ().mul( speed * Time.getDeltaTime()));
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) transform.translate(0, -speed * Time.getDeltaTime(), 0);
        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) transform.translate(0, speed * Time.getDeltaTime(), 0);

        if (Input.isKeyPressed(GLFW.GLFW_KEY_H))
            Input.lockCursor();
        else if (Input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE))
            Input.resetCursor();
    }

    public Matrix4f getViewMatrix()
    {
        return new Matrix4f().identity().rotate(transform.getRotation()).translate(new Vector3f(transform.getPosition()).negate());
    }

    public Matrix4f getProjectionMatrix(Window window)
    {
        return new Matrix4f().perspective((float) Math.toRadians(60), window.getAspectRatio(), 0.001f, 1000.0f, false);
    }
}
