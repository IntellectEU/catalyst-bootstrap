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

package com.intellecteu.catalyst.web.support;

import com.intellecteu.catalyst.metadata.BillOfMaterials;
import com.intellecteu.catalyst.metadata.Dependency;
import com.intellecteu.catalyst.metadata.DependencyMetadata;
import com.intellecteu.catalyst.metadata.DependencyMetadataProvider;
import com.intellecteu.catalyst.metadata.InitializrMetadata;
import com.intellecteu.catalyst.metadata.Repository;
import com.intellecteu.catalyst.util.Version;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;

/**
 * A default {@link DependencyMetadataProvider} implementation.
 *
 * @author Stephane Nicoll
 */
public class DefaultDependencyMetadataProvider implements DependencyMetadataProvider {

  @Override
  @Cacheable(cacheNames = "initializr.dependency-metadata", key = "#p1")
  public DependencyMetadata get(InitializrMetadata metadata, Version bootVersion) {
    Map<String, Dependency> dependencies = new LinkedHashMap<>();
    for (Dependency d : metadata.getDependencies().getAll()) {
      if (d.match(bootVersion)) {
        dependencies.put(d.getId(), d.resolve(bootVersion));
      }
    }

    Map<String, Repository> repositories = new LinkedHashMap<>();
    for (Dependency d : dependencies.values()) {
      if (d.getRepository() != null) {
        repositories.put(d.getRepository(), metadata.getConfiguration()
            .getEnv().getRepositories().get(d.getRepository()));
      }
    }

    Map<String, BillOfMaterials> boms = new LinkedHashMap<>();
    for (Dependency d : dependencies.values()) {
      if (d.getBom() != null) {
        boms.put(d.getBom(), metadata.getConfiguration().getEnv()
            .getBoms().get(d.getBom()).resolve(bootVersion));
      }
    }
    // Each resolved bom may require additional repositories
    for (BillOfMaterials b : boms.values()) {
      for (String id : b.getRepositories()) {
        repositories.put(id, metadata.getConfiguration().getEnv()
            .getRepositories().get(id));
      }
    }

    return new DependencyMetadata(bootVersion, dependencies, repositories, boms);
  }

}
