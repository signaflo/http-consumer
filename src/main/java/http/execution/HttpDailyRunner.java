package http.execution;

import lombok.NonNull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import http.data.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * A daily consumer of data from an HTTP source.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class HttpDailyRunner implements HttpRunner<File> {

    private static final Logger logger = LoggerFactory.getLogger(HttpDailyRunner.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss-SSS");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    private static final int OK = 200;
    private static final int NOT_MODIFIED = 304;

    private final String uri;
    private final Map<String, String> requestProperties;
    private final PathProperties pathProperties;
    private boolean firstRun = true;
    private String etag = "";

    public HttpDailyRunner(String uri, Map<String, String> requestProperties, PathProperties pathProperties) {
        this.uri = uri;
        this.requestProperties = new HashMap<>(requestProperties);
        this.pathProperties = pathProperties;
    }

    @Override
    public Request createRequest() {
        return Request.Get(uri);
    }

    @Override
    public int getStatusCode(@NonNull HttpResponse response) {
        return response.getStatusLine().getStatusCode();

    }

    @Override
    public Response getResponse(@NonNull Request request) {
        for (String s : requestProperties.keySet()) {
            request.addHeader(s, requestProperties.get(s));
        }
        request.addHeader("If-None-Match", this.etag);
        try {
            return request.execute();
        } catch (IOException e) {
            logger.error("Error executing the request.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Destination<File> createDestination() {
        String time = LocalTime.now().format(TIME_FORMATTER);
        String day  = LocalDate.now().format(DAY_FORMATTER);
        String dirName = pathProperties.getDirectory() + "/" + day;
        String prefix = pathProperties.getPrefix();
        String suffix = pathProperties.getSuffix();
        return new FileDestination(dirName, prefix, time, suffix);
    }

    @Override
    public void write(@NonNull HttpResponse response, @NonNull Destination<File> destination) {
        if (firstRun) {
            throw new IllegalStateException("Cannot write to file on the first run.");
        }
        File file = destination.get();
        HttpEntity entity;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))){
            entity = response.getEntity();
            entity.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getETag(@NonNull HttpResponse response) {
            return response.getFirstHeader("ETag").getValue();
    }

    @Override
    public void run() {
        final Request request;
        if (firstRun) {
            request = createRequest();
            firstRun = false;
        } else {
            request = createRequest();
        }
        Response response = getResponse(request);
        HttpResponse httpResponse;
        try {
            httpResponse = response.returnResponse();
        } catch (IOException e) {
            logger.error("Error retrieving http response.", e);
            throw new RuntimeException(e);
        }
        int statusCode = getStatusCode(httpResponse);
        this.etag = getETag(httpResponse);
        if (statusCode == OK) {
            Destination<File> destination = createDestination();
            write(httpResponse, destination);
        } else if (statusCode != NOT_MODIFIED) {
            throw new RuntimeException("Unexpected status code " + statusCode +
                                       ". Should be equivalent to OK (200) or NOT_MODIFIED (304).");
        }
    }

}
