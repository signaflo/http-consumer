package http.execution;

import http.data.Destination;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

/**
 * A runner that takes a URL as input and outputs the body of the http response to a destination. With each run,
 * implementations may choose to write to the same destination, or to create a new destination on the fly.
 *
 * @param <T> the type of destination the runner will write to.
 */
public interface HttpRunner<T> extends Runnable {

    /**
     * Create a new connection to the underlying http source.
     *
     * @return A new request object.
     */
    Request createRequest();

    /**
     * Get the response from the given request.
     *
     * @param request the request to the entity a response is sought from.
     * @return the response from the given request.
     */
    Response getResponse(Request request);

    /**
     * Return the status code from the given http response.
     * @param response an http response.
     * @return the status code from the given http response.
     */
    int getStatusCode(HttpResponse response);

    /**
     * Create a new destination.
     *
     * @return a new destination object.
     */
    Destination createDestination();

    /**
     * Write the data received from the http response to the destination.
     *
     * @param response the response containing the content to be supplied to the destination.
     * @param destination the destination for the http response content.
     */
    void write(HttpResponse response, Destination<T> destination);
}
