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

package com.intellecteu.catalyst.generator;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.intellecteu.catalyst.InitializrException;
import com.intellecteu.catalyst.test.generator.ProjectAssert;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

/**
 * Test for custom {@link ProjectGenerator}.
 *
 * @author Torsten Walter
 * @author Stephane Nicoll
 */
public class CustomProjectGeneratorTests extends AbstractProjectGeneratorTests {

  public CustomProjectGeneratorTests() {
    super(new MyProjectGenerator());
  }

  @Test
  public void generateCustomResource() {
    ProjectRequest request = createProjectRequest();
    request.setType("maven-project");
    request.setGroupId("com.example.custom");
    ProjectAssert project = generateProject(request);
    project.sourceCodeAssert("custom.txt")
        .equalsTo(new ClassPathResource("project/custom/custom.txt"));
  }

  @Test
  public void camelUsecaseTest() {
    ProjectRequest request = createProjectRequest();
    request.setType("maven-project");
    request.setLanguage("java");
    request.setName("MyDemo");
    request.setPackageName("foo");
    request.setFacets(new ArrayList<String>() {{
      add("code.txt,{sources}/code.txt");
      add("code.txt,{tests}/code.txt");
      add("custom.txt,{resources}/custom.txt");
      add("custom.txt,custom.txt");
    }});
    ProjectAssert project = generateProject(request);
    project.sourceCodeAssert("src/main/java/foo/code.txt").contains("foo");
    project.sourceCodeAssert("src/test/java/foo/code.txt").contains("foo");
    project.sourceCodeAssert("src/main/resources/custom.txt");
    project.sourceCodeAssert("custom.txt");
  }

  @Test
  public void camelUsecase_fullPackageName_empty_Test() {
    ProjectRequest request = createProjectRequest();
    request.setType("maven-project");
    request.setLanguage("java");
    request.setName("MyDemo");
    request.setPackageName("foo");
    request.setFacets(new ArrayList<String>() {{
      add("code.txt,{resources}/code.txt");
    }});
    try {
      generateProject(request);
      Assert.fail("Exception is not thrown !");
    } catch (final Exception exception) {
      // Verify if the thrown exception is instance of MyException, otherwise throws an assert failure
      assertTrue("An exception other than MyException is thrown !",
          exception instanceof InitializrException);
      // Verifying the error message
      assertTrue(exception.getCause().getCause().getMessage()
          .contains("No method or field with name 'fullPackageName'"));
    }

  }

  @Test
  public void generateCustomResourceDisabled() {
    ProjectRequest request = createProjectRequest();
    request.setType("gradle-build");
    request.setGroupId("com.example.custom");
    ProjectAssert project = generateProject(request);
    project.hasNoFile("custom.txt");
  }

  @Test
  public void projectGenerationEventFiredAfterCustomization() {
    ProjectRequest request = createProjectRequest();
    request.setType("maven-project");
    request.setGroupId("com.example.custom");
    generateProject(request);
    verifyProjectSuccessfulEventFor(request);

    Runnable customFileGenerated = ((MyProjectGenerator) projectGenerator).customFileGenerated;
    InOrder inOrder = Mockito.inOrder(eventPublisher, customFileGenerated);

    inOrder.verify(customFileGenerated, times(1)).run();
    inOrder.verify(eventPublisher, times(1)).publishEvent(
        argThat(new ProjectGeneratedEventMatcher(request)));
  }


  private static class MyProjectGenerator extends ProjectGenerator {

    private Runnable customFileGenerated = mock(Runnable.class);

    @Override
    protected File generateProjectStructure(ProjectRequest request,
        Map<String, Object> model) {
      model.put("customValue", 42);
      File dir = super.generateProjectStructure(request, model);
      if ("maven".equals(request.getBuild())) {
        write(new File(dir, "custom.txt"), "custom.txt", model);
        customFileGenerated.run();
      }
      return dir;
    }
  }

}
