package rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

/**
 * A rest response that uses the {@link java.net} package in the underlying implementation.
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaRestResponse implements RestResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaRestResponse.class);
    private final HttpURLConnection connection;
    private final byte[] bytes;

    JavaRestResponse(HttpURLConnection connection) {
        this.connection = connection;
        this.bytes = getBytesFromConnection();
    }

    private byte[] getBytesFromConnection() {
        try(InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(connection.getContentLength())) {
            byte[] freshBytes = new byte[connection.getContentLength()];
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
