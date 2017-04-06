package rest;

import lombok.NonNull;

import java.net.HttpURLConnection;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaRestRequest implements RestRequest {

    private final HttpURLConnection connection;

    JavaRestRequest(@NonNull HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public RestResponse makeRequest() {
        return new JavaRestResponse(connection);
    }
}
