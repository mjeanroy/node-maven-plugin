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
import com.github.mjeanroy.maven.plugins.node.commands.CommandException;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.model.EngineConfig;
import com.github.zafarkhaja.semver.Version;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Objects;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.Strings.capitalize;

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
	defaultPhase = LifecyclePhase.VALIDATE
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
		this.engines = new EngineConfig();
	}

	@Override
	public void execute() throws MojoExecutionException {
		Command npm = npm();

		runAndCheckEngine(node());
		runAndCheckEngine(npm());

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
	private String check(Command cmd) throws MojoExecutionException {
		cmd.addArgument("--version");

		getLog().info("Checking " +  cmd.getName() + " command");
		getLog().debug("Running: " + cmd.toString());

		try {
			CommandResult result = execute(cmd);
			return result.getOut();
		}
		catch (CommandException ex) {
			throw new MojoExecutionException(capitalize(cmd.getName()) + " is not available. Please install it on your operating system.");
		}
	}

	private void runAndCheckEngine(Command command) throws MojoExecutionException {
		String out = check(command);
		String name = command.getName();
		String requirement = engines.getRequiredEngine(name);
		if (requirement != null && !requirement.isEmpty() && !Objects.equals(requirement, "*")) {
			if (!checkEngineRequirement(out, requirement)) {
				String message = "Engine '" + name + "' with version '" + out + "' does not satisfy required version: '" + requirement + "'";
				if (engines.isStrict()) {
					throw new MojoExecutionException(message);
				} else {
					getLog().warn(message);
				}
			}
		}
	}

	private boolean checkEngineRequirement(String actualVersion, String requiredVersion) throws MojoExecutionException {
		try {
			Version actual = Version.valueOf(fixVersionFormat(actualVersion));
			return actual.satisfies(requiredVersion);
		}
		catch (Exception ex) {
			getLog().error("An error occurred while checking for engine requirements: " + ex.getMessage());
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}

	private static String fixVersionFormat(String version) {
		if (version == null || version.isEmpty()) {
			return version;
		}

		String trimmedVersion = version.trim().toLowerCase();
		return trimmedVersion.charAt(0) == 'v' ? trimmedVersion.substring(1) : trimmedVersion;
	}
}
