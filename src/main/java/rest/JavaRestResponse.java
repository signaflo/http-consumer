package rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaRestResponse<T> implements RestResponse<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaRestResponse.class);
    private final HttpURLConnection connection;

    JavaRestResponse(HttpURLConnection connection) {
        this.connection = connection;
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
        return new String(getBodyAsByteArray());
    }

    @Override
    public InputStream getBodyAsInputStream() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            LOGGER.error("Could not retrieve input from the URL connection.", e);
            throw new RuntimeException(e);
        }
    }

    private byte[] getBodyAsByteArray() {
        try(BufferedInputStream inputStream = new BufferedInputStream(getBodyAsInputStream());) {
        byte[] bytes = new byte[connection.getContentLength()];
        inputStream.read(bytes);
        return bytes;
        } catch (IOException e) {
            LOGGER.error("Could not read bytes from the URL connection.", e);
            throw new RuntimeException();
        }
    }
}
