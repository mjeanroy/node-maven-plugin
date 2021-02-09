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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Incremental build configuration for specific goal.
 */
public final class IncrementalBuildGoalConfiguration {

	/**
	 * If incremental build for the specific goal is enabled or not.
	 */
	private boolean enabled;

	/**
	 * The list of included input files.
	 */
	private List<String> includes;

	/**
	 * The list of included input files.
	 */
	private List<String> excludes;

	/**
	 * Enable/Disable default file inclusion, default is {@code true}.
	 */
	private boolean useDefaultIncludes;

	/**
	 * Enable/Disable default file exclusion, default is {@code true}.
	 */
	private boolean useDefaultExcludes;

	public IncrementalBuildGoalConfiguration() {
		this.enabled = true;
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
		this.useDefaultIncludes = true;
		this.useDefaultExcludes = true;
	}

	/**
	 * Get {@link #enabled}
	 *
	 * @return {@link #enabled}
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set {@link #enabled}
	 *
	 * @param enabled New {@link #enabled}
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Get {@link #includes}
	 *
	 * @return {@link #includes}
	 */
	public List<String> getIncludes() {
		return includes;
	}

	/**
	 * Set {@link #includes}
	 *
	 * @param includes New {@link #includes}
	 */
	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	/**
	 * Get {@link #excludes}
	 *
	 * @return {@link #excludes}
	 */
	public List<String> getExcludes() {
		return excludes;
	}

	/**
	 * Set {@link #excludes}
	 *
	 * @param excludes New {@link #excludes}
	 */
	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	/**
	 * Get {@link #useDefaultIncludes}
	 *
	 * @return {@link #useDefaultIncludes}
	 */
	public boolean isUseDefaultIncludes() {
		return useDefaultIncludes;
	}

	/**
	 * Set {@link #useDefaultIncludes}
	 *
	 * @param useDefaultIncludes New {@link #useDefaultIncludes}
	 */
	public void setUseDefaultIncludes(boolean useDefaultIncludes) {
		this.useDefaultIncludes = useDefaultIncludes;
	}

	/**
	 * Get {@link #useDefaultExcludes}
	 *
	 * @return {@link #useDefaultExcludes}
	 */
	public boolean isUseDefaultExcludes() {
		return useDefaultExcludes;
	}

	/**
	 * Set {@link #useDefaultExcludes}
	 *
	 * @param useDefaultExcludes New {@link #useDefaultExcludes}
	 */
	public void setUseDefaultExcludes(boolean useDefaultExcludes) {
		this.useDefaultExcludes = useDefaultExcludes;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof IncrementalBuildGoalConfiguration) {
			IncrementalBuildGoalConfiguration c = (IncrementalBuildGoalConfiguration) o;
			return Objects.equals(enabled, c.enabled)
					&& Objects.equals(useDefaultIncludes, c.useDefaultIncludes)
					&& Objects.equals(useDefaultExcludes, c.useDefaultExcludes)
					&& Objects.equals(includes, c.includes)
					&& Objects.equals(excludes, c.excludes);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, useDefaultIncludes, useDefaultExcludes, includes, excludes);
	}

	@Override
	public String toString() {
		return ToStringBuilder.builder(getClass())
				.append("enabled", enabled)
				.append("useDefaultIncludes", useDefaultIncludes)
				.append("useDefaultExcludes", useDefaultExcludes)
				.append("includes", includes)
				.append("excludes", excludes)
				.build();
	}
}
