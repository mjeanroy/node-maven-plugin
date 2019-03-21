/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CheckNodeMojoTest extends AbstractNpmMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Override
	protected String mojoName() {
		return "check";
	}

	@Test
	public void it_should_execute_mojo() throws Exception {
		CommandExecutor executor = mock(CommandExecutor.class);
		CheckNodeMojo mojo = createMojo("mojo", false);
		writePrivate(mojo, "executor", executor);

		CommandResult result = createResult(true);
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		verify(executor, times(2)).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		Log logger = readPrivate(mojo, "log");
		InOrder inOrder = inOrder(logger);
		inOrder.verify(logger).info("Checking node command");
		inOrder.verify(logger).debug("Running: node --version");
		inOrder.verify(logger).info("Checking npm command");
		inOrder.verify(logger).debug("Running: npm --version");
	}

	@Test
	public void it_should_execute_mojo_with_yarn() throws Exception {
		CommandExecutor executor = mock(CommandExecutor.class);
		CheckNodeMojo mojo = createMojo("mojo", false);
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "yarn", true);

		CommandResult result = createResult(true);
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		verify(executor, times(3)).execute(any(File.class), any(Command.class), any(NpmLogger.class));

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
		thrown.expect(MojoExecutionException.class);
		thrown.expectMessage("Node is not available. Please install it on your operating system.");

		CommandExecutor executor = mock(CommandExecutor.class);
		CheckNodeMojo mojo = createMojo("mojo", false);
		writePrivate(mojo, "executor", executor);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				Command command = (Command) invocation.getArguments()[1];
				if (command.toString().contains("node")) {
					throw new CommandException(mock(IOException.class));
				}

				return createResult(true);
			}
		});

		mojo.execute();
	}

	@Test
	public void it_should_fail_if_npm_is_not_available() throws Exception {
		thrown.expect(MojoExecutionException.class);
		thrown.expectMessage("Npm is not available. Please install it on your operating system.");

		CommandExecutor executor = mock(CommandExecutor.class);
		CheckNodeMojo mojo = createMojo("mojo", false);
		writePrivate(mojo, "executor", executor);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				Command command = (Command) invocation.getArguments()[1];
				if (command.toString().contains("npm")) {
					throw new CommandException(mock(IOException.class));
				}

				return createResult(true);
			}
		});

		mojo.execute();
	}

	@Test
	public void it_should_fail_if_yarn_is_not_available() throws Exception {
		thrown.expect(MojoExecutionException.class);
		thrown.expectMessage("Yarn is not available. Please install it on your operating system.");

		CommandExecutor executor = mock(CommandExecutor.class);
		CheckNodeMojo mojo = createMojo("mojo", false);
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "yarn", true);

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				Command command = (Command) invocation.getArguments()[1];
				if (command.toString().contains("yarn")) {
					throw new CommandException(mock(IOException.class));
				}

				return createResult(true);
			}
		});

		mojo.execute();
	}
}
