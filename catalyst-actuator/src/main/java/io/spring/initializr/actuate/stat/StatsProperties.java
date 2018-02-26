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

package io.spring.initializr.actuate.stat;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

/**
 * Statistics-related properties.
 *
 * @author Stephane Nicoll
 */
@ConfigurationProperties("initializr.stats")
public class StatsProperties {

	@NestedConfigurationProperty
	private final Elastic elastic = new Elastic();

	public Elastic getElastic() {
		return elastic;
	}

	public static final class Elastic {

		/**
		 * Elastic service uri.
		 */
		private String uri;

		/**
		 * Elastic service username.
		 */
		private String username;

		/**
		 * Elastic service password
		 */
		private String password;

		/**
		 * Name of the index.
		 */
		private String indexName = "initializr";

		/**
		 * Name of the entity to use to publish stats.
		 */
		private String entityName = "request";

		/**
		 * Number of attempts before giving up.
		 */
		private int maxAttempts = 3;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getIndexName() {
			return indexName;
		}

		public void setIndexName(String indexName) {
			this.indexName = indexName;
		}

		public String getEntityName() {
			return entityName;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public int getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = cleanUri(uri);
		}

		public URI getEntityUrl() {
			String string = uri + "/" + indexName + "/" + entityName;
			try {
				return new URI(string);
			}
			catch (URISyntaxException ex) {
				throw new IllegalStateException("Cannot create entity URL: " + string, ex);
			}
		}

		private static String cleanUri(String contextPath) {
			if (StringUtils.hasText(contextPath) && contextPath.endsWith("/")) {
				return contextPath.substring(0, contextPath.length() - 1);
			}
			return contextPath;
		}

	}

}
