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

package com.intellecteu.catalyst.web.mapper;

import static org.junit.Assert.assertEquals;

import com.intellecteu.catalyst.metadata.BillOfMaterials;
import com.intellecteu.catalyst.metadata.Dependency;
import com.intellecteu.catalyst.metadata.DependencyMetadata;
import com.intellecteu.catalyst.metadata.Repository;
import com.intellecteu.catalyst.util.Version;
import java.net.URL;
import java.util.Collections;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @author Stephane Nicoll
 */
public class DependencyMetadataJsonMapperTests {

  private final DependencyMetadataJsonMapper mapper =
      new DependencyMetadataV21JsonMapper();

  @Test
  public void mapDependency() throws Exception {
    Dependency d = Dependency.withId("foo", "org.foo", "foo");
    d.setRepository("my-repo");
    d.setBom("my-bom");
    d.setCategory("camel");
    Repository repository = new Repository();
    repository.setName("foo-repo");
    repository.setUrl(new URL("http://example.com/foo"));
    BillOfMaterials bom = BillOfMaterials.create("org.foo", "foo-bom",
        "1.0.0.RELEASE");
    DependencyMetadata metadata = new DependencyMetadata(
        Version.parse("1.2.0.RELEASE"), Collections.singletonMap(d.getId(), d),
        Collections.singletonMap("repo-id", repository),
        Collections.singletonMap("bom-id", bom));
    JSONObject content = new JSONObject(mapper.write(metadata));
    assertEquals("my-bom", content.getJSONObject("dependencies").getJSONObject("foo")
        .getString("bom"));
    assertEquals("my-repo", content.getJSONObject("dependencies").getJSONObject("foo")
        .getString("repository"));
    assertEquals("foo-repo", content.getJSONObject("repositories")
        .getJSONObject("repo-id").getString("name"));
    assertEquals("foo-bom", content.getJSONObject("boms").getJSONObject("bom-id")
        .getString("artifactId"));
    assertEquals("1.0.0.RELEASE", content.getJSONObject("boms")
        .getJSONObject("bom-id").getString("version"));
  }

}
