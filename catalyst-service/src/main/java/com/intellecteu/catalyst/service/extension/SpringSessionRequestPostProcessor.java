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

package com.intellecteu.catalyst.service.extension;

import com.intellecteu.catalyst.generator.ProjectRequest;
import com.intellecteu.catalyst.generator.ProjectRequestPostProcessor;
import com.intellecteu.catalyst.metadata.Dependency;
import com.intellecteu.catalyst.metadata.InitializrMetadata;
import com.intellecteu.catalyst.util.Version;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * A {@link ProjectRequestPostProcessor} that provides explicit handling for the modules introduced
 * in Spring Session 2.
 *
 * @author Stephane Nicoll
 */
@Component
public class SpringSessionRequestPostProcessor
    extends AbstractProjectRequestPostProcessor {

  static final Dependency REDIS = Dependency.withId("session-data-redis",
      "org.springframework.session", "spring-session-data-redis");
  static final Dependency JDBC = Dependency.withId("session-jdbc",
      "org.springframework.session", "spring-session-jdbc");
  private static final Version VERSION_2_0_0_M3 = Version.parse("2.0.0.M3");

  @Override
  public void postProcessAfterResolution(ProjectRequest request,
      InitializrMetadata metadata) {
    if (isSpringBootVersionAtLeastAfter(request, VERSION_2_0_0_M3)) {
      swapSpringSessionDepenendency(request);
    }
  }

  private void swapSpringSessionDepenendency(ProjectRequest request) {
    Dependency session = getDependency(request, "session");
    if (session != null) {
      List<Dependency> swap = new ArrayList<>();
      if (hasDependency(request, "data-redis")
          || hasDependency(request, "data-redis-reactive")) {
        swap.add(REDIS);
      }
      if (hasDependency(request, "jdbc")) {
        swap.add(JDBC);
      }
      if (!swap.isEmpty()) {
        request.getResolvedDependencies().remove(session);
        request.getResolvedDependencies().addAll(swap);
      }
    }
  }

}
