{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import <%packageName%>.domain.CarData;
import <%packageName%>.routes.MqttSqlRoutes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class TestProfileContextConfiguration {

  @Bean
  public DataSource carServiceDataSource(){
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:clients-data-schema.sql")
        .addScript("classpath:clients-data.sql")
        .build();
  }

  @Bean
  public SqlComponent carServiceSqlComponent(@Qualifier("carServiceDataSource") DataSource dataSource) {
    SqlComponent sqlComponent = new SqlComponent();
    sqlComponent.setDataSource(dataSource);
    return sqlComponent;
  }

  @Bean
  Processor fillMqttProcessor() {
    return new Processor() {
      private int counter = 0;

      @Override
      public void process(Exchange exchange) throws Exception {
        double multiplier = counter + Math.random();
        System.out.println("\r\n ---- INCREMENT -----\r\n");
        System.out.println("milesIncrement" + multiplier);
        List<CarData> list = new ArrayList<>();
        list.add(new CarData(1, 252 * multiplier));
        list.add(new CarData(2, 252.13 * multiplier));
        list.add(new CarData(3, 93.13 * multiplier));
        list.add(new CarData(4, 123.41 * multiplier));
        list.add(new CarData(6, 1052 * multiplier));
        ObjectMapper objectMapper = new ObjectMapper();
        String listInJson = objectMapper.writeValueAsString(list);
        exchange.getIn().setBody(listInJson);
        counter++;
      }
    };
  }

  @Bean
  Processor convertJsonArrayProcessor() {
    return exchange -> {
      ObjectMapper objectMapper = new ObjectMapper();
      List<CarData> list = objectMapper.readValue(exchange.getIn().getBody(String.class),
          new TypeReference<List<CarData>>() {
          });
      exchange.getIn().setBody(list);
    };
  }

  @Bean
  Processor putInLedgerProcessor() {
    return exchange -> {};
  }

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  AggregationStrategy sqlAggregator() {
    return (oldExchange, newExchange) -> {
      Map<String, Object> sqlMapper =
          (Map<String, Object>) (newExchange.getIn().getBody(List.class).get(0));
      CarData oldCarData = oldExchange.getIn().getBody(CarData.class);
      CarData newCarData = new CarData();
      newCarData.setMiles(oldCarData.getMiles());
      newCarData.setCarId((Integer) sqlMapper.get("car_id"));
      oldExchange.getIn().setBody(newCarData);
      return oldExchange;
    };
  }

}

<%={{ }}=%>
