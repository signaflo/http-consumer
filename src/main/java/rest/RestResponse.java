package rest;

import java.io.InputStream;
import java.util.List;

/**
 * A network response that uses the REST protocol.
 *
 * @param <T> The type of data in the response body.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public interface RestResponse<T> {
    int getStatus();

    List<String> getHeaderField(String headerField);

    String getBodyAsString();

    InputStream getBodyAsInputStream();
}
