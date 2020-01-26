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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.absolutePath;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public abstract class AbstractNpmMojoTest<T extends AbstractNpmMojo> extends AbstractMojoTest<T> {

	@Test
	public void it_should_run_npm_client_defaulting_to_global_npm() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		mojo.execute();
		verify_command_executable(mojo, "npm");
	}

	@Test
	public void it_should_run_npm_client_defaulting_to_global_yarn() throws Exception {
		T mojo = lookupMojo("mojo-with-npm-client");
		mojo.execute();
		verify_command_executable(mojo, "yarn");
	}

	@Test
	public void it_should_run_npm_client_using_custom_npm_client_home() throws Exception {
		T mojo = lookupMojo("mojo-with-npm-client-home");
		mojo.execute();
		verify_command_executable(mojo, absolutePath("/usr/bin/yarn"));
	}

	private void verify_command_executable(T mojo, String executable) {
		File workingDirectory = readPrivate(mojo, "workingDirectory", File.class);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);

		verify(readPrivate(mojo, "executor", CommandExecutor.class), atLeastOnce()).execute(
				eq(workingDirectory),
				cmdCaptor.capture(),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.getName()).isEqualTo(executable);
	}
}
