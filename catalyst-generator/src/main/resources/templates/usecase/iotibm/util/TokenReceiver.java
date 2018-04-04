{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import static <%packageName%>.config.Constants.REST_API_ADDRESS;

import <%packageName%>.service.BlockchainConnector;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenReceiver implements InitializingBean {

  private String token;

  @Autowired
  private BlockchainConnector blockchainConnector;

  @PostConstruct
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
