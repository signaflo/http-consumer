package rest;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class UnirestResponseSpec {

    @SuppressWarnings("unchecked")
    private HttpResponse<JsonNode> response = mock(HttpResponse.class);

    @Test
    public void whenUnirestResponseThenStatusCode() {
        when(response.getStatus()).thenReturn(304);
        RestResponse restResponse = new UnirestResponse<>(response);
        assertThat(restResponse.getStatus(), is(304));
    }

    @Test
    public void whenGetHeaderFieldThenCorrectListReturned() {
        List<String> contentType = Collections.singletonList("text/html");
        Headers headers = new Headers();
        headers.put("Content-Type", contentType);
        when(response.getHeaders()).thenReturn(headers);
        RestResponse restResponse = new UnirestResponse<>(response);
        assertThat(restResponse.getHeaderField("Content-Type"), is(contentType));
    }

    @Test
    public void whenGetBodyAsStringNullBodyThenEmptyString() {
        when(response.getBody()).thenReturn(null);
        RestResponse restResponse = new UnirestResponse<>(response);
        assertThat(restResponse.getBodyAsString(), is(""));
    }
}
