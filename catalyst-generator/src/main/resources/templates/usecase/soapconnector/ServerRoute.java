{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;
import model.CelsiumFahrenheitPortType;
import model.CelsiumTemperature;
import model.FahrenheitTemperature;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerRoute extends RouteBuilder {

  @Autowired
  CamelContext context;

  @Override
  public void configure() throws Exception {
    CxfEndpoint cxfEndpoint = new CxfEndpoint();
    CamelContext camelContext = getContext();

    cxfEndpoint.setBeanId("cxfTemparatureEndpoint");
    cxfEndpoint.setAddress("http://localhost:9090/intellecteu/catalyst/convert");
    cxfEndpoint.setCamelContext(camelContext);
    cxfEndpoint.setDataFormat(DataFormat.POJO);
    cxfEndpoint.setServiceClass(CelsiumFahrenheitPortType.class);
    context.addEndpoint("temperatureServer", cxfEndpoint);

    from(cxfEndpoint)
        .process(exchange -> {

          final CelsiumTemperature celsiumTemperatureRequest = exchange.getIn().getBody(
              CelsiumTemperature.class);

          final FahrenheitTemperature fahrenheitTemperatureResponse = new FahrenheitTemperature();
          fahrenheitTemperatureResponse
              .setFahrenheit(celsiumTemperatureRequest.getCelsium() * 1.8 + 32);

          exchange.getOut().setBody(fahrenheitTemperatureResponse);
        });
  }
}
<%={{ }}=%>
