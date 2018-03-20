{{=<% %>=}}

<%license%>

package <%packageName%>;

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
        // Consume files from sql.query
        from("sql:{{sql.query}}?onConsumeBatchComplete={{sql.onConsume}}").id("ReadSQlRoute")
                .autoStartup("{{sql.enabled}}")
                
                .log("Received data from [{{sql.query}}].")
                
                .log("TODO: Implement your routing");
    }
}
<%={{ }}=%>
