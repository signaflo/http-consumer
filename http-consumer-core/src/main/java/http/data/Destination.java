package http.data;

/**
 * Represents a data output destination.
 * @param <T> the type of destination.
 */
public interface Destination<T> {
    T get();
}
