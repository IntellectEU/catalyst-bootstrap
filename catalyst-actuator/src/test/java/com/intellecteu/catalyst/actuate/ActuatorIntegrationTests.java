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

package com.intellecteu.catalyst.actuate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.catalyst.web.AbstractFullStackInitializrIntegrationTests;
import com.intellecteu.catalyst.web.AbstractInitializrIntegrationTests.Config;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@SpringBootTest(classes = Config.class, webEnvironment = RANDOM_PORT,
    properties = "management.endpoints.web.exposure.include=info,metrics")
public class ActuatorIntegrationTests
    extends AbstractFullStackInitializrIntegrationTests {

  @Test
  public void infoHasExternalProperties() {
    String body = getRestTemplate().getForObject(
        createUrl("/actuator/info"), String.class);
    assertTrue("Wrong body:\n" + body, body.contains("\"spring-boot\""));
    assertTrue("Wrong body:\n" + body,
        body.contains("\"version\":\"1.1.4.RELEASE\""));
  }

  @Test
  public void metricsAreRegistered() {
    downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web&style=jpa");
    JsonNode result = metricsEndpoint();
    JsonNode names = result.get("names");
    List<String> metrics = new ArrayList<>();
    for (JsonNode name : names) {
      metrics.add(name.textValue());
    }
    assertThat(metrics).contains("initializr.requests", "initializr.packaging.jar",
        "initializr.java_version.1_8", "initializr.dependency.web",
        "initializr.dependency.data-jpa");

    int requests = metricValue("initializr.requests");
    int packaging = metricValue("initializr.packaging.jar");
    int javaVersion = metricValue("initializr.java_version.1_8");
    int webDependency = metricValue("initializr.dependency.web");
    int jpaDependency = metricValue("initializr.dependency.data-jpa");

    // No jpa dep this time
    downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web");

    assertEquals("Number of request should have increased", requests + 1,
        metricValue("initializr.requests"));
    assertEquals("jar packaging metric should have increased", packaging + 1,
        metricValue("initializr.packaging.jar"));
    assertEquals("java version metric should have increased", javaVersion + 1,
        metricValue("initializr.java_version.1_8"));
    assertEquals("web dependency metric should have increased", webDependency + 1,
        metricValue("initializr.dependency.web"));
    assertEquals("jpa dependency metric should not have increased", jpaDependency,
        metricValue("initializr.dependency.data-jpa"));
  }

  private JsonNode metricsEndpoint() {
    return parseJson(getRestTemplate().getForObject(
        createUrl("/actuator/metrics"), String.class));
  }

  private int metricValue(String metric) {
    JsonNode root = parseJson(getRestTemplate().getForObject(
        createUrl("/actuator/metrics/" + metric), String.class));
    JsonNode measurements = root.get("measurements");
    assertThat(measurements.isArray());
    assertThat(measurements.size()).isEqualTo(1);
    JsonNode measurement = measurements.get(0);
    return measurement.get("value").intValue();
  }

}
