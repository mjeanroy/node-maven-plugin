/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 Mickael Jeanroy
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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Configuration for incremental build.
 */
public final class IncrementalBuildConfiguration {

	/**
	 * If incremental build is enabled or not.
	 */
	private boolean enabled;

	/**
	 * Enable/Disable default file inclusion, default is {@code true}.
	 */
	private boolean useDefaultIncludes;

	/**
	 * Enable/Disable default file exclusion, default is {@code true}.
	 */
	private boolean useDefaultExcludes;

	/**
	 * The specific configuration for the INSTALL goal.
	 */
	private IncrementalBuildGoalConfiguration install;

	/**
	 * The specific configuration for the BOWER goal.
	 */
	private IncrementalBuildGoalConfiguration bower;

	/**
	 * The specific configuration for the LINT goal.
	 */
	private IncrementalBuildGoalConfiguration lint;

	/**
	 * The specific configuration for the BUILD goal.
	 */
	private IncrementalBuildGoalConfiguration build;

	/**
	 * The specific configuration for the PRE-CLEAN goal.
	 */
	private IncrementalBuildGoalConfiguration preClean;

	public IncrementalBuildConfiguration() {
		this.enabled = false;
		this.useDefaultIncludes = true;
		this.useDefaultExcludes = true;
		this.install = new IncrementalBuildGoalConfiguration();
		this.bower = new IncrementalBuildGoalConfiguration();
		this.lint = new IncrementalBuildGoalConfiguration();
		this.build = new IncrementalBuildGoalConfiguration();
		this.preClean = new IncrementalBuildGoalConfiguration();
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
	 * Get {@link #install}
	 *
	 * @return {@link #install}
	 */
	public IncrementalBuildGoalConfiguration getInstall() {
		return install;
	}

	/**
	 * Set {@link #install}
	 *
	 * @param install New {@link #install}
	 */
	public void setInstall(IncrementalBuildGoalConfiguration install) {
		this.install = install;
	}

	/**
	 * Get {@link #bower}
	 *
	 * @return {@link #bower}
	 */
	public IncrementalBuildGoalConfiguration getBower() {
		return bower;
	}

	/**
	 * Set {@link #bower}
	 *
	 * @param bower New {@link #bower}
	 */
	public void setBower(IncrementalBuildGoalConfiguration bower) {
		this.bower = bower;
	}

	/**
	 * Get {@link #lint}
	 *
	 * @return {@link #lint}
	 */
	public IncrementalBuildGoalConfiguration getLint() {
		return lint;
	}

	/**
	 * Set {@link #lint}
	 *
	 * @param lint New {@link #lint}
	 */
	public void setLint(IncrementalBuildGoalConfiguration lint) {
		this.lint = lint;
	}

	/**
	 * Get {@link #build}
	 *
	 * @return {@link #build}
	 */
	public IncrementalBuildGoalConfiguration getBuild() {
		return build;
	}

	/**
	 * Set {@link #build}
	 *
	 * @param build New {@link #build}
	 */
	public void setBuild(IncrementalBuildGoalConfiguration build) {
		this.build = build;
	}

	/**
	 * Get {@link #preClean}
	 *
	 * @return {@link #preClean}
	 */
	public IncrementalBuildGoalConfiguration getPreClean() {
		return preClean;
	}

	/**
	 * Set {@link #preClean}
	 *
	 * @param preClean New {@link #preClean}
	 */
	public void setPreClean(IncrementalBuildGoalConfiguration preClean) {
		this.preClean = preClean;
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

	/**
	 * Check if incremental build is enabled for the given goal.
	 *
	 * @param goal Goal name.
	 * @return {@code true} if incremental build is enabled for goal, {@code false} otherwise.
	 */
	public boolean isEnabled(String goal) {
		IncrementalBuildGoalConfiguration configuration = getGoalConfiguration(goal);
		return configuration != null && configuration.isEnabled();
	}

	/**
	 * Get all input entries for given goal.
	 *
	 * @param goal The goal.
	 * @return The included inputs.
	 */
	public Collection<String> getIncludes(String goal) {
		IncrementalBuildGoalConfiguration configuration = getGoalConfiguration(goal);
		return configuration == null ? Collections.<String>emptySet() : configuration.getIncludes();
	}

	/**
	 * Get all input entries for given goal.
	 *
	 * @param goal The goal.
	 * @return The included inputs.
	 */
	public Collection<String> getExcludes(String goal) {
		IncrementalBuildGoalConfiguration configuration = getGoalConfiguration(goal);
		return configuration == null ? Collections.<String>emptySet() : configuration.getExcludes();
	}

	/**
	 * Check if default file inclusion for given goal is enabled.
	 *
	 * @param goal The goal name.
	 * @return {@code true} if default file inclusion is enabled, {@code false} otherwise.
	 */
	public boolean useDefaultIncludes(String goal) {
		if (!useDefaultIncludes) {
			return false;
		}

		IncrementalBuildGoalConfiguration configuration = getGoalConfiguration(goal);
		return configuration == null || configuration.isUseDefaultIncludes();
	}

	/**
	 * Check if default file exclusion for given goal is enabled.
	 *
	 * @param goal The goal name.
	 * @return {@code true} if default file exclusion is enabled, {@code false} otherwise.
	 */
	public boolean useDefaultExcludes(String goal) {
		if (!useDefaultExcludes) {
			return false;
		}

		IncrementalBuildGoalConfiguration configuration = getGoalConfiguration(goal);
		return configuration == null || configuration.isUseDefaultExcludes();
	}

	private IncrementalBuildGoalConfiguration getGoalConfiguration(String goal) {
		if (Objects.equals(goal, "install")) {
			return install;
		}

		if (Objects.equals(goal, "bower")) {
			return bower;
		}

		if (Objects.equals(goal, "lint")) {
			return lint;
		}

		if (Objects.equals(goal, "build")) {
			return build;
		}

		if (Objects.equals(goal, "pre-clean")) {
			return preClean;
		}

		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof IncrementalBuildConfiguration) {
			IncrementalBuildConfiguration c = (IncrementalBuildConfiguration) o;
			return Objects.equals(enabled, c.enabled)
					&& Objects.equals(useDefaultIncludes, c.useDefaultIncludes)
					&& Objects.equals(useDefaultExcludes, c.useDefaultExcludes)
					&& Objects.equals(install, c.install)
					&& Objects.equals(bower, c.bower)
					&& Objects.equals(lint, c.lint)
					&& Objects.equals(build, c.build)
					&& Objects.equals(preClean, c.preClean);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, useDefaultExcludes, useDefaultIncludes, install, bower, lint, build, preClean);
	}

	@Override
	public String toString() {
		return ToStringBuilder.builder(getClass())
				.append("enabled", enabled)
				.append("useDefaultExcludes", useDefaultExcludes)
				.append("useDefaultIncludes", useDefaultIncludes)
				.append("install", install)
				.append("bower", bower)
				.append("lint", lint)
				.append("build", build)
				.append("preClean", preClean)
				.build();
	}
}
