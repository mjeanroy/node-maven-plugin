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
import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractNpmScriptMojoTest<T extends AbstractNpmScriptMojo> extends AbstractNpmMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Test
	public void test_should_create_mojo() throws Exception {
		T mojo = createMojo("mojo", false);
		assertThat(mojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		assertThat(mojo).isNotNull();
		assertThat((Boolean) readField(mojo, "color", true)).isTrue();
		assertThat((File) readField(mojo, "workingDirectory", true)).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);

		CommandResult result = createResult(true);
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).containsExactlyElementsOf(defaultArguments());

		Log logger = (Log) readField(mojo, "log", true);
		verify(logger).info("Running: npm " + join(defaultArguments()));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void it_should_execute_mojo_in_success_with_custom_script() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		writeField(mojo, "script", "foobar", true);

		CommandResult result = createResult(true);
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).containsExactly(
				"run-script",
				"foobar"
		);

		Log logger = (Log) readField(mojo, "log", true);
		verify(logger).info("Running: npm run-script foobar");
		verify(logger, never()).error(anyString());
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		writeField(mojo, "failOnError", false, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).containsExactlyElementsOf(defaultArguments());

		Log logger = (Log) readField(mojo, "log", true);
		verify(logger).error("Error during execution of: npm " + join(defaultArguments()));
		verify(logger).error("Exit status: 1");
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() throws Exception {
		thrown.expect(MojoExecutionException.class);

		T mojo = createMojo("mojo-with-parameters", true);
		writeField(mojo, "failOnError", true, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();
	}

	@Test
	public void it_throw_exception_if_scripts_does_not_exist() throws Exception {
		if (!isStandardNpm()) {
			thrown.expect(MojoExecutionException.class);
			thrown.expectMessage("Cannot execute npm " + join(defaultArguments()) + " command: it is not defined in package.json");
		}

		T mojo = createMojo("mojo-without-scripts", false);

		CommandResult result = createResult(false);
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();
	}

	@Test
	public void it_throw_exception_if_package_json_does_not_exist() throws Exception {
		File workingDirectory = new File(".");

		thrown.expect(PackageJsonNotFoundException.class);
		thrown.expectMessage("File " + new File(workingDirectory.getAbsolutePath(), "package.json") + " does not exist");

		T mojo = createMojo("mojo-with-parameters", true);
		writeField(mojo, "failOnError", false, true);
		writeField(mojo, "workingDirectory", workingDirectory, true);

		mojo.execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T createMojo(String projectName, boolean hasConfiguration) throws Exception {
		T mojo = super.createMojo(projectName, hasConfiguration);

		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(mojo, "executor", executor, true);

		return mojo;
	}

	private boolean isStandardNpm() {
		String mojoName = mojoName();
		return mojoName.equals("test") || mojoName.equals("install");
	}

	private List<String> defaultArguments() {
		List<String> arguments = new ArrayList<String>();

		String mojoName = mojoName();
		if (!isStandardNpm()) {
			arguments.add("run-script");
		}

		arguments.add(mojoName);
		return arguments;
	}

	private String join(List<String> arguments) {
		StringBuilder sb = new StringBuilder();
		for (String arg : arguments) {
			sb.append(arg).append(" ");
		}
		return sb.toString().trim();
	}
}
