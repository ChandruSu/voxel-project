package rendering;

import engine.Debug;
import engine.FileLoader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL32.*;

public abstract class Shader
{
    protected final int programID;

    protected final int[] shaders;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(int nShaders)
    {
        this.programID = glCreateProgram();
        this.shaders = new int[nShaders];
        create();

        // links sub shaders into one program
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            Debug.failure("Failed to link shader program: " + glGetProgramInfoLog(programID));
        }

        // validates program feasibility
        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            Debug.failure("Failed to validate shader program: " + glGetProgramInfoLog(programID));
        }

        initialise();
    }

    protected void createShader(int index, int type, String sourcePath)
    {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, FileLoader.loadFileRaw(FileLoader.SHADER_DIR + sourcePath));
        glCompileShader(shaderID);

        // validates sub shader and prints error information
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            Debug.failure("Failed to compile shader: " + sourcePath + "\n" + glGetShaderInfoLog(shaderID));
        }

        glAttachShader(programID, shaderID);
        this.shaders[index] = shaderID;
    }

    protected abstract void create();

    protected abstract void initialise();

    protected void getAttributes(String... names)
    {
        for (String name: names) {
            glGetAttribLocation(programID, name);
        }
    }

    protected void getUniformLocations(String... uniformNames)
    {
        for (String name : uniformNames) {
            int location = glGetUniformLocation(programID, name);

            if (location == -1) {
                Debug.error("Couldn't find uniform location for variable: " + name);
            }

            uniforms.put(name, location);
        }
    }

    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    public void setUniform(String name, Vector2f value) {
        glUniform2f(uniforms.get(name), value.x, value.y);
    }

    public void setUniform(String name, Vector3f value) {
        glUniform3f(uniforms.get(name), value.x, value.y, value.z);
    }

    public void setUniform(String name, Vector4f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String name, Matrix4f value) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        float[] values = new float[16];
        value.get(values);
        buffer.put(values);
        buffer.flip();
        glUniformMatrix4fv(uniforms.get(name), false, buffer);
    }

    public void bind()
    {
        glUseProgram(programID);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void cleanup()
    {
        for (int shader : shaders) {
            glDetachShader(programID, shader);
            glDeleteShader(shader);
        }
        glDeleteProgram(programID);
    }
}
