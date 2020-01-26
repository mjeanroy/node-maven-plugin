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
package com.github.mjeanroy.maven.plugins.node.tests.builders;

import com.github.mjeanroy.maven.plugins.node.model.EngineConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.instantiate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;

/**
 * Builder for {@link EngineConfig}, to use in unit tests only.
 */
public class EngineConfigTestBuilder {

	/**
	 * The strict flag.
	 *
	 * @see EngineConfig#isStrict()
	 */
	private boolean strict;

	/**
	 * The requirements.
	 *
	 * @see EngineConfig#getRequirements()
	 */
	private final Map<String, String> requirements;

	/**
	 * Create builder with default values.
	 */
	public EngineConfigTestBuilder() {
		this.strict = false;
		this.requirements = new LinkedHashMap<>();
	}

	/**
	 * Update {@link #strict}
	 *
	 * @param strict New {@link #strict}
	 * @return The builder.
	 */
	public EngineConfigTestBuilder withStrict(boolean strict) {
		this.strict = strict;
		return this;
	}

	/**
	 * Add new requirement.
	 *
	 * @param name Engine name.
	 * @param value Engine requirement (i.e version).
	 * @return The builder.
	 */
	public EngineConfigTestBuilder addRequirement(String name, String value) {
		this.requirements.put(name, value);
		return this;
	}

	/**
	 * Build final {@link EngineConfig} instance.
	 *
	 * @return The new instance.
	 */
	public EngineConfig build() {
		EngineConfig engineConfig = instantiate(EngineConfig.class);
		writePrivate(engineConfig, "strict", strict);
		writePrivate(engineConfig, "requirements", requirements);
		return engineConfig;
	}
}
