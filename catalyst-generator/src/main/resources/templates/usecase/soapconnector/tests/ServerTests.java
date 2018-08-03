{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import model.CelsiumFahrenheitPortType;
import model.CelsiumTemperature;
import model.FahrenheitTemperature;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerTests extends CamelTestSupport {

  @Autowired
  CamelContext camelContext;

  private CxfEndpoint cxfEndpoint;
  private Exchange exchange;

  @Before
  public void init() {
    cxfEndpoint = new CxfEndpoint();
    cxfEndpoint.setAddress("http://localhost:9090/intellecteu/catalyst/convert");
    cxfEndpoint.setCamelContext(camelContext);
    cxfEndpoint.setServiceClass(CelsiumFahrenheitPortType.class);
    cxfEndpoint.setDataFormat(DataFormat.POJO);
    exchange = new DefaultExchange(camelContext);
  }

  @Test
  public void testSuccessfullConversionZero() {
    CelsiumTemperature celsiumTemperature = new CelsiumTemperature();
    celsiumTemperature.setCelsium(0.0d);

    exchange.getIn().setBody(celsiumTemperature);

    template.send(cxfEndpoint, exchange);

    FahrenheitTemperature actualFahrenheitTemperature = exchange.getOut()
        .getBody(FahrenheitTemperature.class);

    FahrenheitTemperature expectedFahrenheitTemperature = new FahrenheitTemperature();
    expectedFahrenheitTemperature.setFahrenheit(32.0d);

    assertEquals(expectedFahrenheitTemperature.getFahrenheit(), actualFahrenheitTemperature.getFahrenheit(), 0.00000000001);
  }

}

<%={{ }}=%>
