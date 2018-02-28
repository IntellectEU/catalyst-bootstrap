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
 * Camel routes for sql connectors
 * 
 */
@Component
public class Sql2LogRouter extends RouteBuilder {
    
    @Override
    public void configure() {
        // Consume files from sql.query
        from("sql:{{sql.query}}?onConsumeBatchComplete={{sql.onConsume}}").id("ReadSQlRoute")
                .autoStartup("{{sql.enabled}}")
                
                .log("Received data from [{{sql.query}}].")
                
                .log("TODO: Implement your routing");
    }
}
<%={{ }}=%>
