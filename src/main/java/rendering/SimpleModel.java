package rendering;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SimpleModel extends Model
{
    public SimpleModel(float[] vertices, float[] uvCoordinates, float[] normals, int[] indices)
    {
        super(indices.length, 3);
        bind();
        setVertexBuffer(0, vertices, 3);
        setVertexBuffer(1, uvCoordinates, 2);
        setVertexBuffer(2, normals, 3);
        setElementBuffer(indices);
        unbind();
    }
}
