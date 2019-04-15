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

package com.github.mjeanroy.maven.plugins.node.commands;

import com.github.mjeanroy.maven.plugins.node.commons.EnvUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import static com.github.mjeanroy.maven.plugins.node.commands.Commands.node;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.yarn;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writeStatic;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandsTest {

	private boolean isWindows;

	@Before
	public void setUp() {
		isWindows = EnvUtils.isWindows();
	}

	@After
	public void tearDown() throws Exception {
		setWindows(isWindows);
	}

	@Test
	public void it_should_create_npm_command() throws Exception {
		unsetWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("npm");
		assertThat(npm.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_npm_command_with_custom_path() throws Exception {
		unsetWindows();

		final String executable = "./npm-cli";
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo(executable);
		assertThat(npm.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_yarn_command() throws Exception {
		unsetWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("yarn");
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_yarn_command_with_custom_path() throws Exception {
		unsetWindows();

		final String executable =  "./yarn-cli";
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo(executable);
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_node_command() throws Exception {
		unsetWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("node");
		assertThat(node.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_node_command_with_custom_path() throws Exception {
		unsetWindows();

		final String executable = "./node";
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo(executable);
		assertThat(node.getArguments()).containsExactly(arg);
	}

	@Test
	public void it_should_create_npm_command_on_windows() throws Exception {
		setWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly("/C", "npm", arg);
	}

	@Test
	public void it_should_create_npm_command_on_windows_with_custom_path() throws Exception {
		setWindows();

		final String executable = "./npm-cli";
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_yarn_command_on_windows() throws Exception {
		setWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", "yarn", arg);
	}

	@Test
	public void it_should_create_yarn_command_on_windows_with_custom_path() throws Exception {
		setWindows();

		final String executable = "./yarn-cli";
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_node_command_on_windows() throws Exception {
		setWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly("/C", "node", arg);
	}

	@Test
	public void it_should_create_node_command_on_windows_with_custom_path() throws Exception {
		setWindows();

		String executable = "./node.exe";
		String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly("/C", executable, arg);
	}

	private static void setWindows() throws Exception {
		setWindows(true);
	}

	private static void unsetWindows() throws Exception {
		setWindows(false);
	}

	private static void setWindows(final boolean isWindows) throws Exception {
		AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
			@Override
			public Void run() {
				writeStatic(EnvUtils.class, "IS_WINDOWS", isWindows);
				return null;
			}
		});
	}
}
