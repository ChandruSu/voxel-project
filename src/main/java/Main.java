import engine.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import rendering.*;
import world.Chunk;


import java.util.ArrayList;
import java.util.List;

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
                getUniformLocations("tex", "projection", "view", "position");
            }
        };

        Camera camera = new Camera(0, 0, 0);

        TextureArray atlas = FileLoader.loadTextureArray(new String[] { "cobble.png", "dirt.jpg" }, 256, 256);
        Texture img = FileLoader.loadTexture("cobble.png");

        List<Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < 1; i++)
            for (int j = 0; j < 1; j++)
                for (int k = 0; k < 1; k++)
                    chunks.add(new Chunk(new Vector3i(i*32, k*32, j*32)));

        while (window.isOpen())
        {
            window.update();
            Input.update(window);
            Time.update();

            shader.bind();

            atlas.bind();
            shader.setUniform("projection", camera.getProjectionMatrix(window));
            shader.setUniform("view", camera.getViewMatrix());

            for (Chunk chunk: chunks) {
                shader.setUniform("position", chunk.getPosition());
                glBindVertexArray(chunk.getVaoID());
                glPolygonMode(GL_FRONT_AND_BACK, Input.isKeyDown(GLFW.GLFW_KEY_L) ? GL_LINE : GL_FILL);
                glDrawArrays(GL_TRIANGLES, 0, chunk.getVertexCount());
                glBindVertexArray(0);
            }

            atlas.unbind();
            shader.unbind();
            camera.wasd();
        }

        for (Chunk chunk: chunks) {
            chunk.delete();
        }

        img.cleanup();
        atlas.cleanup();
        shader.cleanup();
        window.cleanup();
    }
}
