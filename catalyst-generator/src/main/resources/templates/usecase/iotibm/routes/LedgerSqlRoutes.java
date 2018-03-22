{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LedgerSqlRoutes extends RouteBuilder {

  @Autowired
  Processor fromLedgerToInsuranceDB;

  @Autowired
  Processor setInLedgerStatus;

  @Override
  public void configure() {
// @formatter:off
    from("timer://foo?fixedRate=true&amp;period=5000")
        .id("fromLedgerToInsuranceDB")
        .process(fromLedgerToInsuranceDB)
        .choice()
          .when(body().isNotNull())
            .split(body())
              .process(setInLedgerStatus)
              .log("${body}   ---  write to insurance DB")
              .to("sql:{{insert.into.cardata}}?dataSource=insuranceDataSource");
// @formatter:on
  }
}
