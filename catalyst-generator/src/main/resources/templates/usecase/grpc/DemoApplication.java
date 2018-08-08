{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import <%fullPackageName%>.server.HelloCamelServer;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    SpringApplication.run(DemoApplication.class, args);
    HelloCamelServer.main(args);
  }

}
