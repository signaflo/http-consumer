package rest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
public class ConsumerSpec {

    private String url = "http://localhost:8080";
    private RestRequest restRequest;
    private RestResponse restResponse;
    private Consumer consumer;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    @SuppressWarnings("unchecked")
    public void beforeMethod() {
        restRequest = mock(RestRequest.class);
        restResponse = mock(RestResponse.class);
        consumer = new Consumer(url);
    }

    @Test
    public void whenUpdateWithNullRestResponseThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(restRequest.makeRequest()).thenReturn(null);
        consumer.updateWith(restRequest);
        assertThat(consumer.getEtag(), is(""));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseThenEtagUpdated() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(restRequest.makeRequest()).thenReturn(restResponse);
        when(restResponse.getHeaderField("ETag")).thenReturn(Collections.singletonList("abc"));
        consumer.updateRestResponse(restRequest);
        consumer.updateWith(restRequest);
        assertThat(consumer.getEtag(), is("abc"));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseAndGetStatusNotModfiedThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(restRequest.makeRequest()).thenReturn(restResponse);
        when(restResponse.getStatus()).thenReturn(304);
        consumer.updateRestResponse(restRequest);
        consumer.updateWith(restRequest);
        assertThat(consumer.getEtag(), is(""));
    }

    @Test
    public void whenUpdateEtagWithNullHeaderFieldThenException() {
        when(restRequest.makeRequest()).thenReturn(restResponse);
        consumer.updateRestResponse(restRequest);
        exception.expect(RuntimeException.class);
        consumer.updateETag();
    }

    @Test
    public void whenUpdateEtagWithEmptyETagHeaderThenException() {
        when(restResponse.getHeaderField("ETag")).thenReturn(Collections.emptyList());
        when(restRequest.makeRequest()).thenReturn(restResponse);
        consumer.updateRestResponse(restRequest);
        exception.expect(RuntimeException.class);
        consumer.updateETag();
    }

    @Test
    public void whenUpdateWithNullRestRequestThenNPE() {
        restRequest = null;
        exception.expect(NullPointerException.class);
        consumer.updateWith(restRequest);
    }

    @Test
    public void whenInsantiatedWithNullURLThenNPE() {
        String url = null;
        exception.expect(NullPointerException.class);
        new Consumer(url);
    }

    @Test
    public void whenRunThenNoErrors() throws Exception {
        Consumer consumer = new Consumer(new URL(url));
        consumer.run();
    }

    @Test
    @Ignore
    public void quickTest() throws Exception {
        url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        Consumer consumer = new Consumer(url);
        consumer.run();
        Thread.sleep(15000L);
        consumer.run();
        Thread.sleep(15000L);
        consumer.run();
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleWithFixedDelay(repeatRequest, 1, 15, TimeUnit.SECONDS);
//        Thread.sleep(100 * 1000);
    }
}

