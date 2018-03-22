{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of Idempotent Repository for Duplicate Check use-case
 */
@Configuration
public class DuplicateCheckConfig {

  @Bean
  public IdempotentRepository idempotentRepository() {
    return new MemoryIdempotentRepository();
  }
}

<%={{ }}=%>