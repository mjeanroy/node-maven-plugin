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
