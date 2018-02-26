package com.intellecteu.catalyst.stub;

import com.intellecteu.catalyst.actuate.autoconfigure.InitializrActuatorEndpointsAutoConfiguration;
import com.intellecteu.catalyst.web.autoconfigure.InitializrAutoConfiguration;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A sample app where the Initializr auto-configuration has been disabled.
 *
 * @author Stephane Nicoll
 */
@SpringBootApplication(exclude = {InitializrAutoConfiguration.class,
		InitializrActuatorEndpointsAutoConfiguration.class})
public class SampleApp {
}
