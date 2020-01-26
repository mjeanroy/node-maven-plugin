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

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.commands.OutputHandler;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static java.util.Collections.emptyMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractMojoTest<T extends AbstractNpmMojo> {

	@Rule
	public TestResources resources = new TestResources();

	@Rule
	public MojoRule mojoRule = new MojoRule();

	T lookupMojo(String projectName) {
		return lookupAndConfigureMojo(projectName, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupMojo(goal, pom);
			}
		});
	}

	T lookupEmptyMojo(String projectName) {
		return lookupAndConfigureMojo(projectName, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupEmptyMojo(goal, pom);
			}
		});
	}

	T lookupMojo(String projectName, Map<String, ?> configuration) {
		return lookupAndConfigureMojo(projectName, configuration, new MojoFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T build(String goal, File pom) throws Exception {
				return (T) mojoRule.lookupEmptyMojo(goal, pom);
			}
		});
	}

	private T lookupAndConfigureMojo(String projectName, MojoFactory<T> factory) {
		Map<String, Object> configuration = emptyMap();
		return lookupAndConfigureMojo(projectName, configuration, factory);
	}

	private T lookupAndConfigureMojo(String projectName, Map<String, ?> configuration, MojoFactory<T> factory) {
		try {
			return doLookupAndConfigurationMojo(projectName, configuration, factory);
		}
		catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	private T doLookupAndConfigurationMojo(String projectName, Map<String, ?> configuration, MojoFactory<T> factory) throws Exception {
		File baseDir = resources.getBasedir(projectName);
		File pom = new File(baseDir, "pom.xml");
		Log logger = createLogger();

		T mojo = factory.build(mojoName(), pom);

		writePrivate(mojo, "workingDirectory", baseDir);
		writePrivate(mojo, "log", logger);
		writePrivate(mojo, "executor", givenSuccessfulExecutor());

		for (Map.Entry<String, ?> property : configuration.entrySet()) {
			writePrivate(mojo, property.getKey(), property.getValue());
		}

		return mojo;
	}

	private CommandExecutor givenSuccessfulExecutor() {
		CommandExecutor executor = mock(CommandExecutor.class);

		when(executor.execute(any(File.class), any(Command.class), any(OutputHandler.class), ArgumentMatchers.<String, String>anyMap())).thenAnswer(new Answer<CommandResult>() {
			@Override
			public CommandResult answer(InvocationOnMock invocationOnMock) {
				return successResult();
			}
		});

		return executor;
	}

	/**
	 * Get the mojo name to test.
	 *
	 * @return Mojo Name.
	 */
	abstract String mojoName();

	private Log createLogger() {
		return mock(Log.class);
	}

	private interface MojoFactory<T extends AbstractNpmMojo> {
		T build(String goal, File pom) throws Exception;
	}
}
