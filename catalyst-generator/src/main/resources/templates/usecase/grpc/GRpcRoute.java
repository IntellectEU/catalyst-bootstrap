{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GRpcRoute extends RouteBuilder {

  @Override
  public void configure() {
    final CamelHelloRequest request = CamelHelloRequest.newBuilder().setName("Camel").build();
    String pack = this.getClass().getPackage().getName();

    from("timer://foo?period=10000&repeatCount=5")
        .routeId("grpc-test")
        .process(exchange -> exchange.getIn().setBody(request, CamelHelloRequest.class)).id("processor")
        .to("grpc://localhost:50051/"+pack+".CamelHello?method=sayHelloToCamel&synchronous=true")
        .log("Received ${body}");
  }

}
