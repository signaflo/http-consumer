package rest;

import lombok.NonNull;

import java.net.HttpURLConnection;

/**
 * [Insert class description]
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
