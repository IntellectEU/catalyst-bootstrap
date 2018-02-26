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

package io.spring.initializr.actuate.info;

import java.util.Map;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import org.junit.Test;

import org.springframework.boot.actuate.info.Info;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link BomRangesInfoContributor}
 *
 * @author Stephane Nicoll
 */
public class BomRangesInfoContributorTests {

	@Test
	public void noBom() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("bom-ranges");
	}

	@Test
	public void noMapping() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo", bom).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("bom-ranges");
	}

	@Test
	public void withMappings() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getMappings().add(
				BillOfMaterials.Mapping.create("[1.3.0.RELEASE,1.3.8.RELEASE]", "1.1.0"));
		bom.getMappings().add(
				BillOfMaterials.Mapping.create("1.3.8.BUILD-SNAPSHOT", "1.1.1-SNAPSHOT"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo", bom).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).containsKeys("bom-ranges");
		@SuppressWarnings("unchecked")
		Map<String, Object> ranges = (Map<String, Object>) info.getDetails()
				.get("bom-ranges");
		assertThat(ranges).containsOnlyKeys("foo");
		@SuppressWarnings("unchecked")
		Map<String, Object> foo = (Map<String, Object>) ranges.get("foo");
		assertThat(foo).containsExactly(
				entry("1.1.0", "Spring Boot >=1.3.0.RELEASE and <=1.3.8.RELEASE"),
				entry("1.1.1-SNAPSHOT", "Spring Boot >=1.3.8.BUILD-SNAPSHOT"));
	}

	private static Info getInfo(InitializrMetadata metadata) {
		Info.Builder builder = new Info.Builder();
		new BomRangesInfoContributor(new SimpleInitializrMetadataProvider(metadata))
				.contribute(builder);
		return builder.build();
	}

}
