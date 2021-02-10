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
import com.github.mjeanroy.maven.plugins.node.commands.CommandException;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.commands.OutputHandler;
import com.github.mjeanroy.maven.plugins.node.model.EngineConfig;
import com.github.mjeanroy.maven.plugins.node.tests.builders.EngineConfigTestBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.CollectionTestUtils.newMap;
import static com.github.mjeanroy.maven.plugins.node.tests.CollectionTestUtils.newMapEntry;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CheckNodeMojoTest extends AbstractNpmMojoTest<CheckNodeMojo> {

	@Override
	String mojoName() {
		return "check";
	}

	@Test
	public void it_should_execute_mojo() throws Exception {
		CheckNodeMojo mojo = givenMojo(Collections.<String, Object>emptyMap());

		mojo.execute();

		verifyMojoExecution(mojo, 2);

		Log logger = readPrivate(mojo, "log");
		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("Checking node command");
		inOrder.verify(logger).debug("Running: node --version");
		inOrder.verify(logger).info("Checking npm command");
		inOrder.verify(logger).debug("Running: npm --version");
	}

	@Test
	public void it_should_skip_mojo() throws Exception {
		CheckNodeMojo mojo = givenMojo(newMap(singletonList(
				newMapEntry("skip", true)
		)));

		mojo.execute();

		verifyZeroInteractions(readPrivate(mojo, "executor"));

		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Goal 'check' is skipped.");
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void it_should_execute_mojo_with_specified_npm_client() throws Exception {
		CheckNodeMojo mojo = givenMojo(newMap(singletonList(
				newMapEntry("npmClient", (Object) "yarn")
		)));

		mojo.execute();

		verifyMojoExecution(mojo, 3);

		Log logger = readPrivate(mojo, "log");
		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("Checking node command");
		inOrder.verify(logger).debug("Running: node --version");
		inOrder.verify(logger).info("Checking npm command");
		inOrder.verify(logger).debug("Running: npm --version");
		inOrder.verify(logger).info("Checking yarn command");
		inOrder.verify(logger).debug("Running: yarn --version");
	}

	@Test
	public void it_should_execute_mojo_with_yarn() throws Exception {
		CheckNodeMojo mojo = givenMojo(newMap(singletonList(
				newMapEntry("yarn", (Object) true)
		)));

		mojo.execute();

		verifyMojoExecution(mojo, 3);

		Log logger = readPrivate(mojo, "log");
		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("Checking node command");
		inOrder.verify(logger).debug("Running: node --version");
		inOrder.verify(logger).info("Checking npm command");
		inOrder.verify(logger).debug("Running: npm --version");
		inOrder.verify(logger).info("Checking yarn command");
		inOrder.verify(logger).debug("Running: yarn --version");
	}

	@Test
	public void it_should_fail_if_node_is_not_available() {
		CheckNodeMojo mojo = givenMojo(newMap(singletonList(
				newMapEntry("executor", givenExecutor("node"))
		)));

		verify_mojo_execution_exception(mojo, "Executable node is not available. Please install it on your operating system.");
	}

	@Test
	public void it_should_fail_if_npm_is_not_available() {
		CheckNodeMojo mojo = givenMojo(newMap(singletonList(
				newMapEntry("executor", givenExecutor("npm"))
		)));

		verify_mojo_execution_exception(mojo, "Executable npm is not available. Please install it on your operating system.");
	}

	@Test
	public void it_should_fail_if_yarn_is_not_available() {
		CheckNodeMojo mojo = givenMojo(newMap(asList(
				newMapEntry("executor", (Object) givenExecutor("yarn")),
				newMapEntry("yarn", (Object) true)
		)));

		verify_mojo_execution_exception(mojo, "Executable yarn is not available. Please install it on your operating system.");
	}

	@Test
	public void it_should_not_fail_if_all_requirement_are_ok() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v8.0.0"),
				newMapEntry("npm", "6.0.0"),
				newMapEntry("yarn", "1.20.1")
		)));

		EngineConfig engineConfig = new EngineConfigTestBuilder()
				.withStrict(false)
				.addRequirement("node", ">= 8")
				.addRequirement("npm", ">= 6")
				.addRequirement("yarn", "~1.20.0")
				.build();

		CheckNodeMojo mojo = givenMojo(newMap(asList(
				newMapEntry("executor", (Object) executor),
				newMapEntry("engines", (Object) engineConfig)
		)));

		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class), never()).warn(anyString());
	}

	@Test
	public void it_should_not_fail_with_given_semver_pattern() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.14.0"),
				newMapEntry("npm", "6.0.0"),
				newMapEntry("yarn", "1.20.1")
		)));

		EngineConfig engineConfig = new EngineConfigTestBuilder()
				.withStrict(false)
				.addRequirement("node", ">=12.14.0 <13.0.0")
				.build();

		CheckNodeMojo mojo = givenMojo(newMap(asList(
				newMapEntry("executor", (Object) executor),
				newMapEntry("engines", (Object) engineConfig)
		)));

		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class), never()).warn(anyString());
	}

	// == ENGINE STRICT = false
	// == Engine specified in pom.xml

	@Test
	public void it_should_warn_if_node_version_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v8.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_warn_because_engine_requirement_failure(executor, "Engine 'node' with version 'v8.0.0' does not satisfy required version: '>= 12'");
	}

	@Test
	public void it_should_warn_if_npm_version_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "4.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_warn_because_engine_requirement_failure(executor, "Engine 'npm' with version '4.0.0' does not satisfy required version: '>= 6'");
	}

	@Test
	public void it_should_warn_if_custom_npm_client_version_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.0.0")
		)));

		check_warn_because_engine_requirement_failure(executor, "Engine 'yarn' with version '1.0.0' does not satisfy required version: '~1.20.0'");
	}

	// == ENGINE STRICT = false
	// == Engine specified in package.json

	@Test
	public void it_should_warn_if_node_version_specified_in_package_json_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v8.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_warn_because_engine_requirement_in_package_json_failure(executor, "Engine 'node' with version 'v8.0.0' does not satisfy required version: '>= 12'");
	}

	@Test
	public void it_should_warn_if_npm_version_specified_in_package_json_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "4.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_warn_because_engine_requirement_in_package_json_failure(executor, "Engine 'npm' with version '4.0.0' does not satisfy required version: '>= 6'");
	}

	@Test
	public void it_should_warn_if_custom_npm_client_version_specified_in_package_json_does_not_satisfy_requirement() throws Exception {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.0.0")
		)));

		check_warn_because_engine_requirement_in_package_json_failure(executor, "Engine 'yarn' with version '1.0.0' does not satisfy required version: '~1.20.0'");
	}

	// == ENGINE STRICT = true
	// == Engine specified in pom.xml

	@Test
	public void it_should_fail_if_node_version_does_not_satisfy_requirement() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v8.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_fail_because_engine_requirement_failure(executor, "Engine 'node' with version 'v8.0.0' does not satisfy required version: '>= 12'");
	}

	@Test
	public void it_should_fail_if_npm_version_does_not_satisfy_requirement() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "4.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_fail_because_engine_requirement_failure(executor, "Engine 'npm' with version '4.0.0' does not satisfy required version: '>= 6'");
	}

	@Test
	public void it_should_fail_if_custom_npm_client_version_does_not_satisfy_requirement() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.0.0")
		)));

		check_fail_because_engine_requirement_failure(executor, "Engine 'yarn' with version '1.0.0' does not satisfy required version: '~1.20.0'");
	}

	// == ENGINE STRICT = true
	// == Engine specified in package.json

	@Test
	public void it_should_fail_if_node_version_does_not_satisfy_requirement_specified_in_package_json() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v8.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_fail_because_engine_requirement_in_package_json_failure(executor, "Engine 'node' with version 'v8.0.0' does not satisfy required version: '>= 12'");
	}

	@Test
	public void it_should_fail_if_npm_version_does_not_satisfy_requirement_specified_in_package_json() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "4.0.0"),
				newMapEntry("yarn", "1.20.0")
		)));

		check_fail_because_engine_requirement_in_package_json_failure(executor, "Engine 'npm' with version '4.0.0' does not satisfy required version: '>= 6'");
	}

	@Test
	public void it_should_fail_if_custom_npm_client_version_does_not_satisfy_requirement_specified_in_package_json() {
		CommandExecutor executor = givenExecutor(newMap(asList(
				newMapEntry("node", "v12.0.0"),
				newMapEntry("npm", "8.0.0"),
				newMapEntry("yarn", "1.0.0")
		)));

		check_fail_because_engine_requirement_in_package_json_failure(executor, "Engine 'yarn' with version '1.0.0' does not satisfy required version: '~1.20.0'");
	}

	private void check_warn_because_engine_requirement_failure(CommandExecutor executor, String expectedWarn) throws Exception {
		EngineConfig engineConfig = new EngineConfigTestBuilder()
				.withStrict(false)
				.addRequirement("node", ">= 12")
				.addRequirement("npm", ">= 6")
				.addRequirement("yarn", "~1.20.0")
				.build();

		CheckNodeMojo mojo = givenMojo(newMap(asList(
				newMapEntry("npmClient", (Object) "yarn"),
				newMapEntry("executor", (Object) executor),
				newMapEntry("engines", (Object) engineConfig)
		)));

		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class)).warn(
				expectedWarn
		);
	}

	private void check_fail_because_engine_requirement_failure(CommandExecutor executor, String expectedMessage) {
		EngineConfig engineConfig = new EngineConfigTestBuilder()
				.withStrict(true)
				.addRequirement("node", ">= 12")
				.addRequirement("npm", ">= 6")
				.addRequirement("yarn", "~1.20.0")
				.build();

		final CheckNodeMojo mojo = givenMojo(newMap(asList(
				newMapEntry("npmClient", (Object) "yarn"),
				newMapEntry("executor", (Object) executor),
				newMapEntry("engines", (Object) engineConfig)
		)));

		ThrowingCallable func = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		assertThatThrownBy(func).isInstanceOf(MojoExecutionException.class).hasMessage(expectedMessage);
	}

	private void check_warn_because_engine_requirement_in_package_json_failure(CommandExecutor executor, String expectedWarn) throws Exception {
		CheckNodeMojo mojo = givenMojo("mojo-with-engine", newMap(asList(
				newMapEntry("npmClient", (Object) "yarn"),
				newMapEntry("executor", (Object) executor)
		)));

		mojo.execute();

		verify(readPrivate(mojo, "log", Log.class)).warn(
				expectedWarn
		);
	}

	private void check_fail_because_engine_requirement_in_package_json_failure(CommandExecutor executor, String expectedMessage) {
		final CheckNodeMojo mojo = givenMojo("mojo-with-engine-strict", newMap(asList(
				newMapEntry("npmClient", (Object) "yarn"),
				newMapEntry("executor", (Object) executor)
		)));

		ThrowingCallable func = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		assertThatThrownBy(func).isInstanceOf(MojoExecutionException.class).hasMessage(expectedMessage);
	}

	private void verifyMojoExecution(CheckNodeMojo mojo, int numberOfCommand) {
		verify(readPrivate(mojo, "executor", CommandExecutor.class), times(numberOfCommand)).execute(
				any(File.class),
				any(Command.class),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);
	}

	private void verify_mojo_execution_exception(final CheckNodeMojo mojo, final String message) {
		ThrowingCallable mojoExecute = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		assertThatThrownBy(mojoExecute).isInstanceOf(MojoExecutionException.class).hasMessage(message);
	}

	private CheckNodeMojo givenMojo(String name, Map<String, ?> props) {
		CheckNodeMojo mojo = lookupEmptyMojo(name);
		for (Map.Entry<String, ?> prop : props.entrySet()) {
			writePrivate(mojo, prop.getKey(), prop.getValue());
		}

		return mojo;
	}

	private CheckNodeMojo givenMojo(Map<String, ?> props) {
		return givenMojo("mojo", props);
	}

	private static class CommandExecutionExceptionAnswer implements Answer<CommandResult> {
		private final String cmd;

		public CommandExecutionExceptionAnswer(String cmd) {
			this.cmd = cmd;
		}

		@Override
		public CommandResult answer(InvocationOnMock invocation) {
			Command command = (Command) invocation.getArguments()[1];
			if (command.toString().contains(cmd)) {
				throw new CommandException(new IOException());
			}

			return successResult();
		}
	}

	private static CommandExecutor givenExecutor(String cmd) {
		CommandExecutor executor = mock(CommandExecutor.class);
		when(executor.execute(any(File.class), any(Command.class), any(OutputHandler.class), ArgumentMatchers.<String, String>anyMap())).thenAnswer(
				new CommandExecutionExceptionAnswer(cmd)
		);

		return executor;
	}

	private static CommandExecutor givenExecutor(final Map<String, String> cmds) {
		CommandExecutor executor = mock(CommandExecutor.class);

		when(executor.execute(any(File.class), any(Command.class), any(OutputHandler.class), ArgumentMatchers.<String, String>anyMap())).thenAnswer(new Answer<CommandResult>() {
				@Override
				public CommandResult answer(InvocationOnMock invocationOnMock) {
					Command command = invocationOnMock.getArgument(1);
					String name = command.getName();
					return successResult(cmds.get(name));
				}
		});

		return executor;
	}
}
