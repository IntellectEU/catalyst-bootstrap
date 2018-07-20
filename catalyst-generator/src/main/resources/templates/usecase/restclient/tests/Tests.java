{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import org.apache.camel.Exchange;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;
import static org.hamcrest.Matchers.is;

import <%packageName%>.model.StarWarsCharacter;
import <%packageName%>.model.Starship;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class Tests extends CamelTestSupport {

  private static final Starship[] starShips = {
      new Starship("X-wing", "T-65 X-wing", "Starfighter"),
      new Starship("Imperial shuttle", "Lambda-class T-4a shuttle", "Armed government transport")
  };

  @Test
  public void sendGetRequest() throws Exception {
    Exchange exchange = template.request("restlet:http://localhost:8080/people/1", null);
    assertThat(exchange.getOut().getHeader(HTTP_RESPONSE_CODE, Integer.class), is(200));

    StarWarsCharacter expected = new StarWarsCharacter("Luke Skywalker", "19BBY", "male", "blond",
        "172", "Tatooine",
        Arrays.asList(starShips[0], starShips[1]));

    String body = exchange.getOut().getBody(String.class);

    ObjectMapper mapper = new ObjectMapper();
    StarWarsCharacter actual = mapper.readValue(body, StarWarsCharacter.class);

    assertEquals(expected, actual);
  }

}
<%={{ }}=%>
