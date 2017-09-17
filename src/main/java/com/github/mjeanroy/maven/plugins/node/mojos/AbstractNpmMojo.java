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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.Commands;
import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.commons.JsonUtils.parseJson;
import static com.github.mjeanroy.maven.plugins.node.commons.PreConditions.notNull;

abstract class AbstractNpmMojo extends AbstractMojo {

	/**
	 * Get the project base directory.
	 * This parameter is automatically provided by maven, but can be overridden by projects.
	 *
	 * <p>
	 *
	 * This directory should contain {@code package.json} file.
	 */
	@Parameter(property = "workingDirectory", defaultValue = "${project.basedir}")
	private File workingDirectory;

	/**
	 * Get {@code node} path.
	 * The path should point to the node executable file.
	 */
	@Parameter(property = "node.path", defaultValue = "node")
	private String nodePath;

	/**
	 * Get {@code npm} path.
	 * The path should point to the npm executable file.
	 */
	@Parameter(property = "npm.path", defaultValue = "npm")
	private String npmPath;

	/**
	 * Get npm path.
	 * The path should point to the npm executable file.
	 */
	@Parameter(property = "yarn.path", defaultValue = "yarn")
	private String yarnPath;

	/**
	 * Flag to check if yarn command should be used instead of npm to install dependencies.
	 * Default is false, since yarn may not be installed.
	 */
	@Parameter(property = "yarn", defaultValue = "false")
	private boolean yarn;

	/**
	 * Default Constructor.
	 */
	AbstractNpmMojo() {
	}

	/**
	 * Get {@link #workingDirectory}
	 *
	 * @return {@link #workingDirectory}
	 */
	File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Check if yarn should be used instead of npm to install dependencies.
	 *
	 * @return {@code true} if {@code yarn} should be used to install dependencies, {@code false} otherwise.
	 */
	boolean isUseYarn() {
		return yarn;
	}

	/**
	 * Return {@code package.json} content.
	 *
	 * @return Instance of {@code package.json} content.
	 */
	PackageJson getPackageJson() {
		File workingDirectory = notNull(getWorkingDirectory(), "Working Directory must not be null");
		getLog().debug("Searching for package.json file in: " + workingDirectory);

		File packageJson = new File(workingDirectory, "package.json");
		if (!packageJson.exists()) {
			getLog().error("Missing package.json file");
			throw new PackageJsonNotFoundException(packageJson);
		}

		return parseJson(packageJson, PackageJson.class);
	}

	/**
	 * Create new {@code npm} command instance.
	 *
	 * @return NPM Command.
	 */
	Command npm() {
		return Commands.npm(npmPath);
	}

	/**
	 * Create new yarn command instance.
	 *
	 * @return Yarn Command.
	 */
	Command yarn() {
		return Commands.yarn(yarnPath);
	}

	/**
	 * Create new {@code node} command instance.
	 *
	 * @return NODE Command.
	 */
	Command node() {
		return Commands.node(nodePath);
	}

	/**
	 * Create new logger for {@code npm} command output (use the appropriate maven
	 * log level, depending on NPM log level).
	 *
	 * @return NPM Logger.
	 */
	NpmLogger npmLogger() {
		return NpmLogger.npmLogger(getLog());
	}
}
