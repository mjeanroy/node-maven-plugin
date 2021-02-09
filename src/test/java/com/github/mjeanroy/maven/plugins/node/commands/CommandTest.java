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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest {

	@Test
	public void it_should_create_a_command_with_an_executable() {
		Command command = new Command("foo");
		assertThat(command.getExecutable()).isEqualTo("foo");
		assertThat(command.getArguments()).isNotNull().isEmpty();
	}

	@Test
	public void it_should_create_a_command_and_add_arguments() {
		Command command = new Command("foo");
		command.addArgument("arg1");
		command.addArgument("arg2");
		command.addArgument("arg2");
		command.addArgument("arg3");

		assertThat(command.getArguments())
			.isNotNull()
			.isNotEmpty()
			.hasSize(3)
			.extractingResultOf("toString")
			.containsExactly("arg1", "arg2", "arg3");
	}

	@Test
	public void it_should_different_arguments_instances() {
		Command command = new Command("foo");
		command.addArgument("arg1");
		command.addArgument("arg2");
		command.addArgument("arg2");
		command.addArgument("arg2");
		assertThat(command.getArguments()).isNotSameAs(command.getArguments());
	}

	@Test
	public void it_should_display_command() {
		Command command = new Command("npm");
		command.addArgument("--no-color");
		command.addArgument("clean");
		assertThat(command.toString())
			.isNotNull()
			.isNotEmpty()
			.isEqualTo("npm --no-color clean");
	}
}
