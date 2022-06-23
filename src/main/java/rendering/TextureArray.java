package rendering;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.*;

public class TextureArray
{
    private final int textureID;
    private final int width, height;
    private final int textureCount;

    public TextureArray(int width, int height, int textureCount, ByteBuffer images) {
        this.textureID = glGenTextures();
        this.width = width;
        this.height = height;
        this.textureCount = textureCount;

        bind();
        setParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        setParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
        setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, width, height, textureCount, 0, GL_RGBA, GL_UNSIGNED_BYTE, images);

        unbind();
    }

    public void setParameter(int param, int value)
    {
        glTexParameteri(GL_TEXTURE_2D_ARRAY, param, value);
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureID);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    public void cleanup()
    {
        glDeleteTextures(textureID);
    }

    public int getTextureID() {
        return textureID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureCount() {
        return textureCount;
    }
}
