import engine.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.*;

import java.io.File;

import static org.lwjgl.opengl.GL32.*;

public class Main
{
    public static void main(String[] args)
    {
        Window window = new Window("Voxel Engine", 1000, 800);

        Shader shader = new Shader(2) {
            @Override protected void create() {
                createShader(0, GL_VERTEX_SHADER, "vertex.glsl");
                createShader(1, GL_FRAGMENT_SHADER, "fragment.glsl");
            }

            @Override
            protected void initialise() {
                getAttributes("position", "texCoord", "normal");
                getUniformLocations("tex", "projection", "view", "transformation");
            }
        };

        Camera camera = new Camera(0, 0, 0);

        Model model = FileLoader.loadModelFromOBJ("cube.obj");

        Texture img = FileLoader.loadTexture("cobble.png");

        Transform transformation = new Transform(0, 0, -8);

        while (window.isOpen()) {
            window.update();
            Input.update(window);
            Time.update();

            //transformation.rotate(5f * Time.getDeltaTime(), 5f * Time.getDeltaTime(), 5f * Time.getDeltaTime());

            shader.bind();

            shader.setUniform("projection", camera.getProjectionMatrix(window));
            shader.setUniform("view", camera.getViewMatrix());
            shader.setUniform("transformation", transformation.getTransformation());

            img.bind();
            model.bind();
            glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
            model.unbind();
            img.unbind();
            shader.bind();

            camera.wasd();
        }

        img.cleanup();
        model.cleanup();
        shader.cleanup();
        window.cleanup();
    }
}
