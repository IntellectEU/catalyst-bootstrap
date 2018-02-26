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

package io.spring.initializr.web.autoconfigure;

import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
public class CloudfoundryEnvironmentPostProcessorTests {

	private final CloudfoundryEnvironmentPostProcessor postProcessor =
			new CloudfoundryEnvironmentPostProcessor();
	private final MockEnvironment environment = new MockEnvironment();
	private final SpringApplication application = new SpringApplication();

	@Test
	public void parseCredentials() {
		environment.setProperty("vcap.services.stats-index.credentials.uri",
				"http://user:pass@example.com/bar/biz?param=one");
		postProcessor.postProcessEnvironment(environment, application);

		assertThat(environment.getProperty("initializr.stats.elastic.uri"))
				.isEqualTo("http://example.com/bar/biz?param=one");
		assertThat(environment.getProperty("initializr.stats.elastic.username"))
				.isEqualTo("user");
		assertThat(environment.getProperty("initializr.stats.elastic.password"))
				.isEqualTo("pass");
	}

	@Test
	public void parseNoCredentials() {
		environment.setProperty("vcap.services.stats-index.credentials.uri",
				"http://example.com/bar/biz?param=one");
		postProcessor.postProcessEnvironment(environment, application);

		assertThat(environment.getProperty("initializr.stats.elastic.uri"))
				.isEqualTo("http://example.com/bar/biz?param=one");
		assertThat(environment.getProperty("initializr.stats.elastic.username")).isNull();
		assertThat(environment.getProperty("initializr.stats.elastic.password")).isNull();
	}

	@Test
	public void parseNoVcapUri() {
		postProcessor.postProcessEnvironment(environment, application);

		assertThat(environment.getProperty("initializr.stats.elastic.uri")).isNull();
		assertThat(environment.getProperty("initializr.stats.elastic.username")).isNull();
		assertThat(environment.getProperty("initializr.stats.elastic.password")).isNull();
	}

}
