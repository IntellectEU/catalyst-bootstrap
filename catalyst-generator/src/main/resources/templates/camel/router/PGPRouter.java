{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sample Camel route to demonstrate encryption and decryption using PGP
 */
@Component
public class PGPRouter extends RouteBuilder {


  @Override
  public void configure() {
    from("timer:name?period=1000")
        .setHeader("messageId", simple("random(10)", String.class))
        .log("Message identifier: ${header.messageId}")
        .log("Processing duplicate message with id: ${header.messageId}")
        .to("mock:duplicate")
  }
}

<%={{ }}=%>