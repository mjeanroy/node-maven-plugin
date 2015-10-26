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

package com.github.mjeanroy.maven.plugins.node.commands;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Provide necessary api to execute a command line on operating system.
 * A command line is defined by:
 * - An executable (such as `npm` for instance).
 * - A list of optional arguments (such as `--no-color` for instance).
 *
 * This class is not thread-safe, and adding / getting arguments should be
 * synchronized if needed.
 */
public class Command {

	/**
	 * Command executable file.
	 * This executable will be run on operating system.
	 */
	private final String executable;

	/**
	 * Optional arguments.
	 * Each arguments will be unique.
	 */
	private final List<String> arguments;

	/**
	 * Create new command providing executable path.
	 *
	 * @param executable Executable path.
	 */
	Command(String executable) {
		this.executable = executable;
		this.arguments = new LinkedList<>();
	}

	/**
	 * Add new argument to the command line.
	 *
	 * @param argument Argument.
	 */
	public void addArgument(String argument) {
		arguments.add(argument);
	}

	/**
	 * Get executable path.
	 *
	 * @return Executable path.
	 */
	public String getExecutable() {
		return executable;
	}

	/**
	 * Get executable name.
	 *
	 * @return Executable name.
	 */
	public String getName() {
		return executable;
	}

	/**
	 * Return unique list of arguments.
	 * Arguments are returned in the same order they were added.
	 *
	 * @return Arguments.
	 */
	public Collection<String> getArguments() {
		return new LinkedHashSet<>(arguments);
	}

	@Override
	public String toString() {
		StringBuilder cmd = new StringBuilder();
		cmd.append(executable).append(" ");

		for (String arg : arguments) {
			cmd.append(arg).append(" ");
		}

		return cmd.toString().trim();
	}
}
