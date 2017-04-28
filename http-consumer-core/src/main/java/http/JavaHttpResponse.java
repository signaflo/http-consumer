package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

/**
 * An HTTP response that uses the {@link java.net} package for the underlying implementation.
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaHttpResponse implements HttpResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpResponse.class);
    private final HttpURLConnection connection;
    private final byte[] bytes;

    JavaHttpResponse(HttpURLConnection connection) {
        this.connection = connection;
        this.bytes = getBytesFromConnection();
    }

    private byte[] getBytesFromConnection() {
        int contentLength = connection.getContentLength();
        if (contentLength < 1) {
            return new byte[0];
        }
        try (InputStream inputStream = new BufferedInputStream(
                connection.getInputStream()); ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                contentLength)) {
            byte[] freshBytes = new byte[contentLength];
            int len;
            while ((len = inputStream.read(freshBytes)) != -1) {
                outputStream.write(freshBytes, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Could not read bytes from the URL connection.", e);
            throw new RuntimeException();
        }
    }

    @Override
    public int getStatus() {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.error("Could not retrieve the status code from the URL connection.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getHeaderField(String headerField) {
        return Collections.singletonList(this.connection.getHeaderField(headerField));
    }

    @Override
    public String getBodyAsString() {
        return new String(this.bytes.clone());
    }

    @Override
    public InputStream getBodyAsInputStream() {
        return new ByteArrayInputStream(this.bytes.clone());
    }
}
