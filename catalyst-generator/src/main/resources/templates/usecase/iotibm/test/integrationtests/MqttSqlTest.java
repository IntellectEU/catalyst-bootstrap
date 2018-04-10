{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import <%packageName%>.<%mavenParentArtifactId%>Application;
import <%packageName%>.TestProfileContextConfiguration;
import <%packageName%>.domain.CarData;
import <%packageName%>.routes.MqttSqlRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringBootRunner.class)
@UseAdviceWith
@ContextConfiguration(classes = {TestProfileContextConfiguration.class, MqttSqlRoutes.class,
    CamelAutoConfiguration.class})
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MqttSqlTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  SqlComponent carServiceSqlComponent;

  @Autowired
  CamelContext context;

  @EndpointInject(uri = "mock:in")
  MockEndpoint mockEndpoint;

  @EndpointInject(uri = "mock:mqttResult")
  MockEndpoint mockMqttResult;

  @EndpointInject(uri = "mock:afterConvert")
  MockEndpoint mockEnd;

  @EndpointInject(uri = "mock:beforePutInProcess")
  MockEndpoint mockBeforePutInProcess;

  @Autowired
  private ProducerTemplate producerTemplate;


  @Before
  public void init() throws Exception {
    context.getRouteDefinition("mqttRoute").adviceWith(context,
        new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            interceptSendToEndpoint(
                "mqtt:mosquitoTo?host={{catalyst.iotibm.mosquitto.host}}&publishTopicName={{catalyst.iotibm.mqtt.topic}}")
                .skipSendToOriginalEndpoint()
                .to("mock:mock");
          }
        });

    context.getRouteDefinition("mqttEnrichSqlRoute")
        .adviceWith(context, new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            replaceFromWith("direct:in");
            weaveByToUri("seda:toLedger").before().to("mock:mqttResult");
          }
        });

    context.getRouteDefinition("fromMqttToLedger").adviceWith(context,
        new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
            interceptFrom()
                .to("mock:in");
            weaveById("convertProcess").after().to("mock:afterConvert");
            weaveById("putInProcess").after().to("mock:beforePutInProcess");
          }
        });

    context.addComponent("carServiceSqlComponent", carServiceSqlComponent);
    context.start();

  }


  @Test
  public void integrationTest() throws Exception {
    List<CarData> list = new ArrayList<>();
    CarData carData = new CarData(1, 252);
    CarData afterSql = new CarData();
    afterSql.setCarId(1);
    afterSql.setMiles(252);
    list.add(carData);
    String listInJson = objectMapper.writeValueAsString(list);

    producerTemplate.sendBody("direct:in", listInJson);

    mockEndpoint.expectedMessageCount(1);

    mockMqttResult.expectedMessageCount(1);

    mockEnd.expectedMessageCount(1);
    mockEnd.message(0).body().isEqualTo(list);

    mockBeforePutInProcess.expectedMessageCount(1);
    mockBeforePutInProcess.message(0).body().isEqualTo(afterSql);

    mockBeforePutInProcess.assertIsSatisfied();
    mockMqttResult.assertIsSatisfied();
    mockEndpoint.assertIsSatisfied();
    mockEnd.assertIsSatisfied();
  }


  @After
  public void destroy() throws Exception {
    context.stop();
  }
}

<%={{ }}=%>
