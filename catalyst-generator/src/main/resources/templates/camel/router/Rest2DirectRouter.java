/*
 *
 *  * Copyright 2012-2018. the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

{{=<% %>=}}
package <%packageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel routes for REST consumer
 * 
 */
@Component
public class Rest2DirectRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        rest("/{{rest.base.path}}/")
                .get("/{id}")
                    // TODO: route
                    .to("direct:customerDetail")
                .get("/{id}/orders")
                     // TODO: route
                    .to("direct:customerOrders");
    }
}

<%={{ }}=%>
