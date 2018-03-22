{{=<% %>=}}

<%license%>

package <%packageName%>;

import java.io.IOException;
import org.springframework.http.ResponseEntity;

public interface BlockchainConnector {


  void invoke(String host, String channel, String chaincode, String function, String objectInJson);

  <T> ResponseEntity<T> query(String host, String channel, String chaincode,
      String function, String owner, String peer, String objectInJson, Class<T> type);

  String enroll(String host) throws IOException;
}
