package http.data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import http.HttpRequest;
import http.HttpResponse;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpSourceSpec {

    private String url = "http://localhost:8080";
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSource source;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    @SuppressWarnings("unchecked")
    public void beforeMethod() {
        httpRequest = mock(HttpRequest.class);
        httpResponse = mock(HttpResponse.class);
        source = new HttpSource(url, "text/plain; charset=UTF-8");
    }

    @Test
    public void whenUpdateWithNullRestResponseThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(null);
        source.updateWith(httpRequest);
        assertThat(source.getEtag(), is(""));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseThenEtagUpdated() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(200);
        when(httpResponse.getHeaderField("ETag")).thenReturn(Collections.singletonList("abc"));
        source.updateRestResponse(httpRequest);
        source.updateWith(httpRequest);
        assertThat(source.getEtag(), is("abc"));
    }

    @Test
    public void whenUpdateWithNonNullRestResponseAndGetStatusNotModfiedThenEtagEmptyString() {
        //final String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(304);
        source.updateRestResponse(httpRequest);
        source.updateWith(httpRequest);
        assertThat(source.getEtag(), is(""));
    }

    @Test
    public void whenUpdateEtagWithNullHeaderFieldThenException() {
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(200);
        source.updateRestResponse(httpRequest);
        exception.expect(RuntimeException.class);
        source.updateETag();
    }

    @Test
    public void whenUpdateEtagWithEmptyETagHeaderThenException() {
        when(httpResponse.getHeaderField("ETag")).thenReturn(Collections.emptyList());
        when(httpRequest.makeRequest()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(200);
        source.updateRestResponse(httpRequest);
        exception.expect(RuntimeException.class);
        source.updateETag();
    }

    @Test
    public void whenUpdateWithNullRestRequestThenNPE() {
        httpRequest = null;
        exception.expect(NullPointerException.class);
        source.updateWith(httpRequest);
    }

    @Test
    public void whenInsantiatedWithNullURLThenNPE() {
        String url = null;
        exception.expect(NullPointerException.class);
        new HttpSource(url, "text/plain; charset=UTF-8");
    }
}
