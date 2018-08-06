{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  //You can run this example if you will run eclipse-mosquitto Docker container
  // >docker-compose -f test-compose.yml up from "resources"

  //Can be replaced by Your mqtt broker host and topic name
  protected static final String HOST = "localhost:1883";
  protected static final String TOPIC_NAME = "carData";

  @Autowired
  Processor fillMqttProcessor;

    @Override
    public void configure() {

      //Can be replaced by any other endpoint
      from("timer://runOnce?repeatCount=1")
          .routeId("toMqttRoute")
          .process(fillMqttProcessor).id("fillMqttProcessor") //Generating data for demonstration
          .log("\\r\\n -- To mqtt ---- ${body}")
          .to("mqtt:mosquitoTo?host=tcp://localhost:1883&publishTopicName=carData");

      from("mqtt:mosquittoFrom?host=tcp://localhost:1883&subscribeTopicNames=carData")
          .routeId("fromMqttRoute")
          .log("\\r\\n -- From mqtt ---- ${body}")
          .to("mock:result");
          //Can be replaced by any other endpoint
    }
}
