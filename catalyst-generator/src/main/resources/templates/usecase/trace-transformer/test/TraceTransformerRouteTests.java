{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.intellecteu.catalyst.swift.converter.MTUtils.loadResource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TraceTransformerRouteTests {

    @Produce(uri = "direct:traceTransformCanonicalToMt103")
    private ProducerTemplate traceTransformationToMT103;
    
    @EndpointInject(uri = "mock:mt103TraceTransformGenerated")
    private MockEndpoint mt103EPTraceTransformed;
    
    @Autowired
    private CamelContext context;

    @Test
    public void traceTransformCanonicalToMtFlow() throws Exception {
        mt103EPTraceTransformed.reset();
        mt103EPTraceTransformed.expectedMessageCount(1);
        mt103EPTraceTransformed.expectedBodiesReceived(loadResource("MT103_Expected_Trace.fin", true));


        Exchange canonical = ExchangeBuilder.anExchange(context)
                .withBody(loadResource("canonical103.xml", true))
                .build();

        traceTransformationToMT103.send(canonical);

        mt103EPTraceTransformed.assertIsSatisfied();

    }

}

<%={{ }}=%>
