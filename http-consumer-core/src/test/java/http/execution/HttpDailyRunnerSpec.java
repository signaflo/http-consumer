package http.execution;

import http.data.PathProperties;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpDailyRunnerSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    Request request = mock(Request.class);
    String url = "http://localhost:8080";
    Map<String, String> requestProperties = new HashMap<>();
    PathProperties pathProperties = new PathProperties("vehiclePositions", "json", "data");

    @Test
    public void whenGettingResponseFailsThenRuntimeException() throws Exception {
        when(request.execute()).thenThrow(IOException.class);
        HttpRunner runner = new HttpDailyRunner(url, requestProperties, pathProperties);
        exception.expect(RuntimeException.class);
        runner.getResponse(request);
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

        final String vehiclePositionsJsonURL = "https://data.austintexas.gov/razz/cuc7-ywmd/text/plain";
        final String tripUpdatesJsonURL = "https://data.texas.gov/download/mqtr-wwpy/text%2Fplain";
        final String vehiclePositionsPbURL = "https://data.texas.gov/download/eiei-9rpf/application%2Foctet-stream";
        final String tripUpdatesPbURL = "https://data.texas.gov/download/rmk2-acnw/application%2Foctet-stream";

        final String vehiclePositionsPrefix = "vehicle-positions";
        final String tripUpdatesPrefix = "trip-updates";
        final String jsonDirectory = "data" + File.separator + "json";
        final String pbDirectory = "data" + File.separator + "pb";
        final String jsonSuffix = "json";
        final String pbSuffix = "pb";
        final String jsonContentType = "application/octet-stream";
        final String pbContentType = "application/octet-stream";

        Map<String, String> requestProperties = new HashMap<>(3);
        requestProperties.put("Content-Type", jsonContentType);
        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");

        PathProperties pathProperties = new PathProperties(vehiclePositionsPrefix, jsonSuffix,
                                                           jsonDirectory + File.separator + vehiclePositionsPrefix);
        Runnable vehiclePositionsJsonRunner = new HttpDailyRunner(vehiclePositionsJsonURL, requestProperties,
                                                                  pathProperties);

        pathProperties = new PathProperties(tripUpdatesPrefix, jsonSuffix,
                                            jsonDirectory + File.separator + tripUpdatesPrefix);
        Runnable tripUpdatesJsonRunner = new HttpDailyRunner(tripUpdatesJsonURL, requestProperties,
                                                             pathProperties);

        requestProperties.put("Content-Type", pbContentType);

        pathProperties = new PathProperties(vehiclePositionsPrefix, pbSuffix,
                                            pbDirectory + File.separator + vehiclePositionsPrefix);
        Runnable vehiclePositionPbRunner = new HttpDailyRunner(vehiclePositionsPbURL, requestProperties,
                                                               pathProperties);

        pathProperties = new PathProperties(tripUpdatesPrefix, pbSuffix,
                                            pbDirectory + File.separator + tripUpdatesPrefix);
        Runnable tripUpdatesPbRunner = new HttpDailyRunner(tripUpdatesPbURL, requestProperties,
                                                           pathProperties);

        return Arrays.asList(vehiclePositionsJsonRunner/*, tripUpdatesJsonRunner,
                             vehiclePositionPbRunner, tripUpdatesPbRunner*/);
    }
}

