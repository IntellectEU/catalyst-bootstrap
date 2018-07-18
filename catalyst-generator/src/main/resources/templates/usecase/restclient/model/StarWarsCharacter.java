{{=<% %>=}}

    <%license%>

    package <%fullPackageName%>;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import <%packageName%>.model.Starship;

/**
 * People model represents an individual person or character within the Star Wars universe.
 */
@AllArgsConstructor
public class StarWarsCharacter implements Serializable {

  public String name;

  public String birthYear;

  public String gender;

  public String hairColor;

  public String height;

  public String homeWorld;

  public List<Starship> starships;

  public StarWarsCharacter() {
  }

}
<%={{ }}=%>
