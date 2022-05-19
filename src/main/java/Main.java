import engine.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import rendering.*;
import world.Chunk;


import static org.lwjgl.opengl.GL32.*;

public class Main
{
    public static void main(String[] args)
    {

        Window window = new Window("Voxel Engine", 1000, 800);

        Shader shader = new Shader(2) {
            @Override protected void create() {
                createShader(0, GL_VERTEX_SHADER, "chunk_vertex.glsl");
                createShader(1, GL_FRAGMENT_SHADER, "chunk_fragment.glsl");
            }

            @Override
            protected void initialise() {
                getAttributes("data", "type");
                getUniformLocations("tex", "projection", "view");
            }
        };

        Camera camera = new Camera(0, 0, 0);

        Model model = FileLoader.loadModelFromOBJ("cube.obj");

        Texture img = FileLoader.loadTexture("square.jpg");

        Transform transformation = new Transform(0, 0, -8);

        Chunk chunk = new Chunk();

        while (window.isOpen()) {
            window.update();
            Input.update(window);
            Time.update();

            if (Input.isKeyDown(GLFW.GLFW_KEY_P)) {
                chunk.offset++;
                chunk.generate();
            }

            shader.bind();

            img.bind();
            shader.setUniform("projection", camera.getProjectionMatrix(window));
            shader.setUniform("view", camera.getViewMatrix());

            glBindVertexArray(chunk.getVaoID());
            glPolygonMode(GL_FRONT_AND_BACK, Input.isKeyDown(GLFW.GLFW_KEY_L) ? GL_LINE : GL_FILL);
            glDrawArrays(GL_TRIANGLES, 0, chunk.getVertexCount());
            glBindVertexArray(0);

            img.unbind();
            shader.unbind();
            camera.wasd();
        }

        chunk.delete();
        img.cleanup();
        model.cleanup();
        shader.cleanup();
        window.cleanup();
    }
}
