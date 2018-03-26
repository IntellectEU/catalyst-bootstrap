{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankApprovesPaymentRoutes extends RouteBuilder {

  @Autowired
  Processor paymentProcessor;

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

    // @formatter:off
    from("direct:{{direct.userApproval}}").id("userPays")
        .convertBodyTo(String.class)
        .log("${body}   - user result")
        .setProperty("status").jsonpath("$.status")
        .choice()
          .when().simple("${property.status} == 'ACCEPT'")
          .setProperty("paymentId").jsonpath("$.id")
          .toD("sql:{{select.from.cardata}}?dataSource=insuranceDataSource")
          .process(paymentProcessor)
          .choice()
            .when(body().isNotNull())
              .setHeader("CamelHttpMethod",constant("POST"))
              .setHeader("Content-Type",constant("application/json"))
              .log("${body}     --- p")
              .to("http4://{{payment.host}}")
              .to("direct:{{direct.banking}}")
          .endChoice()
        .otherwise()
          .to("direct:{{direct.banking}}")
        .endChoice()
        .end();
    // @formatter:on
  }
}

<%={{ }}=%>