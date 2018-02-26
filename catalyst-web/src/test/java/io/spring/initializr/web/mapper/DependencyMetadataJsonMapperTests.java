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

package io.spring.initializr.web.mapper;

import java.net.URL;
import java.util.Collections;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.util.Version;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
