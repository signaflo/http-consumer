package rest;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A consumer of some given data source.
 *
 * @param <T> The type of data to be consumed.
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class Consumer<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private static final int NOT_MODIFIED = 304;
    private static final AtomicLong FILE_COUNTER = new AtomicLong();
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String url;
    private final Class<T> type;
    private String etag = "";
    private RestResponse<T> restResponse = null;

    Consumer(@NonNull String url, @NonNull Class<T> type) {
        this.url = url;
        this.type = type;
    }

    void updateWith(@NonNull final RestRequest<T> restRequest) {
        updateRestResponse(restRequest);
        updateETag();
    }

    void updateETag() {
        if (this.restResponse != null && this.restResponse.getStatus() != NOT_MODIFIED) {
            List<String> headerField = this.restResponse.getHeaderField("ETag");
            if (headerField != null && headerField.size() > 0) {
                this.etag = headerField.get(0);
            } else {
                throw new RuntimeException("HTTP ETag header expected but not found.");
            }
        }
    }

    void updateRestResponse(final RestRequest<T> restRequest) {
        this.restResponse = restRequest.makeRequest();
    }

    String getEtag() {
        return this.etag;
    }

    @Override
    public void run() {
        GetRequest getRequest = Unirest.get(this.url);
        getRequest.header("If-None-Match", this.etag);
        RestRequest<T> restRequest = new UnirestRequest<>(getRequest, this.type);
        this.updateWith(restRequest);
        if (this.restResponse.getStatus() == 200) {
            String currentDate = LocalDateTime.now().format(DTF);
            String path = currentDate + "_" + FILE_COUNTER.incrementAndGet();
            File file = new File(path);
            if (file.exists()) {
                RuntimeException e = new RuntimeException(
                        "The file " + file + " exists and will not be overwritten." + " Data will not be saved.");
                LOGGER.error("The file " + file + " already exists.", e);
                throw e;
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    file.createNewFile();
                    InputStream inputStream = this.restResponse.getBodyAsInputStream();
                    int streamLength = inputStream.available();
                    byte[] rawData = new byte[streamLength];
                    inputStream.read(rawData);
                    outputStream.write(rawData);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException ioe) {
                    LOGGER.error("Error writing to output stream.", ioe);
                    throw new RuntimeException("The file " + path + " could not be created.");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Consumer{" + "etag='" + etag + '\'' + ", url='" + url + '\'' + ", restResponse=" + restResponse +
               ", type=" + type + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Consumer<?> that = (Consumer<?>) o;

        if (!url.equals(that.url)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
