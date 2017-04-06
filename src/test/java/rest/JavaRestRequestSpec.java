package rest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class JavaRestRequestSpec {

    private String simpleJsonString = "{"+
            "    \"id\": 1,"+
            "    \"name\": \"A green door\","+
            "    \"price\": 12.50,"+
            "    \"tags\": [\"home\", \"green\"]"+
            "}";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenNullConnectionThenNPE() {
        exception.expect(NullPointerException.class);
        new JavaRestRequest(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenGetJsonStringThenJsonReturned() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(simpleJsonString.getBytes());
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(inputStream);
        when(connection.getContentLength()).thenReturn(simpleJsonString.getBytes().length);
        RestRequest request = new JavaRestRequest(connection);
        assertThat(request.makeRequest().getBodyAsString(), is(simpleJsonString));
    }
}
