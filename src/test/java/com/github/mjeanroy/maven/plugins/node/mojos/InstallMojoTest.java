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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
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

public class InstallMojoTest extends AbstractMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Override
	protected String mojoName() {
		return "install";
	}

	@Test
	public void test_should_create_mojo() throws Exception {
		InstallMojo mojo = createMojo("clean-mojo", false);
		assertThat(mojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() throws Exception {
		InstallMojo mojo = createMojo("clean-mojo-with-parameters", true);
		assertThat(mojo).isNotNull();
		assertThat((Boolean) readField(mojo, "color", true)).isTrue();
		assertThat((File) readField(mojo, "workingDirectory", true)).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		InstallMojo mojo = createMojo("clean-mojo-with-parameters", true);

		CommandResult result = createResult(true);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(mojo, "executor", executor, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		mojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).contains(
				"install"
		);

		verify(logger).info("Running: npm install");
		verify(logger, never()).error(anyString());
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		InstallMojo mojo = createMojo("clean-mojo-with-parameters", true);
		writeField(mojo, "failOnError", false, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(mojo, "executor", executor, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		mojo.execute();

		verify(executor).execute(any(File.class), any(Command.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getExecutable()).isEqualTo("npm");
		assertThat(cmd.getArguments()).contains(
				"install"
		);

		verify(logger).error("Error during execution of: npm install");
		verify(logger).error("Exit status: 1");
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() throws Exception {
		thrown.expect(MojoExecutionException.class);

		InstallMojo mojo = createMojo("clean-mojo-with-parameters", true);
		writeField(mojo, "failOnError", true, true);

		CommandResult result = createResult(false);
		CommandExecutor executor = mock(CommandExecutor.class);
		writeField(mojo, "executor", executor, true);

		Log logger = createLogger();
		writeField(mojo, "log", logger, true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture())).thenReturn(result);

		mojo.execute();
	}
}
