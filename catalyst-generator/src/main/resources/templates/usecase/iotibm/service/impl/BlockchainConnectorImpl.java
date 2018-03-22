{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import static <%packageName%>.config.Constants.AUTHORIZATION_HEADER;
import static <%packageName%>.config.Constants.BEAR_HEADER;

import <%packageName%>.config.Constants;
import <%packageName%>.exception.TokenReceiverException;
import <%packageName%>.service.BlockchainConnector;
import <%packageName%>.util.TokenReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BlockchainConnectorImpl implements BlockchainConnector {

  @Autowired
  private TokenReceiver tokenReceiver;

  @Autowired
  private ObjectMapper objectMapper;

  private RestTemplate restTemplate = new RestTemplate();


  @Override
  public void invoke(String host, String channel, String chaincode, String function,
      String objectInJson) {
    String token = tokenReceiver.getToken();
    if (token == null || token.isEmpty()) {
      throw new TokenReceiverException("token is null or empty");
    }

    final String url = host +
        "/channels/" + channel +
        "/chaincodes/" + chaincode;

    if (objectInJson != null && !objectInJson.isEmpty()) {
      objectInJson = objectInJson.replace("\"", "\\\"");
    }
    final String input = "{\"fcn\":\"" + function + "\",\"args\":[\"" + objectInJson + "\"]}";

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set(AUTHORIZATION_HEADER, BEAR_HEADER + " " + token);

    HttpEntity<String> entity = new HttpEntity<>(input, httpHeaders);
    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
  }

  @Override
  public <T> ResponseEntity<T> query(String host, String channel, String chaincode,
      String function, String owner, String peer, String objectInJson, Class<T> type) {
    String token = tokenReceiver.getToken();
    if (token == null || token.isEmpty()) {
      throw new TokenReceiverException("token is null or empty");
    }

    if (objectInJson != null && !objectInJson.isEmpty()) {
      objectInJson = objectInJson.replace("\"", "\\\"");
    }

    final String url = host +
        "/channels/" + channel +
        "/chaincodes/" + chaincode +
        "?args=[" + objectInJson + "]" +
        "&fcn=" + function +
        "&peer=" + owner +
        "/" + peer;

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(AUTHORIZATION_HEADER, BEAR_HEADER + " " + token);
    HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
    return restTemplate.exchange(url, HttpMethod.GET, entity, type);

  }

  @Override
  public String enroll(String host) throws IOException {
    final String url = host + "/users";
    final String input = "{\"username\":\"admin\"}";

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(input, httpHeaders);

    ResponseEntity<String> result = restTemplate.
        exchange(url, HttpMethod.POST, entity, String.class);

    return (String) objectMapper.readValue(result.getBody(), HashMap.class)
        .get(Constants.TOKEN);
  }
}

<%={{ }}=%>