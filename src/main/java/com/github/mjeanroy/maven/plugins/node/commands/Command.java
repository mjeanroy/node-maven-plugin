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

package com.github.mjeanroy.maven.plugins.node.commands;

import java.util.*;

import static com.github.mjeanroy.maven.plugins.node.commands.StringCommandArg.arg;

/**
 * Provide necessary api to execute a command line on operating system.
 *
 * <p>
 *
 * A command line is defined by:
 *
 * <ul>
 *   <li>An executable (such as {@code npm}).</li>
 *   <li>A list of optional arguments (such as {@code --no-color}).</li>
 * </ul>
 *
 * This class is not thread-safe, and adding / getting arguments should be
 * synchronized if needed.
 */
public class Command {

	/**
	 * Command executable file, this executable will be run on operating system.
	 */
	private final String executable;

	/**
	 * Optional arguments (each arguments will be unique).
	 */
	private final List<CommandArg> arguments;

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
	public void addArgument(CommandArg argument) {
		arguments.add(argument);
	}

	/**
	 * Add new argument to the command line.
	 *
	 * @param argument Argument.
	 */
	public void addArgument(String argument) {
		arguments.add(arg(argument));
	}

	/**
	 * Get executable path, can be used to execute command.
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
		String separator = "\\\\|/";
		String[] parts = executable.split(separator);
		return parts[parts.length - 1];
	}

	/**
	 * Get bin executable path, should not be used to execute command, as windows requires
	 * to use msdos wrapper.
	 *
	 * @return Bin path.
	 */
	public String getBin() {
		return executable;
	}

	/**
	 * Return unique list of arguments.
	 * Arguments are returned in the same order they were added.
	 *
	 * @return Arguments.
	 */
	public Collection<String> getArguments() {
		Set<String> args = new LinkedHashSet<>();
		for (CommandArg arg : arguments) {
			args.add(arg.toArgument());
		}

		return args;
	}

	@Override
	public String toString() {
		StringBuilder cmd = new StringBuilder();
		cmd.append(executable).append(" ");

		for (CommandArg arg : arguments) {
			cmd.append(arg.toString()).append(" ");
		}

		return cmd.toString().trim();
	}
}
