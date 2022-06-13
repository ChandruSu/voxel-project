package engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import rendering.SimpleModel;
import rendering.Texture;
import rendering.TextureArray;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.stb.STBImage.*;

public class FileLoader
{
    public static final String RES_PATH = "src\\main\\resources\\";

    public static final String SHADER_DIR = "shaders\\";
    public static final String MODEL_DIR = "models\\";
    public static final String IMG_DIR = "textures\\";

    public static String loadFileRaw(String filepath)
    {
        String fileData = "";

        try {
            fileData = Files.readString(Path.of(RES_PATH + filepath));
        } catch (IOException e) {
            Debug.error("Failed to load file: " + filepath);
        }

        return fileData;
    }

    public static SimpleModel loadModelFromOBJ(String filepath)
    {
        AIScene scene = Assimp.aiImportFile(RES_PATH + MODEL_DIR + filepath, Assimp.aiProcess_Triangulate);

        if (scene == null) {
            Debug.failure("Couldn't load .obj model + " + filepath);
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        float[] vertices = new float[mesh.mNumVertices() * 3];
        float[] uvCoords = new float[mesh.mNumVertices() * 2];
        float[] normals  = new float[mesh.mNumVertices() * 3];

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D vertex = mesh.mVertices().get(i);
            AIVector3D uv = mesh.mTextureCoords(0).get(i);
            AIVector3D normal = mesh.mNormals().get(i);

            // loads vertex
            vertices[i * 3] = vertex.x();
            vertices[i * 3 + 1] = vertex.y();
            vertices[i * 3 + 2] = vertex.z();

            // loads uv texture coordinate
            uvCoords[i * 2] = uv.x();
            uvCoords[i * 2 + 1] = uv.y();

            // loads vertex normal
            normals[i * 3] = normal.x();
            normals[i * 3 + 1] = normal.y();
            normals[i * 3 + 2] = normal.z();
        }

        // loads index/element data
        int[] indices = new int[mesh.mNumFaces() * 3];
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            indices[i * 3] = face.mIndices().get(0);
            indices[i * 3 + 1] = face.mIndices().get(1);
            indices[i * 3 + 2] = face.mIndices().get(2);
        }
        return new SimpleModel(vertices, uvCoords, normals, indices);
    }

    public static Texture loadTexture(String filepath) {
        ByteBuffer data;
        int width, height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);


            stbi_set_flip_vertically_on_load(false);
            data = stbi_load(RES_PATH + IMG_DIR + filepath, w, h, comp, 4);
            if (data == null) {
                Debug.error("Failed to load texture: " + filepath + "\n" + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        return new Texture(width, height, data);
    }

    public static TextureArray loadTextureArray(String[] filepaths, int width, int height) {

        ByteBuffer buffer = BufferUtils.createByteBuffer(filepaths.length * width * height * 4);

        try (MemoryStack stack = MemoryStack.stackPush()) {


            for (int i = 0; i < filepaths.length; i++) {

                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                stbi_set_flip_vertically_on_load(false);
                ByteBuffer data = stbi_load(RES_PATH + IMG_DIR + filepaths[i], w, h, comp, 4);

                if (data == null) {
                    Debug.error("Failed to load texture: " + filepaths[i] + "\n" + stbi_failure_reason());
                }

                if (w.get() != width || h.get() != height) {
                    Debug.error("Image '" + filepaths[i] + "', has different dimensions to other images in texture array!");
                }

                buffer.put(width * height * 4 * i, data, 0, width * height * 4);
            }
        }

        return new TextureArray(width, height, filepaths.length, buffer);
    }
}
