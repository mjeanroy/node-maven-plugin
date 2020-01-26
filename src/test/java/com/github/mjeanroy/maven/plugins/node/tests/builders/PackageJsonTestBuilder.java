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

import com.github.mjeanroy.maven.plugins.node.model.PackageJson;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.instantiate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;

/**
 * Builder for {@link PackageJson}, to use in unit tests only.
 */
public class PackageJsonTestBuilder {

	/**
	 * Package Name.
	 *
	 * @see PackageJson#getName()
	 */
	private String name;

	/**
	 * Package Version.
	 *
	 * @see PackageJson#getVersion()
	 */
	private String version;

	/**
	 * Package Dependencies.
	 *
	 * @see PackageJson#getDependencies()
	 */
	private final Map<String, String> dependencies;

	/**
	 * Package Dev Dependencies.
	 *
	 * @see PackageJson#getDevDependencies()
	 */
	private final Map<String, String> devDependencies;

	/**
	 * Package Scripts.
	 *
	 * @see PackageJson#getScripts()
	 */
	private final Map<String, String> scripts;

	/**
	 * Create builder with default values.
	 */
	public PackageJsonTestBuilder() {
		this.name = "my-awesome-package";
		this.version = "0.1.0";
		this.dependencies = new LinkedHashMap<>();
		this.devDependencies = new LinkedHashMap<>();
		this.scripts = new LinkedHashMap<>();
	}

	/**
	 * Update {@link #name}
	 *
	 * @param name New {@link #name}
	 * @return The builder.
	 */
	public PackageJsonTestBuilder withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Update {@link #version}
	 *
	 * @param version New {@link #version}
	 * @return The builder.
	 */
	public PackageJsonTestBuilder withVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * Add new dependency.
	 *
	 * @param name Dependency name.
	 * @param value Dependency value (i.e version).
	 * @return The builder.
	 */
	public PackageJsonTestBuilder addDependency(String name, String value) {
		this.dependencies.put(name, value);
		return this;
	}

	/**
	 * Add new dev dependency.
	 *
	 * @param name Dependency name.
	 * @param value Dependency value (i.e version).
	 * @return The builder.
	 */
	public PackageJsonTestBuilder addDevDependency(String name, String value) {
		this.devDependencies.put(name, value);
		return this;
	}

	/**
	 * Add new script entry.
	 *
	 * @param name Script name.
	 * @param value Script value (i.e the command to run).
	 * @return The builder.
	 */
	public PackageJsonTestBuilder addScript(String name, String value) {
		this.scripts.put(name, value);
		return this;
	}

	/**
	 * Build final {@link PackageJson} instance.
	 *
	 * @return The new instance.
	 */
	public PackageJson build() {
		PackageJson packageJson = instantiate(PackageJson.class);
		writePrivate(packageJson, "name", name);
		writePrivate(packageJson, "version", version);
		writePrivate(packageJson, "dependencies", new LinkedHashMap<>(dependencies));
		writePrivate(packageJson, "devDependencies", new LinkedHashMap<>(devDependencies));
		writePrivate(packageJson, "scripts", new LinkedHashMap<>(scripts));
		return packageJson;
	}
}
