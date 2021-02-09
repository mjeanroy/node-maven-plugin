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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NullCommandExecutorTest {

	private NullCommandExecutor commandExecutor;

	@Before
	public void setUp() {
		commandExecutor = NullCommandExecutor.getInstance();
	}

	@Test
	public void it_should_execute_success_command_on_unix() {
		String script = "success.sh";
		Command command = createUnixCommand(script);
		File workingDirectory = workingDirectory(script);
		OutputHandler out = mock(OutputHandler.class);
		Map<String, String> environment = Collections.emptyMap();

		CommandResult result = commandExecutor.execute(workingDirectory, command, out, environment);

		assertThat(result.getStatus()).isZero();
	}

	private static Command createUnixCommand(String script) {
		Command command = new Command("/bin/sh");
		command.addArgument(script);
		return command;
	}

	private static File workingDirectory(String script) {
		String path = NullCommandExecutorTest.class.getResource("/" + script).getPath();
		File file = new File(path);
		return file.getParentFile();
	}
}
