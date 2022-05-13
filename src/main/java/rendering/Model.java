package rendering;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL32.*;

public abstract class Model
{
    protected final int vaoID;
    protected final int[] vBuffers;

    private final int vertexCount;
    private int elementBufferID;

    public Model(int nVertices, int nVertexBuffers)
    {
        this.vaoID = glGenVertexArrays();
        this.vBuffers = new int[nVertexBuffers];
        this.vertexCount = nVertices;
    }

    public void bind()
    {
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferID);
        for (int i = 0; i < vBuffers.length; i++) {
            glEnableVertexAttribArray(i);
        }
    }

    public void unbind()
    {
        for (int i = 0; i < vBuffers.length; i++) {
            glDisableVertexAttribArray(i);
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void cleanup()
    {
        glDeleteBuffers(vBuffers);
        glDeleteBuffers(elementBufferID);
        glDeleteVertexArrays(vaoID);
    }

    public void setElementBuffer(int[] data)
    {
        IntBuffer buf = BufferUtils.createIntBuffer(data.length);
        buf.put(data);
        buf.flip();

        int id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        this.elementBufferID = id;
    }

    public void setVertexBuffer(int bufferIndex, float[] data, int components)
    {
        FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
        buf.put(data);
        buf.flip();

        int id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        glVertexAttribPointer(bufferIndex, components, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        this.vBuffers[bufferIndex] = id;
    }

    public int getVaoID()
    {
        return vaoID;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }
}
