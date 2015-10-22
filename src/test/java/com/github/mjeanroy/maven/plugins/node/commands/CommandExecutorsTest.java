package com.github.mjeanroy.maven.plugins.node.commands;

import org.junit.Test;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandExecutorsTest {

	@Test
	public void it_should_create_new_executor() {
		CommandExecutor executor = newExecutor();
		assertThat(executor).isNotNull();
	}
}
