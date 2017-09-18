/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * The {@code package.json} model object (simplified model, since some properties that are not
 * explicitly needed are ignored).
 */
public class PackageJson {

	/**
	 * Npm project name.
	 */
	private String name;

	/**
	 * Npm project version.
	 */
	private String version;

	/**
	 * Npm project dependencies.
	 */
	private final Map<String, String> dependencies;

	/**
	 * Npm project devDependencies.
	 */
	private final Map<String, String> devDependencies;

	/**
	 * List of available scripts.
	 */
	private final Map<String, String> scripts;

	/**
	 * Create default package json representation.
	 */
	PackageJson() {
		dependencies = new HashMap<>();
		devDependencies = new HashMap<>();
		scripts = new HashMap<>();
	}

	/**
	 * Get {@link #name}
	 *
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get {@link #version}
	 *
	 * @return {@link #version}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Get {@link #dependencies}
	 *
	 * @return {@link #dependencies}
	 */
	public Map<String, String> getDependencies() {
		return unmodifiableMap(dependencies);
	}

	/**
	 * Get {@link #devDependencies}
	 *
	 * @return {@link #devDependencies}
	 */
	public Map<String, String> getDevDependencies() {
		return unmodifiableMap(devDependencies);
	}

	/**
	 * Get {@link #scripts}
	 *
	 * @return {@link #scripts}
	 */
	public Map<String, String> getScripts() {
		return unmodifiableMap(scripts);
	}

	/**
	 * Check if given script command is defined in package.json file.
	 *
	 * @param script Script command.
	 * @return True if script command is defined, false otherwise.
	 */
	public boolean hasScript(String script) {
		return scripts.containsKey(script);
	}
}
