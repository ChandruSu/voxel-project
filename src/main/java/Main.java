import engine.Input;
import rendering.Model;
import rendering.Shader;
import rendering.SimpleModel;
import rendering.Window;

import static org.lwjgl.opengl.GL32.*;

public class Main
{
    public static void main(String[] args)
    {
        Window window = new Window("Voxel Engine", 1000, 800);

        Shader shader = new Shader(2) {
            @Override
            protected void create() {
                getAttributes("position", "normal");
                createShader(0, GL_VERTEX_SHADER, "vertex.glsl");
                createShader(1, GL_FRAGMENT_SHADER, "fragment.glsl");
            }
        };

        float[] vertices = {
                -0.5f, -0.5f, 0,
                -0.5f,  0.5f, 0,
                 0.5f, -0.5f, 0,
                 0.5f,  0.5f, 0,
        };

        float[] normals = {
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
        };

        float[] uvs = {
                0, 0,
                0, 1,
                1, 0,
                1, 1
        };

        int[] indices = {
                0, 2, 1,
                1, 2, 3
        };

        Model model = new SimpleModel(vertices, uvs, normals, indices);

        while (window.isOpen()) {
            window.update();
            Input.update(window);

            shader.bind();
            model.bind();
            glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
            model.unbind();
            shader.bind();

            if (Input.isMousePressed(0)) {
                System.out.println(Input.getMousePosition());
            }
        }

        model.cleanup();
        shader.cleanup();
        window.cleanup();
    }
}
