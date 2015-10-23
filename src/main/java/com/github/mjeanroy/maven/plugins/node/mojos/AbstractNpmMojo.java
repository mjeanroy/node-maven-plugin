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

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static java.util.Collections.unmodifiableSet;

public abstract class AbstractNpmMojo extends AbstractMojo {

	/**
	 * Store standard npm commands.
	 * Theses commands do not need to be prefixed by "run-script"
	 * argument.
	 */
	private static final Set<String> BASIC_COMMANDS;

	// Initialize commands
	static {
		Set<String> cmds = new HashSet<String>();
		cmds.add("install");
		cmds.add("test");
		cmds.add("publish");
		BASIC_COMMANDS = unmodifiableSet(cmds);
	}

	/**
	 * Check if given command need to be prefixed by "run-script"
	 * argument.
	 *
	 * @param command Command to check.
	 * @return True if command si a custom command and need to be prefixed by "run-script" argument, false otherwise.
	 */
	private static boolean needRunScript(String command) {
		return !BASIC_COMMANDS.contains(command);
	}

	/**
	 * Get the project base directory.
	 * This parameter is automatically provided by maven, but can be overridden by projects.
	 * This directory should contain `package.json` file.
	 */
	@Parameter(property = "workingDirectory", defaultValue = "${project.basedir}")
	private File workingDirectory;

	/**
	 * Flag to check if npm command should use colorization.
	 * Default is false, since colorization is not natively supported with Maven.
	 */
	@Parameter(defaultValue = "false")
	private boolean color;

	/**
	 * Should the build fail on error ?
	 * By default, build will fail with if an error occurs, but this may
	 * be ignored and let the build continue.
	 */
	@Parameter(defaultValue = "true")
	private boolean failOnError;

	/**
	 * Npm script command.
	 */
	private final String script;

	/**
	 * Executor used to run command line.
	 */
	private final CommandExecutor executor;

	/**
	 * Default Constructor.
	 */
	protected AbstractNpmMojo(String script) {
		this.script = script;
		this.executor = newExecutor();
	}

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		Command cmd = npm();

		// Append "run-script" if needed.
		if (needRunScript(script)) {
			cmd.addArgument("run-script");
		}

		cmd.addArgument(script);

		if (!color) {
			cmd.addArgument("--no-color");
		}

		getLog().info("Running: " + cmd.toString());
		executeCommand(cmd);
	}

	private void executeCommand(Command cmd) throws MojoExecutionException {
		CommandResult result = executor.execute(workingDirectory, cmd);
		if (result.isFailure()) {
			// Always display error log
			getLog().error("Error during execution of: " + cmd.toString());
			getLog().error("Exit status: " + result.getStatus());

			// Throw exception if npm command does not succeed
			if (failOnError) {
				throw new MojoExecutionException("Error during: " + cmd.toString());
			}
		} else {
			// Display success log
			getLog().debug("Execution succeed: " + cmd.toString());
		}
	}
}
