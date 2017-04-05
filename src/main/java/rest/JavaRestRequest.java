package rest;

import java.net.HttpURLConnection;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 31, 2017
 */
public class JavaRestRequest<T> implements RestRequest<T> {

    private final HttpURLConnection connection;

    JavaRestRequest(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public RestResponse<T> makeRequest() {
        return new JavaRestResponse<T>(connection);
    }
}
