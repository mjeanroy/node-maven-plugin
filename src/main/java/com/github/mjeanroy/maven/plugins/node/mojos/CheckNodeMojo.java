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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandException;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.model.EngineConfig;
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import com.vdurmont.semver4j.Semver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Objects;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;

/**
 * Check Mojo.
 *
 * <p>
 *
 * Basically, it checks for {@code node} and {@code npm}: these commands must be available and
 * mojo will fail if it is not the case.
 */
@Mojo(
	name = CheckNodeMojo.GOAL_NAME,
	defaultPhase = LifecyclePhase.VALIDATE,
	threadSafe = true
)
public class CheckNodeMojo extends AbstractNpmMojo {

	/**
	 * The maven goal name.
	 * This is the name that will be used in the {@code pom.xml} file.
	 */
	static final String GOAL_NAME = "check";

	/**
	 * Engine Configurations.
	 */
	@Parameter
	private EngineConfig engines;

	/**
	 * Create Mojo.
	 */
	public CheckNodeMojo() {
		super(newExecutor());
	}

	@Override
	public void execute() throws MojoExecutionException {
		if (shouldSkipGlobally()) {
			getLog().info("Goal 'check' is skipped.");
			return;
		}

		Command node = node();
		Command npm = npm();

		runAndCheckEngine(node);
		runAndCheckEngine(npm);

		Command npmClient = npmClient();
		if (!Objects.equals(npm.getName(), npmClient.getName())) {
			runAndCheckEngine(npmClient);
		}
	}

	/**
	 * Execute check operation.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private String run(Command cmd) throws MojoExecutionException {
		cmd.addArgument("--version");

		getLog().info("Checking " +  cmd.getName() + " command");
		getLog().debug("Running: " + cmd.toString());

		try {
			CommandResult result = execute(cmd);
			return result.getOut();
		}
		catch (CommandException ex) {
			throw new MojoExecutionException("Executable " + cmd.getName() + " is not available. Please install it on your operating system.");
		}
	}

	private EngineConfig computeEngineConfig() {
		if (engines != null) {
			return engines;
		}

		File packageJsonFile = lookupPackageJson(false);
		if (packageJsonFile != null) {
			PackageJson packageJson = parsePackageJson(packageJsonFile);
			return new EngineConfig(packageJson.isEngineStrict(), packageJson.getEngines());
		}

		return null;
	}

	private void runAndCheckEngine(Command command) throws MojoExecutionException {
		String out = run(command);
		checkEngine(command, out);
	}

	private void checkEngine(Command command, String out) throws MojoExecutionException {
		String name = command.getName();
		EngineConfig engineConfig = computeEngineConfig();
		if (engineConfig == null) {
			return;
		}

		String requirement = engineConfig.getRequiredEngine(name);
		if (requirement != null && !requirement.isEmpty() && !Objects.equals(requirement, "*")) {
			if (!checkEngineRequirement(out, requirement)) {
				String message = "Engine '" + name + "' with version '" + out + "' does not satisfy required version: '" + requirement + "'";
				if (engineConfig.isStrict()) {
					throw new MojoExecutionException(message);
				} else {
					getLog().warn(message);
				}
			}
		}
	}

	private boolean checkEngineRequirement(String actualVersion, String requiredVersion) throws MojoExecutionException {
		try {
			Semver semver = new Semver(actualVersion, Semver.SemverType.NPM);
			return semver.satisfies(requiredVersion);
		}
		catch (Exception ex) {
			getLog().error("An error occurred while checking for engine requirements: " + ex.getMessage());
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
