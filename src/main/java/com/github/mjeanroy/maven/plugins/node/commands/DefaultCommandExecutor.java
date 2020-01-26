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

package com.github.mjeanroy.maven.plugins.node.commands;

import org.apache.commons.exec.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Execute command line (i.e instance of {@link Command} object.
 *
 * <p>
 *
 * A factory should be used to create new executor, using {@link CommandExecutors} static methods.
 */
class DefaultCommandExecutor implements CommandExecutor {

	/**
	 * The command executor instance.
	 */
	private static final DefaultCommandExecutor INSTANCE = new DefaultCommandExecutor();

	/**
	 * Get the command executor.
	 *
	 * @return The command executor.
	 */
	static DefaultCommandExecutor getInstance() {
		return INSTANCE;
	}

	private DefaultCommandExecutor() {
	}

	@Override
	public CommandResult execute(File workingDirectory, Command command, OutputHandler outputHandler, Map<String, String> environment) {
		CommandLine commandLine = new CommandLine(command.getExecutable());
		for (String argument : command.getArguments()) {
			commandLine.addArgument(argument);
		}

		CaptureOutputHandler captureOutputHandler = new CaptureOutputHandler();

		try {
			Executor executor = new DefaultExecutor();
			executor.setWorkingDirectory(workingDirectory);
			executor.setExitValue(0);

			// Define custom output stream
			LogStreamHandler stream = new LogStreamHandler(new CompositeOutputHandler(asList(
					outputHandler,
					captureOutputHandler
			)));

			executor.setStreamHandler(
					new PumpStreamHandler(stream)
			);

			int status = executor.execute(commandLine, environment);
			return new CommandResult(status, captureOutputHandler.getOut());
		}
		catch (ExecuteException ex) {
			return new CommandResult(ex.getExitValue(), captureOutputHandler.getOut());
		}
		catch (IOException ex) {
			throw new CommandException(ex);
		}
	}
}
