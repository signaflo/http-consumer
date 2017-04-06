package rest;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class JavaRestResponseSpec {

    private HttpURLConnection connection = mock(HttpURLConnection.class);

    @Before
    public void beforeMethod() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        when(connection.getInputStream()).thenReturn(inputStream);
        when(connection.getContentLength()).thenReturn(0);
    }

    @Test
    public void whenJavaRestResponseThenStatusCode() throws Exception {
        when(connection.getResponseCode()).thenReturn(304);
        RestResponse restResponse = new JavaRestResponse(connection);
        assertThat(restResponse.getStatus(), is(304));
    }

    @Test
    public void whenGetHeaderFieldThenCorrectListReturned() {
        List<String> contentType = Collections.singletonList("text/html");
        when(connection.getHeaderField("Content-Type")).thenReturn("text/html");
        RestResponse restResponse = new JavaRestResponse(connection);
        assertThat(restResponse.getHeaderField("Content-Type"), is(contentType));
    }

    @Test
    public void whenGetBodyAsStringNullBodyThenEmptyString() throws Exception {
        RestResponse restResponse = new JavaRestResponse(connection);
        assertThat(restResponse.getBodyAsString(), is(""));
    }
}
