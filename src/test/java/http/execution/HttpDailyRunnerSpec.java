package http.execution;

import http.data.PathProperties;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        HttpDailyRunner runner = new HttpDailyRunner(url, requestProperties, pathProperties);
        exception.expect(RuntimeException.class);
        runner.getResponse(request);
    }

    @Test
    @Ignore
    public void quickTest() throws Exception {
        String url = "https://data.texas.gov/download/cuc7-ywmd/text/plain";
        //String url = "https://data.texas.gov/download/eiei-9rpf/application/octet-stream";
        String contentType = "text/plain; charset=UTF-8";
        Map<String, String> requestProperties = new HashMap<>(3);
        requestProperties.put("Content-Type", contentType);
        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");
        HttpDailyRunner runner = new HttpDailyRunner(url, requestProperties, pathProperties);
        runner.run();
    }
}

