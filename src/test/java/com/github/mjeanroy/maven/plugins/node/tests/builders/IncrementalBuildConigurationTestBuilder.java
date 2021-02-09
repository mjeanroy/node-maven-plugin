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

package com.github.mjeanroy.maven.plugins.node.tests.builders;

import com.github.mjeanroy.maven.plugins.node.model.IncrementalBuildConfiguration;

/**
 * Builder for {@link IncrementalBuildConfiguration}.
 */
public class IncrementalBuildConigurationTestBuilder {

	/**
	 * Build new {@link IncrementalBuildConfiguration} instance with {@link #enabled} flag.
	 *
	 * @param enabled Enable/Disable incremental build.
	 * @return The new {@link IncrementalBuildConfiguration} instance.
	 */
	public static IncrementalBuildConfiguration of(boolean enabled) {
		return new IncrementalBuildConigurationTestBuilder().withEnabled(enabled).build();
	}

	/**
	 * Enable/Disable incremental build.
	 *
	 * @see IncrementalBuildConfiguration#isEnabled()
	 */
	private boolean enabled;

	/**
	 * Initialize builder with default values.
	 */
	public IncrementalBuildConigurationTestBuilder() {
		this.enabled = true;
	}

	/**
	 * Update {@link #enabled}
	 *
	 * @param enabled New {@link #enabled}
	 * @return The builder.
	 */
	public IncrementalBuildConigurationTestBuilder withEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/**
	 * Build final {@link IncrementalBuildConfiguration} instance.
	 *
	 * @return The instance.
	 */
	public IncrementalBuildConfiguration build() {
		IncrementalBuildConfiguration incrementalBuildConfiguration = new IncrementalBuildConfiguration();
		incrementalBuildConfiguration.setEnabled(enabled);
		return incrementalBuildConfiguration;
	}
}
