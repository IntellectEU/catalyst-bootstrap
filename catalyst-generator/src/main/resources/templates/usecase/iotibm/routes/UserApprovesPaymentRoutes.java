{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Igor Ivaniuk, 22.03.18
 */
@Component
public class UserApprovesPaymentRoutes extends RouteBuilder {

  @Autowired
  DataFormat jsonMarshal;

  @Override
  public void configure() {
// @formatter:off
    from("sql:{{select.from.cardata.status.new}}?dataSource=insuranceDataSource")
        .id("getTransaction")
        .choice()
          .when(body().isNotNull())
            .split(body())
              .to("sql:{{update.cardata.status.pending}}?dataSource=insuranceDataSource")
              .to("direct:newBills");

    from("direct:newBills").id("getUserApproval")
        .setHeader("CamelHttpMethod", constant("POST"))
        .setHeader("Content-Type", constant("application/json"))
        .marshal(jsonMarshal)
        .to("http4://{{user.host}}")
        .to("direct:{{direct.userApproval}}?failIfNoConsumers=false");
// @formatter:on
  }
}
