{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;
import model.CelsiumTemperature;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.model.dataformat.SoapJaxbDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientRoute extends RouteBuilder {

  @Autowired
  CamelContext context;

  @Override
  public void configure() {
    CxfEndpoint cxfEndpoint = new CxfEndpoint();

    cxfEndpoint.setBeanId("cxfTemparatureEndpoint");
    cxfEndpoint.setAddress("http://localhost:9090/intellecteu/catalyst/convert");
    cxfEndpoint.setCamelContext(context);
    cxfEndpoint.setDataFormat(DataFormat.RAW);

    try {
      cxfEndpoint.setServiceClass("model.CelsiumFahrenheitPortType");
      context.addEndpoint("temperature", cxfEndpoint);
    } catch (Exception e) {
      e.printStackTrace();
    }

    CelsiumTemperature celsiumTemperature = new CelsiumTemperature();
    celsiumTemperature.setCelsium(0.0d);

    SoapJaxbDataFormat soapDF = new SoapJaxbDataFormat("model");

    from("timer:tempTimer?period=60000&repeatCount=1")
        .process(exchange -> exchange.getIn().setBody(celsiumTemperature))
        .marshal(soapDF)
        .log("Body received ${body}")
        .setHeader("Content-Type", constant("text/xml"))
        .to("temperature")
        .unmarshal(soapDF)
        .log("${body}");
  }
}
<%={{ }}=%>
