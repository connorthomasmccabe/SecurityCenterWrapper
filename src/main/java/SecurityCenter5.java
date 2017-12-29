import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.HttpHost;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SecurityCenter5
{
    private String host;

    public SecurityCenter5(String securityCenterHostName, int securityCenterPort) throws UnirestException
    {
        this.host = new HttpHost(securityCenterHostName, securityCenterPort).toHostString();
    }

    public SecurityCenter5(String securityCenterHostName, int securityCenterPort, String proxyHost, int proxyPort) throws UnirestException
    {
        this(securityCenterHostName, securityCenterPort);
        Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
    }

    public String getAuthenticationToken(String username, String password) throws UnirestException
    {
        Map<String, String> requestBody = getLogInRequestBody(username, password);
        HttpResponse<JsonNode> response = Unirest.post(getRoute("token")).body(new JSONObject(requestBody)).asJson();
        checkForNon200ResponseCode(response);
        return response.getBody().getObject().getJSONObject("response").get("token").toString();
    }

    public HttpResponse<JsonNode> processRequest(String resource, JsonNode requestBody, String authToken, HttpMethod httpMethod) throws UnirestException
    {
        HttpRequest request = new HttpRequestWithBody(httpMethod, getRoute(resource))
                .headers(getRequestHeaders(authToken))
                .body(requestBody)
                .getHttpRequest();
        HttpResponse<JsonNode> response = request.asJson();
        checkForNon200ResponseCode(response);
        return response;
    }

    private Map<String, String> getLogInRequestBody(String username, String password)
    {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("releaseSession", "false");
        return requestBody;
    }

    private Map<String,String> getRequestHeaders(String authToken)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-SecurityCenter", authToken);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private void checkForNon200ResponseCode(HttpResponse<JsonNode> response)
    {
        if(response.getStatus() != 200)
        {
            throw new RuntimeException(String.format("Non 200 Response Code Received.\n" +
                    "Response Code: %s\n" +
                    "Response Status: %s\n" +
                    "Response Body: %s", response.getStatus(), response.getStatusText(), response.getBody().toString()));
        }
    }

    private String getRoute(String route)
    {
        return String.format("%s/rest/%s", host, route);
    }
}
