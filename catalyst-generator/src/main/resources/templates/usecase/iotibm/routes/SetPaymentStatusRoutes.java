{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetPaymentStatusRoutes extends RouteBuilder {

  @Autowired
  Processor changeStatusOfPaymentProcessor;

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
    from("direct:{{direct.banking}}").id("changeStatusOfPayment")
        .convertBodyTo(String.class)
        .log("${body}     --   from payment")
        .setHeader("status").jsonpath("$.message")
        .setProperty("paymentId").jsonpath("$.paymentId")
        .toD("sql:{{select.from.cardata}}?dataSource=insuranceDataSource")
        .log("${body}     --   from DB")
        .split(body())
          .process(changeStatusOfPaymentProcessor)
        .end()
        .choice()
          .when(header("status").isEqualTo("OK"))
            .toD("sql:{{update.cardata.status.paid}}?dataSource=insuranceDataSource")
          .otherwise()
            .toD("sql:{{update.cardata.status.rejected}}?dataSource=insuranceDataSource");
// @formatter:on
  }
}

<%={{ }}=%>