{{=<% %>=}}

<%license%>

package <%packageName%>;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.camel.component.sql.SqlComponent;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the datasource used for SQL endpoint
 */
@Configuration
public class Sql2LogConfig {

  @Bean
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.hikari")
  public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
    return (HikariDataSource) dataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "sql")
  public SqlComponent sqlComponent(HikariDataSource dataSource) {
    SqlComponent sqlComponent = new SqlComponent();
    sqlComponent.setDataSource(dataSource);
    return sqlComponent;
  }

}

<%={{ }}=%>