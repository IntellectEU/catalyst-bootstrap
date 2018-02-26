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

package io.spring.initializr.metadata;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link DependenciesCapability}.
 *
 * @author Stephane Nicoll
 */
public class DependenciesCapabilityTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void indexedDependencies() {
		Dependency dependency = Dependency.withId("first");
		Dependency dependency2 = Dependency.withId("second");
		DependenciesCapability capability = createDependenciesCapability("foo",
				dependency, dependency2);
		capability.validate();

		assertSame(dependency, capability.get("first"));
		assertSame(dependency2, capability.get("second"));
		assertNull(capability.get("anotherId"));
	}

	@Test
	public void addTwoDependenciesWithSameId() {
		Dependency dependency = Dependency.withId("conflict");
		Dependency dependency2 = Dependency.withId("conflict");
		DependenciesCapability capability = createDependenciesCapability("foo",
				dependency, dependency2);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("conflict");
		capability.validate();
	}

	@Test
	public void addDependencyWithAliases() {
		Dependency dependency = Dependency.withId("first");
		dependency.getAliases().add("alias1");
		dependency.getAliases().add("alias2");
		DependenciesCapability capability = createDependenciesCapability("foo",
				dependency);
		capability.validate();

		assertSame(dependency, capability.get("first"));
		assertSame(dependency, capability.get("alias1"));
		assertSame(dependency, capability.get("alias2"));
	}

	@Test
	public void aliasClashWithAnotherDependency() {
		Dependency dependency = Dependency.withId("first");
		dependency.getAliases().add("alias1");
		dependency.getAliases().add("alias2");
		Dependency dependency2 = Dependency.withId("alias2");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(createDependencyGroup("foo", dependency));
		capability.getContent().add(createDependencyGroup("bar", dependency2));

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("alias2");
		capability.validate();
	}

	@Test
	public void mergeAddEntry() {
		DependenciesCapability capability = createDependenciesCapability("foo",
				Dependency.withId("first"), Dependency.withId("second"));

		DependenciesCapability anotherCapability = createDependenciesCapability("foo",
				Dependency.withId("bar"), Dependency.withId("biz"));
		anotherCapability.getContent()
				.add(createDependencyGroup("bar", Dependency.withId("third")));

		capability.merge(anotherCapability);
		assertEquals(2, capability.getContent().size());
		assertNotNull(capability.get("first"));
		assertNotNull(capability.get("second"));
		assertNotNull(capability.get("third"));
	}

	@Test
	public void addDefaultVersionRange() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setVersionRange("1.2.3.RELEASE");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setVersionRange("1.2.0.RELEASE");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		assertEquals("1.2.0.RELEASE", capability.get("first").getVersionRange());
		assertEquals("1.2.3.RELEASE", capability.get("second").getVersionRange());
	}

	@Test
	public void addDefaultBom() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setBom("da-bom");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setBom("test-bom");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		assertEquals("test-bom", capability.get("first").getBom());
		assertEquals("da-bom", capability.get("second").getBom());
	}

	@Test
	public void addDefaultRepository() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setRepository("da-repo");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setRepository("test-repo");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		assertEquals("test-repo", capability.get("first").getRepository());
		assertEquals("da-repo", capability.get("second").getRepository());
	}

	private static DependenciesCapability createDependenciesCapability(String groupName,
			Dependency... dependencies) {
		DependenciesCapability capability = new DependenciesCapability();
		DependencyGroup group = createDependencyGroup(groupName, dependencies);
		capability.getContent().add(group);
		return capability;
	}

	private static DependencyGroup createDependencyGroup(String groupName,
			Dependency... dependencies) {
		DependencyGroup group = DependencyGroup.create(groupName);
		for (Dependency dependency : dependencies) {
			group.getContent().add(dependency);
		}
		return group;
	}

}
