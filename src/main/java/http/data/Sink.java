package http.data;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Sink {

    void write();

    static File getFile(@NonNull String outputPath, @NonNull String fileName) {
        Path path = Paths.get(outputPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Path fullPath = Paths.get(outputPath, fileName);
        return fullPath.toFile();
    }
}
