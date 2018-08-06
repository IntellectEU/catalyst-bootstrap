{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.fasterxml.jackson.databind.ObjectMapper;
import <%packageName%>.model.CarData;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Processors {

  @Bean
  Processor fillMqttProcessor() {
    return new Processor() {

      public void process(Exchange exchange) throws Exception {
        List<CarData> list = new ArrayList<CarData>();
        list.add(new CarData(1, 252));
        list.add(new CarData(2, 252.13));
        list.add(new CarData(3, 93.13));
        list.add(new CarData(4, 123.41));
        list.add(new CarData(5, 1052));
        ObjectMapper objectMapper = new ObjectMapper();
        String listInJson = objectMapper.writeValueAsString(list);
        exchange.getIn().setBody(listInJson);
      }
    };
  }
}
