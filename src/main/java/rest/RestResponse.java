package rest;

import java.io.InputStream;
import java.util.List;

/**
 * A network response that uses the REST protocol.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public interface RestResponse {
    int getStatus();

    List<String> getHeaderField(String headerField);

    String getBodyAsString();

    InputStream getBodyAsInputStream();
}
