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

import com.github.mjeanroy.maven.plugins.node.commands.*;
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

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckNodeMojoTest extends AbstractNpmMojoTest<CheckNodeMojo> {

	@Override
	String mojoName() {
		return "check";
	}

	@Test
	public void it_should_execute_mojo() throws Exception {
		CommandExecutor executor = givenExecutor();

		CheckNodeMojo mojo = lookupEmptyMojo("mojo");
		writePrivate(mojo, "executor", executor);

		mojo.execute();

		verifyMojoExecution(executor, 2);

		Log logger = readPrivate(mojo, "log");
		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("Checking node command");
		inOrder.verify(logger).debug("Running: node --version");
		inOrder.verify(logger).info("Checking npm command");
		inOrder.verify(logger).debug("Running: npm --version");
	}

	@Test
	public void it_should_execute_mojo_with_specified_npm_client() throws Exception {
		CommandExecutor executor = givenExecutor();

		CheckNodeMojo mojo = lookupEmptyMojo("mojo");
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "npmClient", "yarn");

		mojo.execute();

		verifyMojoExecution(executor, 3);

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
		CommandExecutor executor = givenExecutor();

		CheckNodeMojo mojo = lookupEmptyMojo("mojo");
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "yarn", true);

		mojo.execute();

		verifyMojoExecution(executor, 3);

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
	public void it_should_fail_if_node_is_not_available() throws Exception {
		CommandExecutor executor = givenExecutor("node");

		CheckNodeMojo mojo = lookupEmptyMojo("mojo");
		writePrivate(mojo, "executor", executor);

		verifyMojoExecutionException(mojo, "Node is not available. Please install it on your operating system.");
	}

	@Test
	public void it_should_fail_if_npm_is_not_available() throws Exception {
		CheckNodeMojo mojo = lookupEmptyMojo("mojo");

		CommandExecutor executor = givenExecutor("npm");
		writePrivate(mojo, "executor", executor);

		verifyMojoExecutionException(mojo, "Npm is not available. Please install it on your operating system.");
	}

	@Test
	public void it_should_fail_if_yarn_is_not_available() throws Exception {
		CommandExecutor executor = givenExecutor("yarn");

		CheckNodeMojo mojo = lookupEmptyMojo("mojo");
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "yarn", true);

		verifyMojoExecutionException(mojo, "Yarn is not available. Please install it on your operating system.");
	}

	private void verifyMojoExecution(CommandExecutor executor, int numberOfCommand) {
		verify(executor, times(numberOfCommand)).execute(any(File.class), any(Command.class), any(NpmLogger.class), ArgumentMatchers.<String, String>anyMap());
	}

	private void verifyMojoExecutionException(final CheckNodeMojo mojo, final String message) {
		ThrowingCallable mojoExecute = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		assertThatThrownBy(mojoExecute).isInstanceOf(MojoExecutionException.class).hasMessage(message);
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

	private static class CommandExecutionSuccessAnswer implements Answer<CommandResult> {
		@Override
		public CommandResult answer(InvocationOnMock invocation) {
			return successResult();
		}
	}

	private static CommandExecutor givenExecutor() {
		CommandExecutor executor = mock(CommandExecutor.class);
		when(executor.execute(any(File.class), any(Command.class), any(OutputHandler.class), ArgumentMatchers.<String, String>anyMap())).thenAnswer(
				new CommandExecutionSuccessAnswer()
		);

		return executor;
	}

	private static CommandExecutor givenExecutor(String cmd) {
		CommandExecutor executor = mock(CommandExecutor.class);
		when(executor.execute(any(File.class), any(Command.class), any(OutputHandler.class), ArgumentMatchers.<String, String>anyMap())).thenAnswer(
				new CommandExecutionExceptionAnswer(cmd)
		);

		return executor;
	}
}
