{{=<% %>=}}

<%license%>

package <%fullPackageName%>;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TraceTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:traceTransformCanonicalToMt103")
                .log("Start transforming canonical message to MT103...")
                .to("txfrmr:com.intellecteu.transformations/transformCanonicalToMT103")
                .log("Transformation done.")
                .log("\n***Created MT: \n${body}")
                .to("mock:mt103TraceTransformGenerated");
    }
}

<%={{ }}=%>
