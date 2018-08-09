{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import <%fullPackageName%>.server.HelloCamelServer;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@UseAdviceWith
public class DemoApplicationTests {

  @Autowired
  CamelContext camelContext;

  @EndpointInject(uri = "mock:result")
  MockEndpoint mockResult;

  final String TEST_NAME = "Camel";
  final CamelHelloRequest testRequest = CamelHelloRequest.newBuilder().setName(TEST_NAME).build();
  final CamelHelloReply mockReply = CamelHelloReply.newBuilder().setMessage("Hello "+ TEST_NAME).build();

  final HelloCamelServer helloCamelServer = new HelloCamelServer();

  Processor testProcessor = exchange -> exchange.getIn().setBody(testRequest, CamelHelloRequest.class);

  @Before
  public void init() throws Exception {
    helloCamelServer.start();

    camelContext.getRouteDefinition("grpc-test").adviceWith(camelContext,
        new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            weaveById("processor").replace().process(testProcessor);
            weaveAddLast().to("mock:result");
          }
        });
    camelContext.start();
  }

  @Test
  public void runTest() throws InterruptedException {
    mockResult.expectedMessageCount(1);
    mockResult.message(0).body().isEqualTo(mockReply);
    mockResult.assertIsSatisfied();
  }

  @After
  public void destroy() {
    helloCamelServer.stop();
  }
}
