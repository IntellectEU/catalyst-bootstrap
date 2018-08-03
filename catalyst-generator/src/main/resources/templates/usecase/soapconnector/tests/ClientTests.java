{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import model.CelsiumFahrenheitPortType;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.component.mock.MockEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.camel.test.junit4.CamelTestSupport;

import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientTests extends CamelTestSupport {

  @EndpointInject(uri = "mock:received")
  protected MockEndpoint mockTemperatureEndpoint;

  @Autowired
  CamelContext camelContext;

  @Autowired
  private ClientRoute clientRoute;

  private String serverUri = "http://0.0.0.0:8080/wallstreet";
  private CxfEndpoint cxfEndpoint;

  @Before
  public void init() {
    cxfEndpoint = new CxfEndpoint();
    cxfEndpoint.setAddress(serverUri);
    cxfEndpoint.setCamelContext(camelContext);
    cxfEndpoint.setServiceClass(CelsiumFahrenheitPortType.class);
    cxfEndpoint.setDataFormat(DataFormat.POJO);
  }

  @Test
  public void test() throws Exception {
    camelContext.addEndpoint("temperature", mockTemperatureEndpoint);
    camelContext.addRoutes(clientRoute);

    mockTemperatureEndpoint.expectedMessageCount(1);
    mockTemperatureEndpoint.assertIsSatisfied();
  }

}

<%={{ }}=%>
