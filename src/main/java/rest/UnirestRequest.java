package rest;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A REST request that utilizes the Unirest library.
 *
 * @param <T> The type of data in the response body.
 */
public final class UnirestRequest<T> implements RestRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnirestRequest.class);
    private final GetRequest getRequest;
    private final Class<T> type;

    public UnirestRequest(@NonNull GetRequest getRequest, Class<T> type) {
        this.getRequest = getRequest;
        this.type = type;
    }


    GetRequest getGetRequest() {
        return this.getRequest;
    }

    @Override
    public RestResponse makeRequest() {
        try {
            return new UnirestResponse<>(getRequest.asObject(type));
        } catch (UnirestException e) {
            String message = "Unirest threw exception when attempting to get response.";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnirestRequest request = (UnirestRequest) o;

        return getRequest.equals(request.getRequest);
    }

    @Override
    public int hashCode() {
        return getRequest.hashCode();
    }

    @Override
    public String toString() {
        return "UnirestRequest{" + "getRequest=" + getRequest.getHttpMethod() +
                ' ' + getRequest.getUrl() + ", type=" + type + '}';
    }
}
