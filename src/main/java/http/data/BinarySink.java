package http.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Apr. 19, 2017
 */
public class BinarySink implements Sink {

    private static final Logger LOG = LoggerFactory.getLogger(BinarySink.class);

    private final int numBytes;
    private final InputStream inputStream;
    private final File file;

    public BinarySink(int numBytes, String outputPath, String fileName, InputStream inputStream) {
        this.numBytes = numBytes;
        this.file = Sink.getFile(outputPath, fileName);
        this.inputStream = inputStream;
    }

    @Override
    public void write() {
        writeToFile();
    }

    private void writeToFile() {
        if (file.exists()) {
            IllegalStateException e = new IllegalStateException("The file already exists. No data will be saved.");
            LOG.error("The file " + file.getAbsolutePath() + " already exists.", e);
            throw e;
        }

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file), numBytes);
             InputStream bufferedInputStream = new BufferedInputStream(this.inputStream, numBytes)) {
            byte[] rawData = new byte[numBytes];
            int len;
            while ((len = bufferedInputStream.read(rawData)) != -1) {
                outputStream.write(rawData, 0, len);
            }
            outputStream.flush();
        } catch (IOException ioe) {
            LOG.error("Error writing response data to file.", ioe);
            throw new RuntimeException("The file " + file.getAbsolutePath() + " could not be created.");
        }
    }
}
