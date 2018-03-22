{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Constants of the datasource used for SQL endpoint
 */
@Configuration
public class DatasourceConfig {

  @Bean
  @ConfigurationProperties("catalyst.iotibm.carservice-datasource")
  public DataSourceProperties carServiceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("catalyst.iotibm.insurance-datasource")
  public DataSourceProperties insuranceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("catalyst.iotibm.carservice-datasource.hikari")
  public HikariDataSource carServiceDataSource(
      DataSourceProperties carServiceDataSourceProperties) {
    return (HikariDataSource) carServiceDataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @ConfigurationProperties("catalyst.iotibm.insurance-datasource.hikari")
  public HikariDataSource insuranceDataSource(
      DataSourceProperties insuranceDataSourceProperties) {
    return (HikariDataSource) insuranceDataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }
}

<%={{ }}=%>