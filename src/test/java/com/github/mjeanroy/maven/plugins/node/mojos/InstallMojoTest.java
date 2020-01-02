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
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class InstallMojoTest extends AbstractNpmScriptMojoTest<InstallMojo> {

	@Override
	String mojoName() {
		return "install";
	}

	@Override
	void overrideScript(InstallMojo mojo, String script) {
		writePrivate(mojo, "installScript", script);
	}

	@Override
	void enableSkip(InstallMojo mojo) {
		writePrivate(mojo, "skipInstall", true);
	}

	@Test
	public void it_should_execute_mojo_using_yarn_to_install_dependencies() throws Exception {
		InstallMojo mojo = lookupMojo("mojo-with-yarn");

		CommandResult result = successResult();
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Log logger = (Log) readField(mojo, "log", true);
		verify(logger).info("Running: yarn install --maven");
		verify(logger, never()).error(anyString());

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(executor).execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("yarn install --maven");
	}
}
