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
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

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
	public void execute() throws MojoExecutionException {
		check(node());
		check(npm());

		if (isUseYarn()) {
			check(yarn());
		}
	}

	/**
	 * Execute check operation.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private void check(Command cmd) throws MojoExecutionException {
		cmd.addArgument("--version");

		getLog().info("Checking " + cmd.getName() + " command");
		getLog().debug("Running: " + cmd.toString());

		try {
			executor.execute(getWorkingDirectory(), cmd, npmLogger());
		}
		catch (CommandException ex) {
			throw new MojoExecutionException(capitalize(cmd.getName()) + " is not available. Please install it on your operating system.");
		}
	}
}
