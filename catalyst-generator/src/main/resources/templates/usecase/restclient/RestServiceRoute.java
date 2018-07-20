{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import <%fullPackageName%>.model.StarWarsCharacter;

@Component
public class RestServiceRoute extends RouteBuilder {

  @Override
  public void configure() {
    from("rest:http://localhost:8080/people/1").unmarshal().json(JsonLibrary.Jackson, StarWarsCharacter.class);
  }
}
<%={{ }}=%>
