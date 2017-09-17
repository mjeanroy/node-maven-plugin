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
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectUtils.writePrivate;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public abstract class AbstractNpmScriptMojoTest<T extends AbstractNpmScriptMojo> extends AbstractNpmMojoTest {

	@Rule
	public ExpectedException thrown = none();

	@Test
	public void test_should_create_mojo() throws Exception {
		T mojo = createMojo("mojo", false);
		assertThat(mojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		assertThat(mojo).isNotNull();
		assertThat((Boolean) readPrivate(mojo, "color")).isTrue();
		assertThat((File) readPrivate(mojo, "workingDirectory")).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);

		CommandResult result = createResult(true);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Running: npm " + join(defaultArguments(true)));
		verify(logger, never()).error(anyString());

		verify(executor).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("npm " + join(defaultArguments(true)));
	}

	@Test
	public void it_should_execute_mojo_in_success_without_colors() throws Exception {
		T mojo = createMojo("mojo", false);

		CommandResult result = createResult(true);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Running: npm " + join(defaultArguments(false)));
		verify(logger, never()).error(anyString());

		verify(executor).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("npm " + join(defaultArguments(false)));
	}

	@Test
	public void it_should_execute_mojo_in_success_with_custom_script() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		writePrivate(mojo, "script", "foobar");

		CommandResult result = createResult(true);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Running: npm run-script foobar --maven");
		verify(logger, never()).error(anyString());

		verify(executor).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("npm run-script foobar --maven");
	}

	@Test
	public void it_should_skip_mojo_execution() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		writePrivate(mojo, "skip", true);

		CommandExecutor executor = readPrivate(mojo, "executor");
		Log logger = readPrivate(mojo, "log");

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));
		verify(logger).info(skipMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_skip_mojo_execution_if_it_has_been_executed() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);

		Map pluginContext = new HashMap();
		pluginContext.put(script(), true);
		mojo.setPluginContext(pluginContext);

		CommandExecutor executor = readPrivate(mojo, "executor");
		Log logger = readPrivate(mojo, "log");

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		String cmd = "npm" + (isStandardNpm() ? " " : " run-script ") + script();
		verify(logger).info(String.format("Command %s already done, skipping.", cmd));
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		T mojo = createMojo("mojo-with-parameters", true);
		writePrivate(mojo, "failOnError", false);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Log logger = readPrivate(mojo, "log");
		verify(logger).error("Error during execution of: npm " + join(defaultArguments(true)));
		verify(logger).error("Exit status: 1");

		verify(executor).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo("npm " + join(defaultArguments(true)));
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() throws Exception {
		thrown.expect(MojoExecutionException.class);

		T mojo = createMojo("mojo-with-parameters", true);
		writePrivate(mojo, "failOnError", true);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();
	}

	@Test
	public void it_should_throw_exception_if_scripts_does_not_exist() throws Exception {
		if (!isStandardNpm()) {
			thrown.expect(MojoExecutionException.class);
			thrown.expectMessage("Cannot execute npm run-script " + script() + " command: it is not defined in package.json");
		}

		T mojo = createMojo("mojo-without-scripts", false);
		writePrivate(mojo, "failOnMissingScript", true);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();
	}

	@Test
	public void it_should_not_throw_exception_if_scripts_does_not_exist() throws Exception {
		T mojo = createMojo("mojo-without-scripts", false);
		writePrivate(mojo, "failOnMissingScript", false);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		VerificationMode verificationModeLog = isStandardNpm() ? never() : times(1);
		VerificationMode verificationModeExecutor = isStandardNpm() ? times(1) : never();

		Log logger = readPrivate(mojo, "log");
		verify(logger, verificationModeLog).warn("Cannot execute npm run-script " + script() + " command: it is not defined in package.json, skipping.");
		verify(logger, never()).error("Cannot execute npm run-script " + script() + " command: it is not defined in package.json.");
		verify(executor, verificationModeExecutor).execute(any(File.class), any(Command.class), any(NpmLogger.class));
	}

	@Test
	public void it_should_throw_exception_if_package_json_does_not_exist() throws Exception {
		File workingDirectory = new File(".");

		thrown.expect(PackageJsonNotFoundException.class);
		thrown.expectMessage("File " + new File(workingDirectory.getAbsolutePath(), "package.json") + " does not exist");

		T mojo = createMojo("mojo-with-parameters", true);
		writePrivate(mojo, "failOnError", false);
		writePrivate(mojo, "workingDirectory", workingDirectory);

		mojo.execute();
	}

	@Test
	public void it_should_add_proxy_configuration() throws Exception {
		T mojo = createMojo("mojo-with-parameters", false);
		writePrivate(mojo, "ignoreProxies", false);
		writePrivate(mojo, "color", true);

		Proxy httpProxy = createProxy("http", "localhost", 8080, "mjeanroy", "foo");
		Proxy httpsProxy = createProxy("https", "localhost", 8080, "mjeanroy", "foo");

		Settings settings = mock(Settings.class);
		when(settings.getProxies()).thenReturn(asList(httpProxy, httpsProxy));
		writePrivate(mojo, "settings", settings);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
			.contains("--proxy http://mjeanroy:********@localhost:8080")
			.contains("--https-proxy http://mjeanroy:********@localhost:8080");

		Log logger = readPrivate(mojo, "log");

		verify(logger).info(
			"Running: npm " + join(defaultArguments(true)) +
				" --proxy http://mjeanroy:********@localhost:8080" +
				" --https-proxy http://mjeanroy:********@localhost:8080"
		);

		verify(logger).error(
			"Error during execution of: npm " + join(defaultArguments(true)) +
			" --proxy http://mjeanroy:********@localhost:8080" +
			" --https-proxy http://mjeanroy:********@localhost:8080"
		);
	}

	@Test
	public void it_should_ignore_proxy_configuration() throws Exception {
		T mojo = createMojo("mojo", false);
		writePrivate(mojo, "ignoreProxies", true);

		Proxy httpProxy = createProxy("http", "localhost", 8080, "mjeanroy", "foo");
		Proxy httpsProxy = createProxy("https", "localhost", 8080, "mjeanroy", "foo");

		Settings settings = mock(Settings.class);
		when(settings.getProxies()).thenReturn(asList(httpProxy, httpsProxy));
		writePrivate(mojo, "settings", settings);

		CommandResult result = createResult(false);
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		when(executor.execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class))).thenReturn(result);

		mojo.execute();

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
			.doesNotContain("--proxy http://mjeanroy:foo@localhost:8080")
			.doesNotContain("--https-proxy http://mjeanroy:foo@localhost:8080");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T createMojo(String projectName, boolean hasConfiguration) throws Exception {
		T mojo = super.createMojo(projectName, hasConfiguration);

		CommandExecutor executor = mock(CommandExecutor.class);
		writePrivate(mojo, "executor", executor);
		writePrivate(mojo, "failOnMissingScript", false);
		writePrivate(mojo, "ignoreProxies", true);

		return mojo;
	}

	private boolean isStandardNpm() {
		String script = script();
		return script.equals("test") || script.equals("install");
	}

	private List<String> defaultArguments(boolean withColors) {
		List<String> arguments = new ArrayList<>();

		String mojoName = script();
		if (!isStandardNpm()) {
			arguments.add("run-script");
		}

		arguments.add(mojoName);

		if (!withColors) {
			arguments.add("--no-color");
		}

		// Do not forget maven flag
		arguments.add("--maven");

		return arguments;
	}

	private String join(List<String> arguments) {
		StringBuilder sb = new StringBuilder();
		for (String arg : arguments) {
			sb.append(arg).append(" ");
		}
		return sb.toString().trim();
	}

	private Proxy createProxy(String protocol, String host, int port, String username, String password) {
		Proxy proxy = mock(Proxy.class);
		when(proxy.getProtocol()).thenReturn(protocol);
		when(proxy.getHost()).thenReturn(host);
		when(proxy.getPort()).thenReturn(port);
		when(proxy.getUsername()).thenReturn(username);
		when(proxy.getPassword()).thenReturn(password);
		when(proxy.isActive()).thenReturn(true);
		return proxy;
	}

	protected String skipMessage() {
		return String.format("Npm %s is skipped.", script());
	}
}
