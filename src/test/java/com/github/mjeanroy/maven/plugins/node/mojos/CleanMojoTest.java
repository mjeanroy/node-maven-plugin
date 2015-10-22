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
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.io.File;

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

public class CleanMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Rule
	public MojoRule mojoRule = new MojoRule();

	@Rule
	public TestResources resources = new TestResources();

	@Test
	public void test_should_create_mojo() throws Exception {
		CleanMojo cleanMojo = createMojo("clean-mojo", false);
		assertThat(cleanMojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() throws Exception {
		CleanMojo cleanMojo = createMojo("clean-mojo-with-parameters", true);
		assertThat(cleanMojo).isNotNull();
		assertThat((Boolean) readField(cleanMojo, "color", true)).isTrue();
		assertThat((File) readField(cleanMojo, "workingDirectory", true)).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		CleanMojo cleanMojo = createMojo("clean-mojo-with-parameters", true);

		CommandResult result = createResult(true);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(cleanMojo, "executor", executor, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		Log logger = createLogger();
		writeField(cleanMojo, "log", logger, true);

		cleanMojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).contains(
				"run-script",
				"clean"
		);

		verify(logger).info("Running: npm run-script clean");
		verify(logger, never()).error(anyString());
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		CleanMojo cleanMojo = createMojo("clean-mojo-with-parameters", true);
		writeField(cleanMojo, "failOnError", false, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(cleanMojo, "executor", executor, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		Log logger = createLogger();
		writeField(cleanMojo, "log", logger, true);

		cleanMojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).contains(
				"run-script",
				"clean"
		);

		verify(logger).error("Error during execution of: npm run-script clean");
		verify(logger).error("Exit status: 1");
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() throws Exception {
		thrown.expect(MojoExecutionException.class);

		CleanMojo cleanMojo = createMojo("clean-mojo-with-parameters", true);
		writeField(cleanMojo, "failOnError", true, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(cleanMojo, "executor", executor, true);

		Log logger = createLogger();
		writeField(cleanMojo, "log", logger, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		cleanMojo.execute();
	}

	private CommandResult createResult(boolean success) {
		CommandResult result = mock(CommandResult.class);
		when(result.isSuccess()).thenReturn(success);
		when(result.isFailure()).thenReturn(!success);
		when(result.getStatus()).thenReturn(success ? 0 : 1);

		return result;
	}

	private CleanMojo createMojo(String projectName, boolean hasConfiguration) throws Exception {
		File baseDir = resources.getBasedir(projectName);
		File pom = new File(baseDir, "pom.xml");
		Mojo mojo = hasConfiguration ? mojoRule.lookupMojo("clean", pom) : mojoRule.lookupEmptyMojo("clean", pom);
		return (CleanMojo) mojo;
	}

	private Log createLogger() {
		return mock(Log.class);
	}
}
