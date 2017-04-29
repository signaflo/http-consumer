package http.data;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a destination for a file. This class mostly acts as a wrapper for a {@link java.io.File}.
 * It's main utility is that it breaks down and encapsulates various pieces of information that
 * make up the full path of the file.
 */
public class FileDestination implements Destination<File> {

    private final File file;

    /**
     *
     * Create a new file destination with the supplied directory and naming information.
     *
     * @param directory the directory the file will be located in. This may not be null.
     * @param prefix the file prefix. This may be null, in which case it is ignored.
     * @param name the name of the file. This may not be null.
     * @param suffix the end of the file name, typically used to denote the file type. This may be null,
     *              in which case it is ignored.
     * @param separator the text to separate the prefix from the name. This may be null, in which case it is ignored.
     */
    private FileDestination(@NonNull String directory, String prefix, @NonNull String name, String suffix,
                           String separator) {
        String fullName = "";
        if (prefix != null) fullName += prefix;
        if (separator != null) fullName += separator;
        fullName += name;
        if (suffix != null) fullName += "." + suffix;
        this.file = getFile(directory, fullName);
    }

    /**
     *
     * Create a new file destination with the supplied directory and naming information. This constructor uses the
     * hyphen as the default separator.
     *
     * @param directory the directory the file will be located in. This may not be null.
     * @param prefix the file prefix. This may be null, in which case it is ignored.
     * @param name the name of the file. This may not be null.
     * @param suffix the end of the file name, typically used to denote the file type. This may be null,
     *              in which case it is ignored.
     */
    public FileDestination(@NonNull String directory, String prefix, @NonNull String name, String suffix) {
        this(directory, prefix, name, suffix, "-");
    }

    @Override
    public File get() {
        return this.file;
    }

    private File getFile(@NonNull String outputPath, @NonNull String fileName) {
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
