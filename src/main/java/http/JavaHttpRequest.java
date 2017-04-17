package http;

import lombok.NonNull;

import java.net.HttpURLConnection;

/**
 * An HTTP request that uses the {@link java.net} package for the underlying implementation.
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaHttpRequest implements HttpRequest {

    private final HttpURLConnection connection;

    public JavaHttpRequest(@NonNull HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public HttpResponse makeRequest() {
        return new JavaHttpResponse(connection);
    }
}
