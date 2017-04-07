package data;

import execution.HttpRunner;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.HttpRequest;
import rest.HttpResponse;
import rest.JavaHttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpSource implements UpdatingSource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRunner.class);
    private static final int NOT_MODIFIED = 304;
    private static final int OK = 200;

    private final String address;
    private final URL url;
    private HttpURLConnection connection;
    private String etag = "";
    private HttpResponse httpResponse = null;

    public HttpSource(@NonNull String address) {
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
    public int size() {
        if (connection == null) {
            return 0;
        }
        return connection.getContentLength();
    }

    @Override
    public boolean fresh() {
        return this.httpResponse.getStatus() == OK;
    }

    @Override
    public void update() {
        try {
            connection = (HttpURLConnection) (this.url.openConnection());
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestProperty("If-None-Match", this.etag);
            connection.connect();
            HttpRequest httpRequest = new JavaHttpRequest(connection);
            this.updateWith(httpRequest);
        } catch (IOException e) {
            LOG.error("Could not open connection to {}", address, e);
        }

    }

    @Override
    public InputStream read() {
        return this.httpResponse.getBodyAsInputStream();
    }
}
