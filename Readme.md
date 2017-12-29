# SecurityCenter5 Wrapper

### Example of updating SecurityCenter Asset with Wrapper


```java
public class SecurityCenterAssetUpdate
{
    public static void main(String[] args) throws UnirestException
    {
        SecurityCenter5 sc5 = new SecurityCenter5("https://securitycenter.com", 443);
        String authToken = sc5.getAuthenticationToken("someuser", "somepassword");
        Map<String, String> requestbody = new HashMap<>();
        requestbody.put("definedIPs", "10.1.1.1, 10.2.2.2, 10.3.3.3");
        Gson gson = new Gson();
        String json = gson.toJson(requestbody);
        HttpResponse<JsonNode> response = sc5.processRequest("asset/12345", new JsonNode(json), authToken, HttpMethod.PATCH);
    }
}
```
