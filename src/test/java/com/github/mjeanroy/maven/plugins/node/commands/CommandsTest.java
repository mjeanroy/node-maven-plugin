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

package com.github.mjeanroy.maven.plugins.node.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.mjeanroy.maven.plugins.node.commands.Commands.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandsTest {

	private String osName;

	@Before
	public void setUp() {
		osName = System.getProperty("os.name");
	}

	@After
	public void tearDown() {
		System.setProperty("os.name", osName);
	}

	@Test
	public void it_should_create_npm_command_on_linux() {
		useLinux();
		verify_npm_command_on_unix();
	}

	@Test
	public void it_should_create_npm_command_with_custom_path_on_linux() {
		useLinux();
		verify_npm_command_with_custom_path_on_unix();
	}

	@Test
	public void it_should_create_yarn_command_on_linux() {
		useLinux();
		verify_yarn_command_on_unix();
	}

	@Test
	public void it_should_create_yarn_command_with_custom_path_on_linux() {
		useLinux();
		verify_yarn_command_with_custom_path_on_unix();
	}

	@Test
	public void it_should_create_npm_client_command_on_linux() {
		useLinux();
		verify_npm_client_command_on_unix();
	}

	@Test
	public void it_should_create_custom_npm_client_command_on_linux() {
		useLinux();
		verify_custom_npm_client_command_on_unix();
	}

	@Test
	public void it_should_create_node_command_on_linux() {
		useLinux();
		verify_node_command_on_unix();
	}

	@Test
	public void it_should_create_node_command_with_custom_path_on_linux() {
		useLinux();
		verify_node_command_with_custom_path_on_unix();
	}

	@Test
	public void it_should_create_npm_command_on_windows() {
		useWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly("/C", "npm", arg);
	}

	@Test
	public void it_should_create_npm_command_on_windows_with_custom_path() {
		useWindows();

		final String executable = "./npm-cli";
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_yarn_command_on_windows() {
		useWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", "yarn", arg);
	}

	@Test
	public void it_should_create_yarn_command_on_windows_with_custom_path() {
		useWindows();

		final String executable = "./yarn-cli";
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_npm_client_command_on_windows() {
		useWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command yarn = npmClient(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", "npm", arg);
	}

	@Test
	public void it_should_create_custom_npm_client_command_on_windows_with_custom_path() {
		useWindows();

		final String executable = "yarn";
		final String arg = "--no-color";

		Command yarn = npmClient(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("cmd");
		assertThat(yarn.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_node_command_on_windows() {
		useWindows();

		final String executable = null;
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly("/C", "node", arg);
	}

	@Test
	public void it_should_create_node_command_on_windows_with_custom_path() {
		useWindows();

		String executable = "./node.exe";
		String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly("/C", executable, arg);
	}

	@Test
	public void it_should_create_npm_command_on_mac_os_x() {
		useMacOsX();
		verify_npm_command_on_unix();
	}

	@Test
	public void it_should_create_npm_command_with_custom_path_on_mac_os_x() {
		useMacOsX();
		verify_npm_command_with_custom_path_on_unix();
	}

	@Test
	public void it_should_create_yarn_command_on_mac_os_x() {
		useMacOsX();
		verify_yarn_command_on_unix();
	}

	@Test
	public void it_should_create_yarn_command_with_custom_path_on_mac_os_x() {
		useMacOsX();
		verify_yarn_command_with_custom_path_on_unix();
	}

	@Test
	public void it_should_create_node_command_on_mac_os_x() {
		useMacOsX();
		verify_node_command_on_unix();
	}

	@Test
	public void it_should_create_node_command_with_custom_path_on_mac_os_x() {
		useMacOsX();
		verify_yarn_command_with_custom_path_on_unix();
	}

	private void verify_npm_command_on_unix() {
		final String executable = null;
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo("npm");
		assertThat(npm.getArguments()).containsExactly(arg);
	}

	private void verify_npm_command_with_custom_path_on_unix() {
		final String executable = "./npm-cli";
		final String arg = "--no-color";

		Command npm = npm(executable);
		npm.addArgument(arg);

		assertThat(npm.getExecutable()).isEqualTo(executable);
		assertThat(npm.getArguments()).containsExactly(arg);
	}

	private void verify_yarn_command_on_unix() {
		final String executable = null;
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("yarn");
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	private void verify_npm_client_command_on_unix() {
		final String executable = null;
		final String arg = "--no-color";

		Command yarn = npmClient(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("npm");
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	private void verify_custom_npm_client_command_on_unix() {
		final String executable = "yarn";
		final String arg = "--no-color";

		Command yarn = npmClient(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo("yarn");
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	private void verify_yarn_command_with_custom_path_on_unix() {
		final String executable =  "./yarn-cli";
		final String arg = "--no-color";

		Command yarn = yarn(executable);
		yarn.addArgument(arg);

		assertThat(yarn.getExecutable()).isEqualTo(executable);
		assertThat(yarn.getArguments()).containsExactly(arg);
	}

	private void verify_node_command_on_unix() {
		final String executable = null;
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo("node");
		assertThat(node.getArguments()).containsExactly(arg);
	}

	private void verify_node_command_with_custom_path_on_unix() {
		final String executable = "./node";
		final String arg = "--no-color";

		Command node = node(executable);
		node.addArgument(arg);

		assertThat(node.getExecutable()).isEqualTo(executable);
		assertThat(node.getArguments()).containsExactly(arg);
	}

	private static void useWindows() {
		useOs("Windows");
	}

	private static void useLinux() {
		useOs("Linux");
	}

	private static void useMacOsX() {
		useOs("Mac OS X");
	}

	private static void useOs(String osName) {
		System.setProperty("os.name", osName);
	}
}
