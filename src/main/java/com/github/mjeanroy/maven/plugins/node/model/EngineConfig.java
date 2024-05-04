/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mjeanroy.maven.plugins.node.model;

import com.github.mjeanroy.maven.plugins.node.commons.lang.ToStringBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.unmodifiableMap;

/**
 * Node Engine Configuration.
 */
public final class EngineConfig {

	/**
	 * If set to {@code true}, then {@code check} mojo will fail if require engine is not compatible with the current environment.
	 */
	private boolean strict;

	/**
	 * Versions of node/npm/any other npmClient that your stuff works on.
	 */
	private Map<String, String> requirements;

	/**
	 * Create default config.
	 */
	public EngineConfig() {
		this.strict = false;
		this.requirements = new LinkedHashMap<>();
	}

	/**
	 * Create configuration instance.
	 *
	 * @param strict       {@link #strict}
	 * @param requirements {@link #requirements}
	 */
	public EngineConfig(boolean strict, Map<String, String> requirements) {
		this.strict = strict;
		this.requirements = new LinkedHashMap<>(requirements);
	}

	/**
	 * Get {@link #strict}
	 *
	 * @return {@link #strict}
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Set {@link #strict}
	 *
	 * @param strict New {@link #strict}
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Get {@link #requirements}
	 *
	 * @return {@link #requirements}
	 */
	public Map<String, String> getRequirements() {
		return unmodifiableMap(requirements);
	}

	/**
	 * New {@link #requirements}
	 *
	 * @param requirements New {@link #requirements}
	 */
	public void setRequirements(Map<String, String> requirements) {
		this.requirements = requirements;
	}

	/**
	 * Get the required setting for given engine.
	 *
	 * @param id Engine identifier (i.e {@code "node"}, {@code "npm"}, etc.).
	 * @return The engine configuration.
	 */
	public String getRequiredEngine(String id) {
		return requirements.get(id);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof EngineConfig) {
			EngineConfig e = (EngineConfig) o;
			return Objects.equals(strict, e.strict) && Objects.equals(requirements, e.requirements);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(strict, requirements);
	}

	@Override
	public String toString() {
		return ToStringBuilder.builder(getClass())
				.append("strict", strict)
				.append("requirements", requirements)
				.build();
	}
}
