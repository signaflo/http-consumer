package http;

/**
 * A network request that utilizes HTTP.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
@FunctionalInterface
public interface HttpRequest {
    HttpResponse makeRequest();
}
