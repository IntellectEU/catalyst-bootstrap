/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate.autoconfigure;

import io.spring.initializr.actuate.metric.ProjectGenerationMetricsListener;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to handle the metrics of an initializr instance.
 *
 * @author Dave Syer
 */
@Configuration
public class InitializrMetricsConfiguration {

	@Bean
	public ProjectGenerationMetricsListener metricsListener(
			CounterService counterService) {
		return new ProjectGenerationMetricsListener(counterService);
	}

}
