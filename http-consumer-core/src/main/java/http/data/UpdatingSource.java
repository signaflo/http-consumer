package http.data;

public interface UpdatingSource extends Source {

    boolean fresh();

    default boolean stale() {
        return !fresh();
    }

    void update();
}
