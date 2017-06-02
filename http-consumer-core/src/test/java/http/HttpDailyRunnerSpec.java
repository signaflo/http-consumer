package http;

import data.PathProperties;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public final class HttpDailyRunnerSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final Request request = mock(Request.class);
    private final HttpResponse response = mock(HttpResponse.class);
    private final Map<String, String> requestProperties = new HashMap<>();
    private final PathProperties pathProperties = new PathProperties("vehiclePositions", "json",
                                                                     "data");
    private final String url = "http://localhost:8080";
    private final HttpRunner<File> runner = new HttpDailyRunner(url, requestProperties, pathProperties);

    @Test
    public void whenGettingResponseFailsThenRuntimeException() throws Exception {
        when(request.execute()).thenThrow(IOException.class);
        exception.expect(RuntimeException.class);
        runner.getResponse(request);
    }

    @Test
    public void whenGetStatusCodeThenExpectedCodeReturned() {
        StatusLine statusLine = mock(StatusLine.class);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        assertThat(runner.getStatusCode(response), is(200));
    }

    @Test
    public void whenCreateDestinationThenDirectoryCorrect() {
        DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        String day  = LocalDate.now().format(DAY_FORMATTER);
        String expectedDirectory = pathProperties.getDirectory() + File.separator + day;
        assertThat(runner.createDestination().getParent(), is(expectedDirectory));
    }

    @Test
    @Ignore
    public void quickTest() throws Exception {
        final long initialDelay = 1;
        final long delay = 30;
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        List<Runnable> runners = getRunners();
        for (Runnable runner : runners) {
            executorService.scheduleWithFixedDelay(runner, initialDelay, delay, timeUnit);
        }
        Thread.sleep(5000);
    }

    private static List<Runnable> getRunners() {

        final String jsonContentType = "application/octet-stream";

        Map<String, String> requestProperties = new HashMap<>(3);
        requestProperties.put("Content-Type", jsonContentType);
        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");

        final String jsonSuffix = "json";
        final String jsonDirectory = "data" + File.separator + "json";
        final String vehiclePositionsPrefix = "vehicle-positions";
        PathProperties pathProperties = new PathProperties(vehiclePositionsPrefix, jsonSuffix,
                                                           jsonDirectory + File.separator + vehiclePositionsPrefix);
        final String vehiclePositionsJsonURL = "https://data.austintexas.gov/razz/cuc7-ywmd/text/plain";
        Runnable vehiclePositionsJsonRunner = new HttpDailyRunner(vehiclePositionsJsonURL, requestProperties,
                                                                  pathProperties);

        final String tripUpdatesPrefix = "trip-updates";
        pathProperties = new PathProperties(tripUpdatesPrefix, jsonSuffix,
                                            jsonDirectory + File.separator + tripUpdatesPrefix);
        final String tripUpdatesJsonURL = "https://data.texas.gov/download/mqtr-wwpy/text%2Fplain";
        Runnable tripUpdatesJsonRunner = new HttpDailyRunner(tripUpdatesJsonURL, requestProperties,
                                                             pathProperties);

        final String pbContentType = "application/octet-stream";
        requestProperties.put("Content-Type", pbContentType);

        final String pbSuffix = "pb";
        final String pbDirectory = "data" + File.separator + "pb";
        pathProperties = new PathProperties(vehiclePositionsPrefix, pbSuffix,
                                            pbDirectory + File.separator + vehiclePositionsPrefix);
        final String vehiclePositionsPbURL = "https://data.texas.gov/download/eiei-9rpf/application%2Foctet-stream";
        Runnable vehiclePositionPbRunner = new HttpDailyRunner(vehiclePositionsPbURL, requestProperties,
                                                               pathProperties);

        pathProperties = new PathProperties(tripUpdatesPrefix, pbSuffix,
                                            pbDirectory + File.separator + tripUpdatesPrefix);
        final String tripUpdatesPbURL = "https://data.texas.gov/download/rmk2-acnw/application%2Foctet-stream";
        Runnable tripUpdatesPbRunner = new HttpDailyRunner(tripUpdatesPbURL, requestProperties,
                                                           pathProperties);

        return Arrays.asList(vehiclePositionsJsonRunner, tripUpdatesJsonRunner,
                             vehiclePositionPbRunner, tripUpdatesPbRunner);
    }
}

