{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
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
        .log("Root Exception Handler");

    onException(IllegalArgumentException.class)
        .handled(true) // Prevent Camel error handlers to process exception, as we handle it ourselves
        // Insert specific error handling
        .log("Specific exception handler");

    from("timer://runOnce?fixedRate=true&amp;period=55000")
        .id("mqttRoute")
        .process(fillMqttProcessor)
        .log("\\r\\n -- Fill mqtt ---- ${body}     \\r\\n")
        .to("mqtt:mosquitoTo?{{mosquitto.host}}&amp;publishTopicName={{mqtt.topic}");

    from("mqtt:mosquittoFrom?{{mosquitto.host}}&amp;subscribeTopicNames={{mqtt.topic}}")
        .id("mqttEnrichSqlRoute")
        .to("seda:toLedger");
// @formatter:off
    from("seda:toLedger").id("fromMqttToLedger")
        .process(convertJsonArrayProcessor)
        .log("\r\n -- MAXIMUM mqtt ---- ${body}     \r\n")
        .split(body())
          .log("${body}    -   from mqtt to ledger")
          .enrich("sql:{{select.from.clientsdata}}?dataSource=carServiceDataSource", sqlAggregator)
          .process(putInLedgerProcessor);
// @formatter:on
  }
}

<%={{ }}=%>