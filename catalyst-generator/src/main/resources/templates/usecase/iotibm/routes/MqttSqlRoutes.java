{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttSqlRoutes extends RouteBuilder {

  @Autowired
  Processor fillMqttProcessor;

  @Autowired
  Processor convertJsonArrayProcessor;

  @Autowired
  Processor putInLedgerProcessor;

  @Autowired
  AggregationStrategy sqlAggregator;

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

    from("timer://runOnce?fixedRate=true&period=60000").routeId("mqttRoute")
        .process(fillMqttProcessor)
        .log("\\r\\n -- Fill mqtt ---- ${body}     \\r\\n")
        .to("mqtt:mosquitoTo?host={{catalyst.iotibm.mosquitto.host}}&publishTopicName={{catalyst.iotibm.mqtt.topic}}");

    from("mqtt:mosquittoFrom?host={{catalyst.iotibm.mosquitto.host}}&subscribeTopicNames={{catalyst.iotibm.mqtt.topic}}")
        .routeId("mqttEnrichSqlRoute")
        .to("seda:toLedger");
// @formatter:off
    from("seda:toLedger").routeId("fromMqttToLedger")
        .process(convertJsonArrayProcessor).id("convertProcess")
        .log("\r\n -- MAXIMUM mqtt ---- ${body}     \r\n")
        .split(body())
          .log("${body}    -   from mqtt to ledger")
          .enrich(
            "carServiceSqlComponent:SELECT * FROM clients_data WHERE device_id = :#${body.deviceId}",
            sqlAggregator)
          .log("${body}    -    after agregator ro ledger")
          .process(putInLedgerProcessor).id("putInProcess");
// @formatter:on
  }
}

<%={{ }}=%>
