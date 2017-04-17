package http.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSink implements Sink {

    private static final Logger LOG = LoggerFactory.getLogger(FileSink.class);

    private final int numChars;
    private final String outputPath;
    private final String fileName;
    private final InputStream inputStream;
    private final Charset charset;

    public FileSink(int numChars, String outputPath, String fileName, InputStream inputStream, Charset charset) {
        this.numChars = numChars;
        this.outputPath = outputPath;
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.charset = charset;
    }

    @Override
    public void write() {
        writeToFile();
    }

    private void writeToFile() {
        File file = getFile();
        if (file.exists()) {
            IllegalStateException e = new IllegalStateException("The file already exists. No http.data will be saved.");
            LOG.error("The file " + file.getAbsolutePath() + " already exists.", e);
            throw e;
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file), numChars);
             Reader reader = new BufferedReader(new InputStreamReader(
                     this.inputStream, this.charset), numChars)) {
            char[] rawData = new char[numChars];
            int len;
            while ((len = reader.read(rawData)) != -1) {
                writer.write(rawData, 0, len);
            }
            writer.flush();
        } catch (IOException ioe) {
            LOG.error("Error writing response http.data to file.", ioe);
            throw new RuntimeException("The file " + file.getAbsolutePath() + " could not be created.");
        }
    }

    private File getFile() {
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
