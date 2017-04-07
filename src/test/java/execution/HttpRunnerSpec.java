package execution;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import rest.HttpRequest;
import rest.HttpResponse;

import java.net.URL;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public class HttpRunnerSpec {

    private String url = "http://localhost:8080";
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpRunner runner;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    @SuppressWarnings("unchecked")
    public void beforeMethod() {
        httpRequest = mock(HttpRequest.class);
        httpResponse = mock(HttpResponse.class);
        runner = new HttpRunner(url);
    }

    @Test
    public void whenUpdateWithNullRestResponseThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(null);
        runner.updateWith(httpRequest);
        assertThat(runner.getEtag(), is(""));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseThenEtagUpdated() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getHeaderField("ETag")).thenReturn(Collections.singletonList("abc"));
        runner.updateRestResponse(httpRequest);
        runner.updateWith(httpRequest);
        assertThat(runner.getEtag(), is("abc"));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseAndGetStatusNotModfiedThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(304);
        runner.updateRestResponse(httpRequest);
        runner.updateWith(httpRequest);
        assertThat(runner.getEtag(), is(""));
    }

    @Test
    public void whenUpdateEtagWithNullHeaderFieldThenException() {
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        runner.updateRestResponse(httpRequest);
        exception.expect(RuntimeException.class);
        runner.updateETag();
    }

    @Test
    public void whenUpdateEtagWithEmptyETagHeaderThenException() {
        when(httpResponse.getHeaderField("ETag")).thenReturn(Collections.emptyList());
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        runner.updateRestResponse(httpRequest);
        exception.expect(RuntimeException.class);
        runner.updateETag();
    }

    @Test
    public void whenUpdateWithNullRestRequestThenNPE() {
        httpRequest = null;
        exception.expect(NullPointerException.class);
        runner.updateWith(httpRequest);
    }

    @Test
    public void whenInsantiatedWithNullURLThenNPE() {
        String url = null;
        exception.expect(NullPointerException.class);
        new HttpRunner(url);
    }

    @Test
    public void whenRunThenNoErrors() throws Exception {
        HttpRunner runner = new HttpRunner(new URL(url));
        runner.run();
    }

    @Test
    @Ignore
    public void quickTest() throws Exception {
        url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        HttpRunner runner = new HttpRunner(url);
        runner.run();
        Thread.sleep(15000L);
        runner.run();
        Thread.sleep(15000L);
        runner.run();
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleWithFixedDelay(repeatRequest, 1, 15, TimeUnit.SECONDS);
//        Thread.sleep(100 * 1000);
    }
}

