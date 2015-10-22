package com.github.mjeanroy.maven.plugins.node.commands;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeFalse;
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
	public void it_should_execute_success_command_on_unix() throws Exception {
		assumeFalse(isWindows());

		String script = "success.sh";
		Command command = createUnixCommand(script);
		File workingDirectory = workingDirectory(script);

		CommandResult result = commandExecutor.execute(workingDirectory, command);

		assertThat(result.getStatus()).isZero();
	}

	@Test
	public void it_should_execute_error_command_on_unix() throws Exception {
		assumeFalse(isWindows());

		String script = "error.sh";
		Command command = createUnixCommand(script);
		File workingDirectory = workingDirectory(script);

		CommandResult result = commandExecutor.execute(workingDirectory, command);

		assertThat(result.getStatus()).isNotZero().isEqualTo(1);
	}

	private Command createUnixCommand(String script) {
		String executable = "/bin/sh";

		Command command = mock(Command.class);
		when(command.getExecutable()).thenReturn(executable);
		when(command.getArguments()).thenReturn(asList(
				script
		));

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
