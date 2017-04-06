package rest;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 * A consumer of some given data source.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class Consumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private static final int NOT_MODIFIED = 304;
    private static final int OK = 200;
    //private static final AtomicLong FILE_COUNTER = new AtomicLong();
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String address;
    private final URL url;
    private String etag = "";
    private RestResponse restResponse = null;

    public Consumer(@NonNull String address) {
        this.address = address;
        try {
            this.url = new URL(address);
        } catch (IOException e) {
            LOGGER.error("Could not create URL at address: {}", address, e);
            throw new RuntimeException(e);
        }
    }

    Consumer(URL url) {
        this.url = url;
        this.address = url.toString();
    }

    void updateWith(@NonNull final RestRequest restRequest) {
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

    void updateRestResponse(final RestRequest restRequest) {
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
            RestRequest restRequest = new JavaRestRequest(connection);
            this.updateWith(restRequest);
            if (this.restResponse.getStatus() == OK) {
                writeToFile(connection.getContentLength());
            }
        } catch (IOException e) {
            LOGGER.error("Could not open connection to {}", address, e);
        }
    }

    private void writeToFile(int numChars) {
        String currentDate = LocalDateTime.now().format(DTF);
        String path = currentDate + "_" + System.currentTimeMillis();
        File file = new File(path);
        if (file.exists()) {
            IllegalStateException e = new IllegalStateException("The file already exists. No data will be saved.");
            LOGGER.error("The file \"" + file.getAbsolutePath() + "\" already exists.", e);
            throw e;
        }
        try (Writer writer = new BufferedWriter(new FileWriter(file), numChars); Reader reader = new BufferedReader(
                new InputStreamReader(this.restResponse.getBodyAsInputStream(), Charset.forName("UTF-8")), numChars)) {
            char[] rawData = new char[numChars];
            int len;
            while ((len = reader.read(rawData)) != -1) {
                writer.write(rawData, 0, len);
            }
            writer.flush();
        } catch (IOException ioe) {
            LOGGER.error("Error writing response data to file.", ioe);
            throw new RuntimeException("The file " + path + " could not be created.");
        }
    }
}
