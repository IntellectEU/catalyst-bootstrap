{{=<% %>=}}

    <%license%>

package <%fullPackageName%>;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * People model represents an individual person or character within the Star Wars universe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StarWarsCharacter implements Serializable {

  public String name;

  @JsonProperty("birth_year")
  public String birthYear;

  public String gender;

  @JsonProperty("hair_color")
  public String hairColor;

  public String height;

  @JsonProperty("homeworld")
  public String homeWorld;

  @JsonProperty("starships")
  public List<Starship> starships;

}
<%={{ }}=%>
