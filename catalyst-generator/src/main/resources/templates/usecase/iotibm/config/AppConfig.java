{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import static <%packageName%>.config.Constants.CHAINCODE;
import static <%packageName%>.config.Constants.CHANNEL;
import static <%packageName%>.config.Constants.GET_FUNCTION;
import static <%packageName%>.config.Constants.HOST;
import static <%packageName%>.config.Constants.OWNER;
import static <%packageName%>.config.Constants.PAID_STATUS;
import static <%packageName%>.config.Constants.PEER;
import static <%packageName%>.config.Constants.PENDING_STATUS;
import static <%packageName%>.config.Constants.PUT_FUNCTION;
import static <%packageName%>.config.Constants.REJECTED_STATUS;
import static <%packageName%>.config.Constants.SET_FUNCTION;

import <%packageName%>.domain.CarData;
import <%packageName%>.domain.InsuranceData;
import <%packageName%>.service.BlockchainConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.DataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

@Configuration
public class AppConfig {

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
  Processor changeStatusOfPaymentProcessor(BlockchainConnector blockchainConnector) {
    return exchange -> {
      Integer carId =
          (Integer) ((Map<String, Object>) exchange.getIn().getBody(Map.class)).get("car_id");
      String status =
          exchange.getIn().getHeader("status").equals("OK") ? PAID_STATUS : REJECTED_STATUS;
      String jsonArgs =
          "{\"CarId\":" + carId + ",\"Status\":\"" + status + "\"}";
      blockchainConnector.invoke(HOST, CHANNEL, CHAINCODE, SET_FUNCTION, jsonArgs);
    };
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
        list.add(new CarData(5, 1052 * multiplier));
        ObjectMapper objectMapper = new ObjectMapper();
        String listInJson = objectMapper.writeValueAsString(list);
        exchange.getIn().setBody(listInJson);
        counter++;
      }
    };
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

  @Bean
  Processor putInLedgerProcessor(ObjectMapper objectMapper,
      BlockchainConnector blockchainConnector) {
    return exchange -> {
      String newCarDataInJson = null;
      try {
        newCarDataInJson = objectMapper
            .writeValueAsString(exchange.getIn().getBody(CarData.class));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      blockchainConnector.invoke(HOST, CHANNEL, CHAINCODE, PUT_FUNCTION, newCarDataInJson);
    };
  }

  @Bean
  Processor setInLedgerStatus(BlockchainConnector blockchainConnector) {
    return exchange -> {
      final String input =
          "{\"CarId\":" + exchange.getIn().getBody(InsuranceData.class).getCarId() +
              ",\"Status\":\"" + PENDING_STATUS + "\"}";

      blockchainConnector.invoke(HOST, CHANNEL, CHAINCODE, SET_FUNCTION, input);
    };
  }

  @Bean
  Processor fromLedgerToInsuranceDB(ObjectMapper objectMapper,
      BlockchainConnector blockchainConnector) {
    return exchange -> {
      ResponseEntity<String> response = blockchainConnector
          .query(HOST, CHANNEL, CHAINCODE, GET_FUNCTION, OWNER, PEER, "", String.class);
      String result = (String) objectMapper.readValue(response.getBody(), HashMap.class)
          .get("result");
      if (!result.isEmpty()) {
        List<InsuranceData> list = Arrays
            .asList(objectMapper.readValue(result, InsuranceData[].class));
        exchange.getIn().setBody(list);
      }
    };
  }

  @Bean
  Processor paymentProcessor() {
    return exchange -> {
      if (exchange.getIn().getBody(List.class) == null
          || exchange.getIn().getBody(List.class).get(0) == null) {
        exchange.getIn().setBody(null);
        return;
      }
      Map<String, Object> row = (Map<String, Object>) exchange.getIn().getBody(List.class).get(0);
      InsuranceData data = new InsuranceData();
      data.setPaymentId(Integer.parseInt(row.get("payment_id").toString()));
      data.setCarId(Integer.parseInt(row.get("car_id").toString()));
      data.setUserId(Integer.parseInt(row.get("user_id").toString()));
      data.setDeltaMiles(Double.parseDouble(row.get("miles").toString()));
      data.setPremium(Double.parseDouble(row.get("price").toString()));
      exchange.getIn().setBody(new ObjectMapper().writeValueAsString(data));
    };
  }

  @Bean
  DataFormat jsonMarshall() {
    return new GsonDataFormat();
  }
}

<%={{ }}=%>
