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
public class SwiftFinTransformationRoutesTest {
    
    @Produce(uri = "direct:canonicalToMt103")
    private ProducerTemplate transformationToMT103;
    
    @Produce(uri = "direct:Mt103ToCanonical")
    private ProducerTemplate transformationFromMT103;

    @EndpointInject(uri = "mock:mt103Generated")
    private MockEndpoint mt103EP;

    @EndpointInject(uri = "mock:canonicalGenerated")
    private MockEndpoint canonicalEP;

    @Autowired
    private CamelContext context;
    
    
    @Test
    public void canonicalToMtFlow() throws Exception {
        mt103EP.reset();
        mt103EP.expectedMessageCount(1);
        mt103EP.expectedBodiesReceived(loadResource("MT103_Expected.fin", true));

        Exchange canonical = ExchangeBuilder.anExchange(context)
                .withBody(loadResource("canonical103.xml", true))
                .build();

        transformationToMT103.send(canonical);

        mt103EP.assertIsSatisfied();
    }

    @Test
    public void mtToCanonicalFlow() throws Exception {
        canonicalEP.reset();
        canonicalEP.expectedMessageCount(1);
        canonicalEP.expectedBodiesReceived(loadResource("canonicalFrom103.xml"));

        Exchange mt103 = ExchangeBuilder.anExchange(context)
                .withBody(loadResource("MT103_Expected.fin", true))
                .build();

        transformationFromMT103.send(mt103);

        canonicalEP.assertIsSatisfied();
    }

}

<%={{ }}=%>
