{{=<% %>=}}

<%license%>

package <%packageName%>;

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
    from("direct:inbox")
        .log("Message identifier: ${header.messageId}")
        .idempotentConsumer(header("messageId"))
        .messageIdRepository(idempotentRepository)
        .skipDuplicate(true)
        // Needed to be able to process duplicates separately and not just consume them
        .filter(exchangeProperty(Exchange.DUPLICATE_MESSAGE).isEqualTo(true))
        // filter out duplicate messages by sending them to someplace else and then stop
        .log("Processing duplicate message with id: ${header.messageId}")
        .to("mock:duplicate")
        .stop()
        .end()
        // and here we process only new messages (no duplicates)
        .to("mock:proceed");
  }
}

<%={{ }}=%>