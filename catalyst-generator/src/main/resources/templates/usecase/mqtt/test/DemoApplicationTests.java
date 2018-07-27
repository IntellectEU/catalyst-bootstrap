{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import static <%packageName%>.Router.HOST;
import static <%packageName%>.Router.TOPIC_NAME;

import <%packageName%>.model.CarData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@UseAdviceWith
public class DemoApplicationTests {

  @ClassRule
  public static GenericContainer mosquitto = new GenericContainer("eclipse-mosquitto")
      .withExposedPorts(1883);
  String mosquittoUrl;

  private ObjectMapper objectMapper = new ObjectMapper();

  @EndpointInject(uri = "mock:result")
  MockEndpoint mockResult;

  @Autowired
  CamelContext camelContext;

  @Autowired
  ProducerTemplate producerTemplate;

  @Before
  public void init() throws Exception {

    mosquitto.start();

    mosquittoUrl =
        "tcp://" + mosquitto.getContainerIpAddress() + ":" + mosquitto.getMappedPort(1883);

    camelContext.getRouteDefinition("toMqttRoute").adviceWith(camelContext,
        new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            replaceFromWith("direct:in");
            weaveByToUri("mqtt:mosquitoTo?host=tcp://" + HOST + "&publishTopicName=" + TOPIC_NAME)
                .replace().to("mqtt:testTo?host=" + mosquittoUrl + "&publishTopicName=carData");
            weaveById("fillMqttProcessor").remove();
          }
        });

    camelContext.getRouteDefinition("fromMqttRoute").adviceWith(camelContext,
        new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            replaceFromWith("mqtt:testFrom?host=" + mosquittoUrl + "&subscribeTopicNames=carData");
          }
        });

    camelContext.start();
  }

  @Test
  public void runTest() throws Exception {
    List<CarData> list = new ArrayList<>();
    list.add(new CarData(1, 228));
    String listInJson = objectMapper.writeValueAsString(list);

    producerTemplate.sendBody("direct:in", listInJson);

    mockResult.expectedMessageCount(1);
    mockResult.message(0).body().isEqualTo(listInJson);
    mockResult.assertIsSatisfied();
  }

  @After
  public void destroy() throws Exception {
    camelContext.stop();
  }

}
