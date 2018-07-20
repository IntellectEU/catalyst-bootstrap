{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import java.util.Arrays;
import org.springframework.stereotype.Component;

import <%packageName%>.model.StarWarsCharacter;
import <%packageName%>.model.Starship;

@Component
public class StarWarsCharacterService {

  private static final Starship[] STARSHIPS = {
      new Starship("X-wing", "T-65 X-wing", "Starfighter"),
      new Starship("Imperial shuttle", "Lambda-class T-4a shuttle", "Armed government transport"),
      new Starship("TIE Advanced x1", "Twin Ion Engine Advanced x1", "Starfighter")
  };

  private static final StarWarsCharacter[] PEOPLE = {
      new StarWarsCharacter("Luke Skywalker", "19BBY", "male", "blond", "172", "Tatooine",
          Arrays.asList(STARSHIPS[0], STARSHIPS[1])),
      new StarWarsCharacter("Darth Vader", "41.9BBY", "male", "none", "202", "Tatooine", Arrays.asList(STARSHIPS[2]))
  };

  public StarWarsCharacter getPeopleById(Integer id) {
    if (id != null && id > 0 && id <= PEOPLE.length) {
      return PEOPLE[id - 1];
    } else {
      return new StarWarsCharacter();
    }
  }

}
<%={{ }}=%>
