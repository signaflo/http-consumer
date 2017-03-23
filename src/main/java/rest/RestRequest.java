package rest;

/**
 * A network request that uses the REST protocol.
 *
 * @param <T> The type of data in the response body.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
@FunctionalInterface
public interface RestRequest<T> {
    RestResponse<T> makeRequest();
}
