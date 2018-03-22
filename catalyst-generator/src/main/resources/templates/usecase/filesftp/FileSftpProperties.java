{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "catalyst.filesftp")
public class FileSftpProperties {

  private Integer maxRetries = 3;

  public Integer getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
  }

}
<%={{ }}=%>
