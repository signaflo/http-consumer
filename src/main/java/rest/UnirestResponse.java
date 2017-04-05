package rest;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import lombok.NonNull;

import java.io.InputStream;
import java.util.List;

/**
 * A REST response that utilizes the Unirest library.
 *
 * @param <T> The type of data in the response body.
 */
public final class UnirestResponse<T> implements RestResponse {

    //private static final Logger LOGGER = LoggerFactory.getLogger(UnirestResponse.class);
    private final HttpResponse<T> httpResponse;
    private final Headers headers;

    UnirestResponse(@NonNull HttpResponse<T> httpResponse) {
        this.httpResponse = httpResponse;
        this.headers = httpResponse.getHeaders();
    }

    private T getBody() {
        return this.httpResponse.getBody();
    }

    @Override
    public int getStatus() {
        return this.httpResponse.getStatus();
    }

    @Override
    public List<String> getHeaderField(String headerField) {
        return this.headers.get(headerField);
    }

    @Override
    public String getBodyAsString() {
        T body = getBody();
        if (body == null) {
            return "";
        }
        return body.toString();
    }

    @Override
    public InputStream getBodyAsInputStream() {
        return this.httpResponse.getRawBody();
    }

    @Override
    public String toString() {
        return "UnirestResponse{" + "httpResponse=" + httpResponse.getStatus() +
                ' ' + httpResponse.getStatusText() + ", headers=" + headers + '}';
    }
}
