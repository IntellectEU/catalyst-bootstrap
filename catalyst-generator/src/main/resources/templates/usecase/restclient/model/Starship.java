{{=<% %>=}}

    <%license%>

    package <%fullPackageName%>;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Starship model represents a single transport craft that has hyperdrive capability.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Starship implements Serializable {

  @JsonProperty("name")
  public String name;

  @JsonProperty("model")
  public String model;

  @JsonProperty("starship_class")
  public String starshipClass;

}
<%={{ }}=%>
