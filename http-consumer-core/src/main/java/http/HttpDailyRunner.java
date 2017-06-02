package http;

import data.FileUtils;
import data.PathProperties;
import lombok.NonNull;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
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

    HttpDailyRunner(String uri, Map<String, String> requestProperties, PathProperties pathProperties) {
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
        for (String key : requestProperties.keySet()) {
            request.addHeader(key, requestProperties.get(key));
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
    public File createDestination() {
        String time = LocalTime.now().format(TIME_FORMATTER);
        String day  = LocalDate.now().format(DAY_FORMATTER);
        String dirName = pathProperties.getDirectory() + File.separator + day;
        String prefix = pathProperties.getPrefix();
        String suffix = pathProperties.getSuffix();
        return FileUtils.createFile(dirName, prefix, time, suffix);
    }

    @Override
    public void write(@NonNull HttpResponse response, @NonNull File file) {
        if (this.firstRun) {
            throw new IllegalStateException("Cannot write to file on the first run.");
        }
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))){
            HttpEntity entity = response.getEntity();
            entity.writeTo(outputStream);
        } catch (IOException e) {
            logger.error("IOException when attempting to write response to " + file.toString(), e);
            throw new RuntimeException(e);
        }
    }

    private static String getETag(@NonNull HttpMessage response) {
        Header header = response.getFirstHeader("ETag");
        if (header == null) {
            return "";
        }
        return header.getValue();
    }

    @Override
    public void run() {
        int maxAttempts = 5; //TODO: Consider making static or retrieving from external source.
        for (int i = 0; i <= maxAttempts; i++) {
            try {
                execute();
            } catch (RuntimeException e) {
                long waitMillis = 1000 * (i + 1); //TODO: Consider making static or retrieving from external source.
                logRunException(maxAttempts, i, e, waitMillis);
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException ie) {
                    logger.error("{} thread interrupted.", this.getClass().getCanonicalName(), ie);
                }
            }
        }
    }

    private static void logRunException(int maxAttempts, int currentAttempt, RuntimeException e, long waitMillis) {
        if (currentAttempt == maxAttempts) {
            logger.error("Maximum attempts, {}, exceeded. Execution has failed.", maxAttempts, e);
            throw e;
        } else {
            logger.error("Failed attempt. Retry #{} in {} seconds.", (currentAttempt + 1), waitMillis / 1000.0, e);
        }
    }

    private void execute() {
        final Request request = getRequest();
        Response response = getResponse(request);
        HttpResponse httpResponse = extractHttpResponse(response);
        int statusCode = getStatusCode(httpResponse);
        this.etag = getETag(httpResponse);
        writeResponseToDestination(httpResponse, statusCode);
    }

    private void writeResponseToDestination(HttpResponse httpResponse, int statusCode) {
        if (statusCode == OK) {
            File destination = createDestination();
            write(httpResponse, destination);
        } else if (statusCode != NOT_MODIFIED) {
            throw new RuntimeException("Unexpected status code " + statusCode + ". Status should be " +
                                       "equivalent to OK (200) or NOT_MODIFIED (304).");
        }
    }

    private Request getRequest() {
        final Request request;
        if (this.firstRun) {
            request = createRequest();
            this.firstRun = false;
        } else {
            request = createRequest();
        }
        return request;
    }

    private static HttpResponse extractHttpResponse(Response response) {
        HttpResponse httpResponse;
        try {
            httpResponse = response.returnResponse();
        } catch (IOException e) {
            logger.error("Error retrieving http response.");
            throw new RuntimeException(e);
        }
        return httpResponse;
    }

}
