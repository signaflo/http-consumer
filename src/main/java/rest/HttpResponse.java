package rest;

import java.io.InputStream;
import java.util.List;

/**
 * A network response that utilizes HTTP.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public interface HttpResponse {
    int getStatus();

    List<String> getHeaderField(String headerField);

    String getBodyAsString();

    InputStream getBodyAsInputStream();
}
