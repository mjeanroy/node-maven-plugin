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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {

	@Rule
	public ExpectedException thrown = none();

	private CommandExecutor commandExecutor;

	@Before
	public void setUp() {
		commandExecutor = new CommandExecutor();
	}

	@Test
	public void it_should_execute_success_command_on_unix() {
		assumeFalse(isWindows());

		String script = "success.sh";
		Command command = createUnixCommand(script);
		File workingDirectory = workingDirectory(script);
		OutputHandler out = mock(OutputHandler.class);

		CommandResult result = commandExecutor.execute(workingDirectory, command, out);

		assertThat(result.getStatus()).isZero();
	}

	@Test
	public void it_should_execute_error_command_on_unix() {
		assumeFalse(isWindows());

		String script = "error.sh";
		Command command = createUnixCommand(script);
		File workingDirectory = workingDirectory(script);
		OutputHandler out = mock(OutputHandler.class);

		CommandResult result = commandExecutor.execute(workingDirectory, command, out);

		assertThat(result.getStatus()).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_execute_success_command_on_windows() {
		assumeTrue(isWindows());

		String script = "success.bat";
		Command command = createMsDosCommand(script);
		File workingDirectory = workingDirectory(script);
		OutputHandler out = mock(OutputHandler.class);

		CommandResult result = commandExecutor.execute(workingDirectory, command, out);

		assertThat(result.getStatus()).isZero();
	}

	@Test
	public void it_should_execute_error_command_on_windows() {
		assumeTrue(isWindows());

		String script = "error.bat";
		Command command = createMsDosCommand(script);
		File workingDirectory = workingDirectory(script);
		OutputHandler logger = mock(OutputHandler.class);

		CommandResult result = commandExecutor.execute(workingDirectory, command, logger);

		assertThat(result.getStatus()).isNotZero().isEqualTo(1);
	}

	private Command createUnixCommand(String script) {
		String executable = "/bin/sh";

		Command command = mock(Command.class);
		when(command.getExecutable()).thenReturn(executable);
		when(command.getArguments()).thenReturn(singletonList(script));

		return command;
	}

	private Command createMsDosCommand(String script) {
		String executable = "cmd";

		Command command = mock(Command.class);
		when(command.getExecutable()).thenReturn(executable);
		when(command.getArguments()).thenReturn(asList("/C", script));

		return command;
	}

	private File workingDirectory(String script) {
		String path = getClass().getResource("/" + script).getPath();
		File file = new File(path);
		return file.getParentFile();
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}
