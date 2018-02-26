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

import org.springframework.stereotype.Component;

/**
 * A {@link ProjectRequestPostProcessor} that automatically adds "reactor-test" when
 * webflux is selected.
 * 
 * @author Stephane Nicoll
 */
@Component
class ReactorTestRequestPostProcessor extends AbstractProjectRequestPostProcessor {

	private static final Version VERSION_2_0_0_M2 = Version.parse("2.0.0.M2");

	static  final Dependency REACTOR_TEST = Dependency.withId("reactor-test",
			"io.projectreactor", "reactor-test", null, Dependency.SCOPE_TEST);

	@Override
	public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		if (hasDependency(request, "webflux")
				&& isSpringBootVersionAtLeastAfter(request, VERSION_2_0_0_M2)) {
			request.getResolvedDependencies().add(REACTOR_TEST);
		}
	}

}
