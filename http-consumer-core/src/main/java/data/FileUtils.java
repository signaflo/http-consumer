package data;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Static utility methods for working with files.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Create a new file with the supplied directory and naming information.
     *
     * @param directory the directory the file will be located in. This may not be null.
     * @param prefix    the file prefix. This may be null, in which case it is ignored.
     * @param name      the name of the file. This may not be null.
     * @param suffix    the end of the file name, typically used to denote the file type. This may be null,
     *                  in which case it is ignored.
     * @param separator the text to separate the prefix from the name. This may be null, in which case it is ignored.
     * @return a new file from the supplied directory and naming information.
     */
    public static File createFile(@NonNull String directory, String prefix, @NonNull String name, String suffix,
                                  String separator) {
        String fullName = "";
        if (prefix != null) fullName += prefix;
        if (separator != null) fullName += separator;
        fullName += name;
        if (suffix != null) fullName += "." + suffix;
        return getFile(directory, fullName);
    }

    /**
     * Create a new file with the supplied directory and naming information. This constructor uses the
     * hyphen as the default separator.
     *
     * @param directory the directory the file will be located in. This may not be null.
     * @param prefix    the file prefix. This may be null, in which case it is ignored.
     * @param name      the name of the file. This may not be null.
     * @param suffix    the end of the file name, typically used to denote the file type. This may be null,
     *                  in which case it is ignored.
     * @return a new file from the supplied directory and naming information.
     */
    public static File createFile(@NonNull String directory, String prefix, @NonNull String name, String suffix) {
        return createFile(directory, prefix, name, suffix, "-");
    }

    private static File getFile(@NonNull String outputPath, @NonNull String fileName) {
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
