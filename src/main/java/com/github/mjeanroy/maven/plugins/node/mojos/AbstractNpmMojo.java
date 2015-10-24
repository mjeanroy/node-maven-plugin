/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.commons.JsonUtils.parseJson;

public abstract class AbstractNpmMojo extends AbstractMojo {

	/**
	 * Get the project base directory.
	 * This parameter is automatically provided by maven, but can be overridden by projects.
	 * This directory should contain `package.json` file.
	 */
	@Parameter(property = "workingDirectory", defaultValue = "${project.basedir}")
	private File workingDirectory;

	/**
	 * Default Constructor.
	 */
	protected AbstractNpmMojo() {
	}

	/**
	 * Get {@link #workingDirectory}
	 *
	 * @return {@link #workingDirectory}
	 */
	protected File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Return package.json content.
	 *
	 * @return Instance of package.json content.
	 */
	protected PackageJson getPackageJson() {
		File workingDirectory = getWorkingDirectory();
		getLog().debug("Searching for package.json file in: " + workingDirectory);

		File packageJson = new File(workingDirectory, "package.json");
		if (!packageJson.exists()) {
			getLog().error("Missing package.json file");
			throw new PackageJsonNotFoundException(packageJson);
		}

		return parseJson(packageJson, PackageJson.class);
	}

}
