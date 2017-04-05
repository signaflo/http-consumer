package rest;

/**
 * A network request that uses the REST protocol.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
@FunctionalInterface
public interface RestRequest {
    RestResponse makeRequest();
}
