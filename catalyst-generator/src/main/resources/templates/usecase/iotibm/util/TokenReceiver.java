{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import static com.catalyst.cardemo.config.Constants.REST_API_ADDRESS;

import <%packageName%>.service.BlockchainConnector;
import java.io.IOException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class TokenReceiver implements InitializingBean {

  private String token;
  private BlockchainConnector blockchainConnector;

  public TokenReceiver(BlockchainConnector blockchainConnector) {
    this.blockchainConnector = blockchainConnector;
  }

  public void initIt() throws IOException {
    String host = System.getenv(REST_API_ADDRESS);
    if (host != null && !host.isEmpty()) {
      this.token = blockchainConnector.enroll(host);
    }
  }

  public String getToken() {
    return token;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initIt();
  }
}

<%={{ }}=%>