package rest;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private static final int OK = 200;
    private static final AtomicLong FILE_COUNTER = new AtomicLong();
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String address;
    private final URL url;
    private final Class<T> type;
    private String etag = "";
    private RestResponse<T> restResponse = null;

    public Consumer(@NonNull String address, @NonNull Class<T> type) {
        this.address = address;
        this.type = type;
        try {
            this.url = new URL(address);
        } catch (IOException e) {
            LOGGER.error("Could not create URL at address: {}", address, e);
            throw new RuntimeException(e);
        }
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
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) (this.url.openConnection());
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestProperty("If-None-Match", this.etag);
            connection.connect();
            RestRequest<T> restRequest = new JavaRestRequest<>(connection);
            this.updateWith(restRequest);
            if (this.restResponse.getStatus() == OK) {
                writeToFile(connection.getContentLength());
            }
        } catch (IOException e) {
            LOGGER.error("Could not open connection to {}", address, e);
        }
    }

    private void writeToFile(int numBytes) {
        String currentDate = LocalDateTime.now().format(DTF);
        String path = currentDate + "_" + FILE_COUNTER.incrementAndGet();
        File file = new File(path);
        if (file.exists()) {
            RuntimeException e = new RuntimeException(
                    "The file " + file + " exists and will not be overwritten." + " Data will not be saved.");
            LOGGER.error("The file " + file + " already exists.", e);
            throw e;
        } else {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    file), numBytes); InputStream inputStream = new BufferedInputStream(
                    this.restResponse.getBodyAsInputStream(), numBytes)) {
                file.createNewFile();
                byte[] rawData = new byte[numBytes];
                int len;
                while ((len = inputStream.read(rawData)) != -1) {
                    outputStream.write(rawData, 0, len);
                }
                outputStream.flush();
            } catch (IOException ioe) {
                LOGGER.error("Error writing to output stream.", ioe);
                throw new RuntimeException("The file " + path + " could not be created.");
            }
        }
    }

    @Override
    public String toString() {
        return "Consumer{" + "etag='" + etag + '\'' + ", address='" + address + '\'' + ", restResponse=" +
               restResponse + ", type=" + type + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Consumer<?> that = (Consumer<?>) o;

        if (!address.equals(that.address)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
