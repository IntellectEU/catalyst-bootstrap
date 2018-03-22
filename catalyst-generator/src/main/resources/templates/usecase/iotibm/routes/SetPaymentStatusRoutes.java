{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Igor Ivaniuk, 22.03.18
 */
@Component
public class SetPaymentStatusRoutes extends RouteBuilder {

  @Autowired
  Processor changeStatusOfPaymentProcessor;

  @Override
  public void configure() {
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
