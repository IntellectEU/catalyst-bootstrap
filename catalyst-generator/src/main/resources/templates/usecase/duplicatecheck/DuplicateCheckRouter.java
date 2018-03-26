{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sample Camel route to demonstrate duplicate check
 */
@Component
public class DuplicateCheckRouter extends RouteBuilder {

  @Autowired
  private IdempotentRepository idempotentRepository;

  @Override
  public void configure() {

    onException(Exception.class)
        // Insert general error handling
        .log("Root Exception Handler");

    onException(IllegalArgumentException.class)
        .handled(true) // Prevent Camel error handlers to process exception, as we handle it ourselves
        // Insert specific error handling
        .log("Specific exception handler");

    from("timer:name?period=1000")
        .setHeader("messageId", simple("random(10)", String.class))
        .log("Message identifier: ${header.messageId}")
        .idempotentConsumer(header("messageId")).messageIdRepository(idempotentRepository)
        .skipDuplicate(false) // Needed to be able to mark duplicates and not just consume them
        .filter(exchangeProperty(Exchange.DUPLICATE_MESSAGE).isEqualTo(true))
          // here we process duplicates
          .log("Processing duplicate message with id: ${header.messageId}")
          .to("mock:duplicate")
          .stop()
        .end()
        // and here we process only new messages (no duplicates)
        .log("Processing original message with id: ${header.messageId}")
        .to("mock:proceed");
  }
}

<%={{ }}=%>