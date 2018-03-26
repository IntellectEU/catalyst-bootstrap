{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel routes for REST consumer
 * 
 */
@Component
public class Rest2DirectRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {

      onException(Exception.class)
          // Insert general error handling
          .log(LoggingLevel.ERROR,"Root Exception Handler - Caught unhandled exception ${exception.message}");

      onException(IllegalArgumentException.class)
          .handled(true) // Prevent Camel error handlers to process exception, as we handle it ourselves
          // Insert specific error handling
          .log("Specific exception handler")
          .to("log:error?showCaughtException=true&showStackTrace=true");

        rest("/{{rest.base.path}}/")
                .get("/{id}")
                    // TODO: route
                    .to("direct:customerDetail")
                .get("/{id}/orders")
                     // TODO: route
                    .to("direct:customerOrders");
    }
}

<%={{ }}=%>
