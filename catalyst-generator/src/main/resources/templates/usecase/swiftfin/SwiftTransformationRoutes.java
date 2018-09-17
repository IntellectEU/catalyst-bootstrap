{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwiftTransformationRoutes extends RouteBuilder {
    private CanonicalToMt103 canonicalToMt103;
    private Mt103ToCanonical mt103ToCanonical;

    @Autowired
    public SwiftTransformationRoutes(CanonicalToMt103 canonicalToMt103, Mt103ToCanonical mt103ToCanonical) {

        this.canonicalToMt103 = canonicalToMt103;
        this.mt103ToCanonical = mt103ToCanonical;
    }

    @Override
    public void configure() throws Exception {
        DataFormat canonical = new JaxbDataFormat("catalyst.example.canonical");
        
        from("direct:canonicalToMt103")
                .log("Unmarshalling canonical...")
                .unmarshal(canonical)

                .log("Converting canonical message to MT103...")

                .bean(canonicalToMt103)
                .log("Transformation done")

                .log(LoggingLevel.DEBUG, "\n***Created MT: \n${body}")
                .to("mock:mt103Generated");

        from("direct:Mt103ToCanonical")
                .log("Converting MT103 to canonical message...")

                .bean(mt103ToCanonical)
                .log("Transformation done.")

                .log("Marshalling canonical...")
                .marshal(canonical)

                .log(LoggingLevel.DEBUG, "\n***Created canonical: \n${body}")
                .to("mock:canonicalGenerated");
    }
}

<%={{ }}=%>