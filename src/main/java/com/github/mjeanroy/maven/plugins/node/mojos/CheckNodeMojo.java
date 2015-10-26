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
import com.github.mjeanroy.maven.plugins.node.commands.CommandException;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.node;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static com.github.mjeanroy.maven.plugins.node.commons.StringUtils.capitalize;

/**
 * Check Mojo.
 * Basically, it checks for node and npm.
 * These commands must be available and mojo will fail if it
 * is not the case.
 */
@Mojo(
	name = "check",
	defaultPhase = LifecyclePhase.VALIDATE,
	requiresOnline = false
)
public class CheckNodeMojo extends AbstractNpmMojo {

	/**
	 * Executor used to run command line.
	 */
	private final CommandExecutor executor;

	/**
	 * Create Mojo.
	 */
	public CheckNodeMojo() {
		super();
		this.executor = newExecutor();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		check("node", node());
		check("npm", npm());
	}

	/**
	 * Execute check operation.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private void check(String executable, Command cmd) throws MojoExecutionException {
		cmd.addArgument("--version");

		getLog().info("Checking " + executable + " command");
		getLog().debug("Running: " + cmd.toString());

		try {
			executor.execute(getWorkingDirectory(), cmd, getLog());
		}
		catch (CommandException ex) {
			throw new MojoExecutionException(capitalize(cmd.getName()) + " is not available, please install it on your operating system");
		}
	}
}
