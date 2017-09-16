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

package com.github.mjeanroy.maven.plugins.node.commands;

import org.apache.commons.exec.*;

import java.io.File;
import java.io.IOException;

/**
 * Execute command line (i.e instance of {@link Command} object.
 * A factory should be used to create new executor, using {@link CommandExecutors} static methods.
 */
public class CommandExecutor {

	// Package private, use static factory instead.
	CommandExecutor() {
	}

	/**
	 * Execute command line and return the result status.
	 *
	 * @param workingDirectory Working directory (i.e where the command line is executed).
	 * @param command Command, containing executable path with arguments.
	 * @param logger Logger to use to log command output.
	 * @return Command result object.
	 */
	public CommandResult execute(File workingDirectory, Command command, OutputHandler logger) {
		CommandLine commandLine = new CommandLine(command.getExecutable());
		for (String argument : command.getArguments()) {
			commandLine.addArgument(argument);
		}

		try {
			Executor executor = new DefaultExecutor();
			executor.setWorkingDirectory(workingDirectory);
			executor.setExitValue(0);

			// Define custom output stream
			LogStreamHandler stream = new LogStreamHandler(logger);
			PumpStreamHandler handler = new PumpStreamHandler(stream);
			executor.setStreamHandler(handler);

			int status = executor.execute(commandLine);
			return new CommandResult(status);
		}
		catch (ExecuteException ex) {
			return new CommandResult(ex.getExitValue());
		}
		catch (IOException ex) {
			throw new CommandException(ex);
		}
	}
}
