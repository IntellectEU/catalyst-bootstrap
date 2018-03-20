{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultPollingConsumerPollStrategy;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    FileSftpProperties.class
})
public class FileSftpConfig {

  @Bean
  DefaultPollingConsumerPollStrategy ftpPollStrategy(FileSftpProperties properties) {
    return new DefaultPollingConsumerPollStrategy() {
      @Override
      public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e)
          throws Exception {
        log.warn(
            "Exception occurred on endpoint (" + endpoint.getEndpointKey() + "): " + e
                .getMessage());
        if (retryCounter < properties.getMaxRetries()) {
          return true;
        }
        log.error(
            "Stopping consumer for the endpoint (" + endpoint.getEndpointKey() + ") Root cause:" + e
                .getMessage() + " ");
        consumer.stop();
        return false;
      }
    };
  }

  @Bean
  CamelContextConfiguration contextConfiguration() {
    return new CamelContextConfiguration() {
      @Override
      public void beforeApplicationStart(CamelContext context) {
        // your custom configuration goes here
      }

      @Override
      public void afterApplicationStart(CamelContext camelContext) {

      }
    };
  }

}

<%={{ }}=%>
