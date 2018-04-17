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
    from("direct:{{catalyst.iotibm.direct.banking}}").routeId("changeStatusOfPayment")
        .convertBodyTo(String.class)
        .log("${body}     --   from payment")
        .setHeader("status").jsonpath("$.message")
        .setProperty("paymentId").jsonpath("$.paymentId")
        .to("insuranceSqlComponent:SELECT * FROM car_data WHERE payment_id = :#${property.paymentId}")
        .log("${body}     --   from DB")
        .split(body())
          .process(changeStatusOfPaymentProcessor)
        .end()
        .choice()
          .when(header("status").isEqualTo("OK"))
            .to("insuranceSqlComponent:UPDATE car_data SET status = 'paid' WHERE payment_id = :#${property.paymentId}")
          .otherwise()
            .to("insuranceSqlComponent:UPDATE car_data SET status = 'rejected' WHERE payment_id = :#${property.paymentId}");
// @formatter:on
  }
}

<%={{ }}=%>
