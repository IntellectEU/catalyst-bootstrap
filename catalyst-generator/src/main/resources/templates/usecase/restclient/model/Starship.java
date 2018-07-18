{{=<% %>=}}

    <%license%>

    package <%fullPackageName%>;

import java.io.Serializable;

/**
 * Starship model represents a single transport craft that has hyperdrive capability.
 */
public class Starship implements Serializable {

  public String name;

  public String model;

  public String starshipClass;

  public Starship(String name, String model, String starshipClass) {
    this.name = name;
    this.model = model;
    this.starshipClass = starshipClass;
  }
}
<%={{ }}=%>
