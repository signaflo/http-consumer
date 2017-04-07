package execution;

import data.FileSink;
import data.Sink;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.HttpRequest;
import rest.HttpResponse;
import rest.JavaHttpRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A consumer of data from a HTTP source.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class HttpRunner implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRunner.class);
    private static final int NOT_MODIFIED = 304;
    private static final int OK = 200;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String address;
    private final URL url;
    private String etag = "";
    private HttpResponse httpResponse = null;

    HttpRunner(@NonNull URL url) {
        this.url = url;
        this.address = url.toString();
    }

    HttpRunner(@NonNull String address) {
        this.address = address;
        try {
            this.url = new URL(address);
        } catch (IOException e) {
            LOG.error("Could not create URL at address: {}", address, e);
            throw new RuntimeException(e);
        }
    }

    void updateWith(@NonNull final HttpRequest httpRequest) {
        updateRestResponse(httpRequest);
        updateETag();
    }

    void updateETag() {
        if (this.httpResponse != null && this.httpResponse.getStatus() != NOT_MODIFIED) {
            List<String> headerField = this.httpResponse.getHeaderField("ETag");
            if (headerField != null && headerField.size() > 0) {
                this.etag = headerField.get(0);
            } else {
                throw new RuntimeException("HTTP ETag header expected but not found.");
            }
        }
    }

    void updateRestResponse(final HttpRequest httpRequest) {
        this.httpResponse = httpRequest.makeRequest();
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
            HttpRequest httpRequest = new JavaHttpRequest(connection);
            this.updateWith(httpRequest);
            if (this.httpResponse.getStatus() == OK) {
                int numChars = connection.getContentLength();
                String outputPath = "data/" + LocalDateTime.now().format(DTF);
                String fileName = LocalTime.now().toString();
                Sink sink = new FileSink(numChars, outputPath, fileName,
                                         this.httpResponse.getBodyAsInputStream(), Charset.forName("UTF-8"));
                sink.write();
            }
        } catch (IOException e) {
            LOG.error("Could not open connection to {}", address, e);
        }
    }
}
