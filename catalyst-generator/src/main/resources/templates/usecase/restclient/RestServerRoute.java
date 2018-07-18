{{=<% %>=}}

    <%license%>

    package <%fullPackageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import <%packageName%>.service.StarWarsCharacterService;
/**
 * Created by tonya on 7/17/2018.
 */
@Component
public class RestServerRoute extends RouteBuilder {

  @Autowired
  private StarWarsCharacterService starWarsCharacterService;

  @Override
  public void configure() {
    restConfiguration()
        .component("restlet")
        .host("localhost").port("8080")
        .bindingMode(RestBindingMode.auto);

    rest().path("/people/{id}").get().route()
        .bean(starWarsCharacterService, "getPeopleById(${header.id})");
  }

}
<%={{ }}=%>