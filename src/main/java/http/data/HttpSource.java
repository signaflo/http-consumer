package http.data;

import http.execution.HttpDailyRunner;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.HttpRequest;
import http.HttpResponse;
import http.JavaHttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSource implements UpdatingSource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpDailyRunner.class);
    private static final int NOT_MODIFIED = 304;
    private static final int OK = 200;

    private final String address;
    private final URL url;
    private HttpURLConnection connection;
    private HttpResponse httpResponse = null;
    private final Map<String, String> requestProperties;

    public HttpSource(@NonNull String address, @NonNull Map<String, String> requestProperties) {
        this.address = address;
        this.requestProperties = new HashMap<>(requestProperties);
        if (!this.requestProperties.containsKey("If-None-Match")) {
            this.requestProperties.put("If-None-Match", "");
        }
        try {
            this.url = new URL(address);
        } catch (IOException e) {
            LOG.error("Could not create URL at address: {}", address, e);
            throw new RuntimeException(e);
        }
    }

    public HttpSource(@NonNull String address) {
        this(address, new HashMap<>(3));
    }

    void setRequestProperty(@NonNull String key, @NonNull String value) {
        this.requestProperties.put(key, value);
    }

    void updateWith(@NonNull final HttpRequest httpRequest) {
        updateRestResponse(httpRequest);
        updateETag();
    }

    void updateETag() {
        if (this.httpResponse != null && this.httpResponse.getStatus() != NOT_MODIFIED) {
            List<String> headerField = this.httpResponse.getHeaderField("ETag");
            if (headerField != null && headerField.size() > 0) {
                setRequestProperty("If-None-Match", headerField.get(0));
            } else {
                throw new RuntimeException("HTTP ETag header expected but not found.");
            }
        }
    }

    void updateRestResponse(final HttpRequest httpRequest) {
        this.httpResponse = httpRequest.makeRequest();
        if (this.httpResponse != null &&
            !(this.httpResponse.getStatus() == OK || this.httpResponse.getStatus() == NOT_MODIFIED)) {
            throw new IllegalStateException(String.format("Expected response code to be %d or %d, but was %d",
                                                          OK, NOT_MODIFIED, this.httpResponse.getStatus()));
        }
    }

    String getEtag() {
        return this.requestProperties.get("If-None-Match");
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
        int status = this.httpResponse.getStatus();
        LOG.info("Checking http response status... response code = {}", status);
        return status == OK;
    }

    @Override
    public void update() {
        try {
            connection = (HttpURLConnection) (this.url.openConnection());
            for (String key : this.requestProperties.keySet()) {
                connection.setRequestProperty(key, this.requestProperties.get(key));
            }
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
