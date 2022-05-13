package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLoader
{
    public static final String RES_PATH = "src\\main\\resources\\";

    public static final String SHADER_DIR = "shaders\\";

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
}
