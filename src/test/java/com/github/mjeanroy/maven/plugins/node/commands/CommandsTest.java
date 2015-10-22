package com.github.mjeanroy.maven.plugins.node.commands;

import org.junit.Test;

import static com.github.mjeanroy.maven.plugins.node.commands.Commands.node;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandsTest {

	@Test
	public void it_should_create_npm_command() {
		Command npm = npm();
		assertThat(npm.getExecutable()).isEqualTo("npm");
	}

	@Test
	public void it_should_create_node_command() {
		Command npm = node();
		assertThat(npm.getExecutable()).isEqualTo("node");
	}
}
