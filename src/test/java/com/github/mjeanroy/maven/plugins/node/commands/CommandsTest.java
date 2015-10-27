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

package com.github.mjeanroy.maven.plugins.node.commands;

import com.github.mjeanroy.maven.plugins.node.commons.EnvUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.github.mjeanroy.maven.plugins.node.commands.Commands.node;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EnvUtils.class)
public class CommandsTest {

	@Before
	public void setUp() {
		mockStatic(EnvUtils.class);
	}

	@Test
	public void it_should_create_npm_command() {
		when(EnvUtils.isWindows()).thenReturn(false);
		Command npm = npm(null);
		npm.addArgument("--no-color");
		assertThat(npm.getExecutable()).isEqualTo("npm");
		assertThat(npm.getArguments()).containsExactly(
			"--no-color"
		);
	}

	@Test
	public void it_should_create_npm_command_with_custom_path() {
		when(EnvUtils.isWindows()).thenReturn(false);
		Command npm = npm("./npm-cli");
		npm.addArgument("--no-color");
		assertThat(npm.getExecutable()).isEqualTo("./npm-cli");
		assertThat(npm.getArguments()).containsExactly(
			"--no-color"
		);
	}

	@Test
	public void it_should_create_node_command() {
		when(EnvUtils.isWindows()).thenReturn(false);
		Command node = node(null);
		node.addArgument("--no-color");
		assertThat(node.getExecutable()).isEqualTo("node");
		assertThat(node.getArguments()).containsExactly(
			"--no-color"
		);
	}

	@Test
	public void it_should_create_node_command_with_custom_path() {
		when(EnvUtils.isWindows()).thenReturn(false);
		Command node = node("./node");
		node.addArgument("--no-color");
		assertThat(node.getExecutable()).isEqualTo("./node");
		assertThat(node.getArguments()).containsExactly(
			"--no-color"
		);
	}

	@Test
	public void it_should_create_npm_command_on_windows() {
		when(EnvUtils.isWindows()).thenReturn(true);
		Command npm = npm(null);
		npm.addArgument("--no-color");
		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly(
			"/C",
			"npm",
			"--no-color"
		);
	}

	@Test
	public void it_should_create_npm_command_on_windows_with_custom_path() {
		when(EnvUtils.isWindows()).thenReturn(true);
		Command npm = npm("./npm-cli");
		npm.addArgument("--no-color");
		assertThat(npm.getExecutable()).isEqualTo("cmd");
		assertThat(npm.getArguments()).containsExactly(
			"/C",
			"./npm-cli",
			"--no-color"
		);
	}

	@Test
	public void it_should_create_node_command_on_windows() {
		when(EnvUtils.isWindows()).thenReturn(true);
		Command node = node(null);
		node.addArgument("--no-color");
		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly(
			"/C",
			"node",
			"--no-color"
		);
	}

	@Test
	public void it_should_create_node_command_on_windows_with_custom_path() {
		when(EnvUtils.isWindows()).thenReturn(true);
		Command node = node("./node.exe");
		node.addArgument("--no-color");
		assertThat(node.getExecutable()).isEqualTo("cmd");
		assertThat(node.getArguments()).containsExactly(
			"/C",
			"./node.exe",
			"--no-color"
		);
	}
}
