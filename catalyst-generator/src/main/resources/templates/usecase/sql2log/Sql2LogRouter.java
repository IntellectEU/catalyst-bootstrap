{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


/**
 * Camel routes for sql connectors
 * 
 */
@Component
public class Sql2LogRouter extends RouteBuilder {
    
    @Override
    public void configure() {

      onException(Exception.class)
          // Insert general error handling
          .log(LoggingLevel.ERROR,"Root Exception Handler - Caught unhandled exception ${exception.message}");

      onException(IllegalArgumentException.class)
          .handled(true) // Prevent Camel error handlers to process exception, as we handle it ourselves
          // Insert specific error handling
          .log("Specific exception handler")
          .to("log:error?showCaughtException=true&showStackTrace=true");

        // Consume files from sql.query
        from("sql:{{catalyst.sql.query}}?onConsumeBatchComplete={{catalyst.sql.onConsume}}").id("ReadSQlRoute")
                .autoStartup("{{catalyst.sql.enabled}}")
                
                .log("Received data from [{{catalyst.sql.query}}].")
                
                .log("TODO: Implement your routing");
    }
}
<%={{ }}=%>
