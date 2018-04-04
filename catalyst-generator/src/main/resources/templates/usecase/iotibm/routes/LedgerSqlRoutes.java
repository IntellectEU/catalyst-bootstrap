{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LedgerSqlRoutes extends RouteBuilder {

  @Autowired
  Processor fromLedgerToInsuranceDB;

  @Autowired
  Processor setInLedgerStatus;

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
    from("timer://foo?fixedRate=true&period=5000")
        .id("fromLedgerToInsuranceDB")
        .process(fromLedgerToInsuranceDB)
        .choice()
          .when(body().isNotNull())
            .split(body())
              .process(setInLedgerStatus)
              .log("${body}   ---  write to insurance DB")
              .to("insuranceSqlComponent:INSERT INTO car_data(car_id,user_id,miles,price) VALUES(:#${body.carId},:#${body.carId},:#${body.deltaMiles},:#${body.premium})");
// @formatter:on
  }
}

<%={{ }}=%>
