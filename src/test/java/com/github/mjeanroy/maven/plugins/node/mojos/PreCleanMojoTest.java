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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.loggers.NpmLogger;
import com.github.mjeanroy.maven.plugins.node.model.IncrementalBuildConfiguration;
import com.github.mjeanroy.maven.plugins.node.tests.builders.IncrementalBuildConigurationTestBuilder;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.File;

import static com.github.mjeanroy.maven.plugins.node.tests.FileTestUtils.join;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PreCleanMojoTest extends AbstractNpmScriptIncrementalMojoTest<PreCleanMojo> {

	@Override
	String mojoName() {
		return "pre-clean";
	}

	@Override
	String script() {
		return "install";
	}

	@Override
	String scriptParameterName() {
		return "preCleanScript";
	}

	@Override
	void enableSkip(PreCleanMojo mojo) {
		writePrivate(mojo, "skipPreClean", true);
	}

	@Test
	public void it_should_execute_mojo_using_yarn_to_install_dependencies() throws Exception {
		PreCleanMojo mojo = lookupMojo("mojo-with-npm-client");

		CommandResult result = successResult();
		CommandExecutor executor = (CommandExecutor) readField(mojo, "executor", true);
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class), ArgumentMatchers.<String, String>anyMap())).thenReturn(result);

		mojo.execute();

		Log logger = (Log) readField(mojo, "log", true);
		verify(logger).info("Running: yarn install --maven");
		verify(logger, never()).error(anyString());

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(executor).execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class), ArgumentMatchers.<String, String>anyMap());

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("yarn install --maven");
	}

	@Test
	public void it_should_write_input_state_after_build() throws Exception {
		IncrementalBuildConfiguration incrementalBuild = IncrementalBuildConigurationTestBuilder.of(true);
		PreCleanMojo mojo = lookupMojo("mojo", singletonMap("incrementalBuild", incrementalBuild));
		File workingDirectory = readPrivate(mojo, "workingDirectory");

		mojo.execute();

		verifyStateFile(mojo, singleton(
				join(workingDirectory, "package.json")
		));
	}

	@Test
	public void it_should_write_input_state_with_package_lock_after_build() throws Exception {
		PreCleanMojo mojo = lookupMojo("mojo-with-package-lock");
		File workingDirectory = readPrivate(mojo, "workingDirectory");

		mojo.execute();

		verifyStateFile(mojo, asList(
				join(workingDirectory, "package-lock.json"),
				join(workingDirectory, "package.json")
		));
	}

	@Test
	public void it_should_write_input_state_with_yarn_lock_after_build() throws Exception {
		PreCleanMojo mojo = lookupMojo("mojo-with-yarn-lock");
		File workingDirectory = readPrivate(mojo, "workingDirectory");

		mojo.execute();

		verifyStateFile(mojo, asList(
				join(workingDirectory, "package.json"),
				join(workingDirectory, "yarn.lock")
		));
	}

	@Test
	public void it_should_re_run_mojo_after_incremental_build() throws Exception {
		IncrementalBuildConfiguration incrementalBuild = IncrementalBuildConigurationTestBuilder.of(true);
		PreCleanMojo mojo = lookupMojo("mojo", singletonMap(
				"incrementalBuild", incrementalBuild
		));

		mojo.execute();
		resetMojo(mojo);
		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class)).info("Command npm install already done, no changes detected, skipping.");
		verifyZeroInteractions(readPrivate(mojo, "executor"));
	}
}
