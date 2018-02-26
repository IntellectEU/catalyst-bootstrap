/*
 * Copyright 2012-2016 the original author or authors.
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

package com.intellecteu.catalyst.actuate.stat;

import com.intellecteu.catalyst.generator.ProjectRequest;
import com.intellecteu.catalyst.metadata.InitializrMetadata;
import com.intellecteu.catalyst.metadata.InitializrMetadataProvider;
import com.intellecteu.catalyst.metadata.SimpleInitializrMetadataProvider;
import com.intellecteu.catalyst.test.metadata.InitializrMetadataTestBuilder;

/**
 * @author Stephane Nicoll
 */
abstract class AbstractInitializrStatTests {

	private final InitializrMetadata metadata = InitializrMetadataTestBuilder
			.withDefaults()
			.addDependencyGroup("core", "security", "validation", "aop")
			.addDependencyGroup("web", "web", "data-rest", "jersey")
			.addDependencyGroup("data", "data-jpa", "jdbc")
			.addDependencyGroup("database", "h2", "mysql")
			.build();

	protected InitializrMetadataProvider createProvider(InitializrMetadata metadata) {
		return new SimpleInitializrMetadataProvider(metadata);
	}

	protected ProjectRequest createProjectRequest() {
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadata);
		return request;
	}

	public InitializrMetadata getMetadata() {
		return metadata;
	}

}
