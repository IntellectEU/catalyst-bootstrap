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

package com.intellecteu.catalyst.web.project;

import com.intellecteu.catalyst.generator.ProjectRequest;
import com.intellecteu.catalyst.generator.ProjectRequestPostProcessor;
import com.intellecteu.catalyst.metadata.InitializrMetadata;
import com.intellecteu.catalyst.web.AbstractInitializrControllerIntegrationTests;
import com.intellecteu.catalyst.web.project.ProjectGenerationPostProcessorTests.ProjectRequestPostProcessorConfiguration;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-default")
@Import(ProjectRequestPostProcessorConfiguration.class)
public class ProjectGenerationPostProcessorTests
    extends AbstractInitializrControllerIntegrationTests {


  @Test
  public void postProcessorsInvoked() {
    downloadZip("/starter.zip?bootVersion=1.2.4.RELEASE&javaVersion=1.6")
        .isJavaProject()
        .isMavenProject().pomAssert()
        .hasSpringBootParent("1.2.3.RELEASE")
        .hasProperty("java.version", "1.7");
  }


  @Configuration
  static class ProjectRequestPostProcessorConfiguration {

    @Bean
    @Order(2)
    ProjectRequestPostProcessor secondPostProcessor() {
      return new ProjectRequestPostProcessor() {
        @Override
        public void postProcessBeforeResolution(ProjectRequest request,
            InitializrMetadata metadata) {
          request.setJavaVersion("1.7");
        }
      };
    }

    @Bean
    @Order(1)
    ProjectRequestPostProcessor firstPostProcessor() {
      return new ProjectRequestPostProcessor() {
        @Override
        public void postProcessBeforeResolution(ProjectRequest request,
            InitializrMetadata metadata) {
          request.setJavaVersion("1.2");
          request.setBootVersion("1.2.3.RELEASE");
        }
      };
    }

  }

}
