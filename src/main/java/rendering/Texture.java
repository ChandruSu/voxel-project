package rendering;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL32.*;

public class Texture
{
    private final int textureID;
    private final int width;
    private final int height;

    public Texture(int width, int height, ByteBuffer data)
    {
        this.textureID = glGenTextures();
        this.width = width;
        this.height = height;

        bind();
        setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -1.0f);
        unbind();
    }

    public void setParameter(int param, int value)
    {
        glTexParameteri(GL_TEXTURE_2D, param, value);
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup()
    {
        glDeleteTextures(textureID);
    }

    public int getTextureID()
    {
        return textureID;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
