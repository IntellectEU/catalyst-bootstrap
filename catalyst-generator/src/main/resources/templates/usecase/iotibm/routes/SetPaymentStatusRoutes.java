/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

{{=<% %>=}}

<%license%>

package <%packageName%>;

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
