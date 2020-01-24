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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.commands.Commands;
import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.commons.io.Files.getNormalizeAbsolutePath;
import static com.github.mjeanroy.maven.plugins.node.commons.json.Jsons.parseJson;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.PreConditions.notNull;

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
	@Parameter(property = "npm.path")
	private String npmPath;

	/**
	 * Get npm path.
	 * The path should point to the npm executable file.
	 */
	@Parameter(property = "yarn.path")
	private String yarnPath;

	/**
	 * Flag to check if yarn command should be used instead of npm to install dependencies.
	 * Default is false, since yarn may not be installed.
	 */
	@Parameter(property = "yarn")
	private boolean yarn;

	/**
	 * Flag to check if yarn command should be used instead of npm to install dependencies.
	 * Default is false, since yarn may not be installed.
	 */
	@Parameter(property = "npmClient", defaultValue = "${npm.client}")
	private String npmClient;

	/**
	 * A list of environment variables that will be set during command executions.
	 */
	@Parameter(property = "environment")
	private Map<String, String> environment;

	/**
	 * The command executor.
	 */
	private final CommandExecutor executor;

	/**
	 * Default Constructor.
	 */
	AbstractNpmMojo(CommandExecutor executor) {
		this.executor = executor;
		this.environment = new LinkedHashMap<>();
	}

	/**
	 * Get {@link #workingDirectory}
	 *
	 * @return {@link #workingDirectory}
	 */
	final File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Create new npm client command instance.
	 *
	 * @return NPM Client.
	 */
	final Command npmClient() {
		if (yarn || yarnPath != null) {
			return yarn();
		}

		if (npmPath != null) {
			return npm();
		}

		return Commands.npmClient(npmClient);
	}

	/**
	 * Create new {@code node} command instance.
	 *
	 * @return NODE Command.
	 */
	final Command node() {
		return Commands.node(nodePath);
	}

	/**
	 * Create new {@code npm} command instance.
	 *
	 * @return NPM Command.
	 */
	final Command npm() {
		if (npmPath != null) {
			getLog().warn("Parameter 'npmPath' is deprecated, please use 'npmClient' instead.");
		}

		return Commands.npm(npmPath);
	}

	/**
	 * Lookup for `package.json` file in current working directory.
	 *
	 * @return The `package.json` file.
	 */
	final File lookupPackageJson() {
		File workingDirectory = notNull(getWorkingDirectory(), "Working Directory must not be null");
		getLog().debug("Searching for package.json file in: " + workingDirectory);

		String absolutePath = getNormalizeAbsolutePath(workingDirectory);
		File packageJson = new File(absolutePath, "package.json");
		if (!packageJson.exists()) {
			getLog().error("Missing package.json file, cannot find it in: " + workingDirectory);
			throw new PackageJsonNotFoundException(packageJson);
		}

		return packageJson;
	}

	/**
	 * Parse {@code package.json} content.
	 *
	 * @param packageJson The packageJson file.
	 * @return Instance of {@code package.json} content.
	 */
	final PackageJson parsePackageJson(File packageJson) {
		return parseJson(packageJson, PackageJson.class);
	}

	/**
	 * Execute given command.
	 *
	 * @param cmd The command to execute.
	 * @return The execution result.
	 */
	final CommandResult execute(Command cmd) {
		return executor.execute(workingDirectory, cmd, npmLogger(), environment);
	}

	/**
	 * Create new yarn command instance.
	 *
	 * @return Yarn Command.
	 */
	private Command yarn() {
		if (yarn) {
			getLog().warn("Parameter 'yarn' is deprecated, please use 'npmClient' instead.");
		}

		if (yarnPath != null) {
			getLog().warn("Parameter 'yarnPath' is deprecated, please use 'npmClient' instead.");
		}

		return Commands.yarn(yarnPath);
	}

	/**
	 * Create new logger for {@code npm} command output (use the appropriate maven
	 * log level, depending on NPM log level).
	 *
	 * @return NPM Logger.
	 */
	private NpmLogger npmLogger() {
		return NpmLogger.npmLogger(getLog());
	}
}
