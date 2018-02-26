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

package io.spring.initializr.util;

import java.util.Collections;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

/**
 * @author Stephane Nicoll
 */
public class VersionTests {

	private static final VersionParser parser = new VersionParser(Collections.emptyList());

	@Test
	public void equalNoQualifier() {
		Version first = parse("1.2.0");
		Version second = parse("1.2.0");
		assertThat(first, comparesEqualTo(second));
		assertThat(first, equalTo(second));
	}

	@Test
	public void equalQualifierNoVersion() {
		Version first = parse("1.2.0.RELEASE");
		Version second = parse("1.2.0.RELEASE");
		assertThat(first, comparesEqualTo(second));
		assertThat(first, equalTo(second));
	}

	@Test
	public void equalQualifierVersion() {
		Version first = parse("1.2.0.RC1");
		Version second = parse("1.2.0.RC1");
		assertThat(first, comparesEqualTo(second));
		assertThat(first, equalTo(second));
	}

	@Test
	public void compareMajorOnly() {
		assertThat(parse("2.2.0"), greaterThan(parse("1.8.0")));
	}

	@Test
	public void compareMinorOnly() {
		assertThat(parse("2.2.0"), greaterThan(parse("2.1.9")));
	}

	@Test
	public void comparePatchOnly() {
		assertThat(parse("2.2.4"), greaterThan(parse("2.2.3")));
	}

	@Test
	public void compareHigherVersion() {
		assertThat(parse("1.2.0.RELEASE"), greaterThan(parse("1.1.9.RELEASE")));
	}

	@Test
	public void compareHigherQualifier() {
		assertThat(parse("1.2.0.RC1"), greaterThan(parse("1.2.0.M1")));
	}

	@Test
	public void compareHigherQualifierVersion() {
		assertThat(parse("1.2.0.RC2"), greaterThan(parse("1.2.0.RC1")));
	}

	@Test
	public void compareLowerVersion() {
		assertThat(parse("1.0.5.RELEASE"), lessThan(parse("1.1.9.RELEASE")));
	}

	@Test
	public void compareLowerQualifier() {
		assertThat(parse("1.2.0.RC1"), lessThan(parse("1.2.0.RELEASE")));
	}

	@Test
	public void compareLessQualifierVersion() {
		assertThat(parse("1.2.0.RC2"), lessThan(parse("1.2.0.RC3")));
	}

	@Test
	public void compareWithNull() {
		assertThat(parse("1.2.0.RC2"), greaterThan(null));
	}

	@Test
	public void compareUnknownQualifier() {
		assertThat(parse("1.2.0.Beta"), lessThan(parse("1.2.0.CR")));
	}

	@Test
	public void compareUnknownQualifierVersion() {
		assertThat(parse("1.2.0.Beta1"), lessThan(parse("1.2.0.Beta2")));
	}

	@Test
	public void snapshotGreaterThanRC() {
		assertThat(parse("1.2.0.BUILD-SNAPSHOT"), greaterThan(parse("1.2.0.RC1")));
	}

	@Test
	public void snapshotLowerThanRelease() {
		assertThat(parse("1.2.0.BUILD-SNAPSHOT"), lessThan(parse("1.2.0.RELEASE")));
	}

	private static Version parse(String text) {
		return parser.parse(text);
	}

}
