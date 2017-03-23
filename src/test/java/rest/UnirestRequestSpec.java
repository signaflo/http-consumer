package rest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.mashape.unirest.request.GetRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import rest.RestRequest;
import rest.UnirestRequest;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class UnirestRequestSpec {

    private GetRequest getRequest = mock(GetRequest.class);
    private String simpleJsonString = "{"+
            "    \"id\": 1,"+
            "    \"name\": \"A green door\","+
            "    \"price\": 12.50,"+
            "    \"tags\": [\"home\", \"green\"]"+
            "}";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenInstantiatedThenUnirestWrapped() {
        UnirestRequest<JsonNode> request = new UnirestRequest<>(getRequest, JsonNode.class);
        assertThat(request.getGetRequest(), is(instanceOf(GetRequest.class)));
    }

    @Test
    public void whenNullGetRequestThenNPE() {
        exception.expect(NullPointerException.class);
        new UnirestRequest<>(null, JsonNode.class);
    }

    @Test
    public void whenUnirestExceptionThenRuntimeException() throws Exception {
        doThrow(UnirestException.class).when(getRequest).asObject(JsonNode.class);
        exception.expect(RuntimeException.class);
        RestRequest<JsonNode> request = new UnirestRequest<>(getRequest, JsonNode.class);
        request.makeRequest();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenGetJsonStringThenJsonReturned() throws Exception {
        HttpResponse<JsonNode> response = mock(HttpResponse.class);
        JsonNode jsonNode = mock(JsonNode.class);
        when(getRequest.asObject(JsonNode.class)).thenReturn(response);
        when(response.getBody()).thenReturn(jsonNode);
        when(jsonNode.toString()).thenReturn(simpleJsonString);
        RestRequest<JsonNode> request = new UnirestRequest<>(getRequest, JsonNode.class);
        assertThat(request.makeRequest().getBodyAsString(), is(simpleJsonString));
    }
}
