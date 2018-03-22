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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
