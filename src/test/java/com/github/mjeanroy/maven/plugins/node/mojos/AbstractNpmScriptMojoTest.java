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

package com.github.mjeanroy.maven.plugins.node.mojos;

import com.github.mjeanroy.maven.plugins.node.commands.Command;
import com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor;
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.exceptions.PackageJsonNotFoundException;
import com.github.mjeanroy.maven.plugins.node.loggers.NpmLogger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Settings;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.verification.VerificationMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.maven.plugins.node.tests.CollectionTestUtils.newMap;
import static com.github.mjeanroy.maven.plugins.node.tests.CollectionTestUtils.newMapEntry;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.readPrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.ReflectTestUtils.writePrivate;
import static com.github.mjeanroy.maven.plugins.node.tests.StringTestUtils.join;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.failureResult;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.ProxyTestBuilder.defaultHttpProxy;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.ProxyTestBuilder.defaultHttpsProxy;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.SettingsTestBuilder.newSettings;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public abstract class AbstractNpmScriptMojoTest<T extends AbstractNpmScriptMojo> extends AbstractNpmMojoTest<T> {

	private static final String NPM = "npm";
	private static final String YARN = "yarn";

	@Test
	public void test_should_create_mojo() {
		T mojo = lookupEmptyMojo("mojo");
		assertThat(mojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() {
		T mojo = lookupMojo("mojo-with-parameters");

		assertThat(mojo).isNotNull();
		assertThat(readPrivate(mojo, "color", Boolean.class)).isTrue();
		assertThat(readPrivate(mojo, "workingDirectory", File.class)).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");
		execute_and_verify_successful_mojo_execution(mojo, NPM);
	}

	@Test
	public void it_should_execute_mojo_given_environment_variables() throws Exception {
		Map<String, String> environmentVariables = singletonMap("maven", "true");
		T mojo = lookupMojo("mojo-with-parameters", newMap(asList(
				newMapEntry("environmentVariables", (Object) environmentVariables),
				newMapEntry("color", (Object) true)
		)));

		execute_and_verify_successful_mojo_execution(mojo, NPM, environmentVariables);
	}

	@Test
	public void it_should_execute_mojo_with_yarn_in_success() throws Exception {
		T mojo = lookupMojo("mojo-with-yarn");
		execute_and_verify_successful_mojo_execution(mojo, YARN);
		verify(readPrivate(mojo, "log", Log.class)).warn("Parameter 'yarn' is deprecated, please use 'npmClient' instead.");
	}

	@Test
	public void it_should_execute_mojo_with_custom_npm_client_in_success() throws Exception {
		T mojo = lookupMojo("mojo-with-npm-client");
		execute_and_verify_successful_mojo_execution(mojo, YARN);
		verify(readPrivate(mojo, "log", Log.class), never()).warn(anyString());
	}

	@Test
	public void it_should_execute_mojo_in_success_without_colors() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		mojo.execute();
		verify_mojo_execution(mojo, NPM, join(defaultArguments(true)));
	}

	@Test
	public void it_should_execute_mojo_in_success_with_custom_script() throws Exception {
		String script = script() + ":foobar";
		T mojo = lookupMojo("mojo-with-parameters", singletonMap(
				scriptParameterName(), script
		));

		mojo.execute();

		verify_mojo_execution(mojo, NPM, "run " + script + " --maven");
	}

	@Test
	public void it_should_execute_mojo_in_success_with_script_with_options() throws Exception {
		String script = script();
		String cmd = script + " --force";
		boolean addRun = !isStandardScript();

		T mojo = lookupMojo("mojo-with-parameters", singletonMap(
				scriptParameterName(), cmd
		));

		mojo.execute();

		verify_mojo_execution(mojo, NPM, (addRun ? "run " : "") + cmd + " --maven");
	}

	@Test
	public void it_should_execute_mojo_in_success_with_custom_script_and_warn_if_deprecated_option_is_used() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters", singletonMap(
				"script", "foobar"
		));

		mojo.execute();

		verify_mojo_execution(mojo, NPM, "run foobar --maven");
		verify(readPrivate(mojo, "log", Log.class)).warn(
				"Parameter `script` has been deprecated to avoid conflict issues, please use `" + scriptParameterName() + "` instead"
		);
	}

	@Test
	public void it_should_skip_mojo_execution() throws Exception {
		T mojo = lookupMojo("mojo", singletonMap(
				"skip", true
		));

		mojo.execute();

		verifySkippedMojo(mojo);
	}

	@Test
	public void it_should_skip_individual_mojo_execution() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		// Enable individual skip.
		enableSkip(mojo);

		mojo.execute();

		verifySkippedMojo(mojo);
	}

	@Test
	public void it_should_skip_individual_mojo_execution_with_yarn() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		// Enable yarn
		writePrivate(mojo, "yarn", true);

		// Enable individual skip.
		enableSkip(mojo);

		mojo.execute();

		verifySkippedMojo(mojo, "yarn");
	}

	@Test
	public void it_should_skip_individual_mojo_execution_with_custom_npm_client() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		// Enable npmClient
		writePrivate(mojo, "npmClient", "yarn");

		// Enable individual skip.
		enableSkip(mojo);

		mojo.execute();

		verifySkippedMojo(mojo, "yarn");
	}

	@Test
	public void it_should_skip_mojo_execution_if_it_has_been_executed() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		mojo.execute();
		mojo.execute();

		String cmd = NPM + (isStandardScript() ? "" : " run") + " " + script();
		verify_executor_has_been_run_once(mojo);
		verify_command_skipped_has_been_logged_once(mojo, cmd);
	}

	@Test
	public void it_should_not_skip_mojo_execution_if_it_has_been_executed_on_another_working_directory() throws Exception {
		// Create two mojos in two different working directory
		T mojo1 = lookupMojo("mojo-fail-on-error-false");
		T mojo2 = lookupMojo("mojo-fail-on-error-true");

		// Share plugin context as in a "normal" build
		Map<?, ?> pluginContext = new HashMap<>();
		mojo1.setPluginContext(pluginContext);
		mojo2.setPluginContext(pluginContext);

		// Execute both mojo
		mojo1.execute();
		mojo2.execute();

		verify_executor_has_been_run_once(mojo1);
		verify_executor_has_been_run_once(mojo2);

		String cmd = NPM + (isStandardScript() ? "" : " run") + " " + script();
		verify_command_skipped_has_not_been_logged(mojo1, cmd);
		verify_command_skipped_has_not_been_logged(mojo2, cmd);
	}

	@Test
	public void it_should_not_skip_mojo_execution_if_it_has_been_executed_on_another_working_directory_using_normalized_path() throws Exception {
		T mojo1 = lookupEmptyMojo("mojo");
		T mojo2 = lookupEmptyMojo("mojo");

		// Override working directories with relative path
		writePrivate(mojo1, "workingDirectory", new File(readPrivate(mojo1, "workingDirectory", File.class), "foo/.."));
		writePrivate(mojo2, "workingDirectory", new File(readPrivate(mojo1, "workingDirectory", File.class), "."));

		// Share plugin context as in a "normal" build
		Map<?, ?> pluginContext = new HashMap<>();
		mojo1.setPluginContext(pluginContext);
		mojo2.setPluginContext(pluginContext);

		// Execute both mojo
		mojo1.execute();
		mojo2.execute();

		verify_executor_has_been_run_once(mojo1);

		String cmd = NPM + (isStandardScript() ? "" : " run") + " " + script();
		verify_command_skipped_has_not_been_logged(mojo1, cmd);
		verify_command_skipped_has_been_logged_once(mojo2, cmd);
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		T mojo = lookupMojo("mojo-fail-on-error-false");

		givenFailExecutor(mojo);

		mojo.execute();

		verify_mojo_error_execution(mojo, NPM, join(defaultArguments(true)));
	}

	@Test
	public void it_should_execute_yarn_mojo_in_failure() throws Exception {
		T mojo = lookupMojo("mojo-yarn-fail-on-error-false");

		givenFailExecutor(mojo);

		mojo.execute();

		verify_mojo_error_execution(mojo, YARN, join(defaultArguments(true)));
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() {
		final T mojo = lookupMojo("mojo-fail-on-error-true");
		final ThrowingCallable mojoExecute = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		givenFailExecutor(mojo);

		assertThatThrownBy(mojoExecute).isInstanceOf(MojoExecutionException.class);
	}

	@Test
	public void it_should_throw_exception_if_scripts_does_not_exist() throws Throwable {
		final T mojo = lookupMojo("mojo-without-scripts-fail");
		final ThrowingCallable mojoExecute = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};

		if (isStandardScript()) {
			mojoExecute.call();
		} else {
			assertThatThrownBy(mojoExecute).isInstanceOf(MojoExecutionException.class).hasMessageStartingWith("Cannot execute npm run " + script() + " command: it is not defined in package.json");
		}
	}

	@Test
	public void it_should_not_throw_exception_if_scripts_does_not_exist() throws Exception {
		T mojo = lookupMojo("mojo-without-scripts-do-not-fail");

		mojo.execute();

		VerificationMode verificationModeLog = isStandardScript() ? never() : times(1);
		VerificationMode verificationModeExecutor = isStandardScript() ? times(1) : never();

		Log logger = readPrivate(mojo, "log");

		String expectedPackageJsonPath = new File(mojo.getWorkingDirectory(), "package.json").getAbsolutePath();
		verify(logger, verificationModeLog).warn("Cannot execute npm run " + script() + " command: it is not defined in package.json (please check file: " + expectedPackageJsonPath + "), skipping.");
		verify(logger, never()).error(anyString());

		verify(readPrivate(mojo, "executor", CommandExecutor.class), verificationModeExecutor).execute(
				any(File.class),
				any(Command.class),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);
	}

	@Test
	public void it_should_throw_exception_if_package_json_does_not_exist() {
		final T mojo = lookupMojo("mojo-without-package-json");
		final ThrowingCallable mojoExecute = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				mojo.execute();
			}
		};
		assertThatThrownBy(mojoExecute).isInstanceOf(PackageJsonNotFoundException.class).hasMessageEndingWith("package.json does not exist");
	}

	@Test
	public void it_should_add_proxy_configuration() throws Exception {
		T mojo = lookupMojo("mojo-with-proxy");
		givenSettingsWithDefaultProxies(mojo);
		givenFailExecutor(mojo);

		mojo.execute();

		verify_command_execution_with_proxy(mojo);
		verify_proxy_log_output(mojo);
	}

	@Test
	public void it_should_ignore_proxy_configuration() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		givenSettingsWithDefaultProxies(mojo);
		writePrivate(mojo, "ignoreProxies", true);

		mojo.execute();

		verify_command_execution_ignoring_proxies(mojo);
	}

	@Test
	public void it_should_not_add_maven_argument_if_disabled() throws Exception {
		T mojo = lookupMojo("mojo-without-maven-argument");
		mojo.execute();
		verify_mojo_execution(mojo, NPM, join(defaultArguments(false)));
	}

	@Override
	T lookupMojo(String projectName) {
		return configureMojo(
				super.lookupMojo(projectName)
		);
	}

	@Override
	T lookupEmptyMojo(String projectName) {
		return configureMojo(
				super.lookupEmptyMojo(projectName)
		);
	}

	@Override
	T lookupMojo(String projectName, Map<String, ?> configuration) {
		return configureMojo(
				super.lookupMojo(projectName, configuration)
		);
	}

	private T configureMojo(T mojo) {
		writePrivate(mojo, "executor", givenExecutor(successResult()));
		return mojo;
	}

	/**
	 * Get the script parameter name used by this mojo.
	 *
	 * @return The script parameter name.
	 */
	abstract String scriptParameterName();

	/**
	 * Override mojo script value.
	 *
	 * @param mojo The mojo.
	 */
	abstract void enableSkip(T mojo);

	/**
	 * The expected skipped message.
	 *
	 * @param npmClient The npm client name.
	 * @return Skipped message.
	 */
	String skipMessage(String npmClient) {
		String command = npmClient + " " + (isStandardScript() ? "" : "run ") + script();
		return "Command '" + command + "' is skipped.";
	}

	/**
	 * Get the NPM/YARN script that is tested.
	 *
	 * @return The script name.
	 */
	String script() {
		return mojoName();
	}

	private boolean isStandardScript() {
		String script = script();
		return script.equals("test") || script.equals("install") || script.equals("publish") || script.equals("start") || script.equals("prune");
	}

	private List<String> defaultArguments(boolean withMaven) {
		List<String> arguments = new ArrayList<>();

		String mojoName = script();

		if (!isStandardScript()) {
			arguments.add("run");
		}

		arguments.add(mojoName);

		// Do not forget maven flag
		if (withMaven) {
			arguments.add("--maven");
		}

		return arguments;
	}

	private void verifySkippedMojo(T mojo, String npmClient) {
		verifyZeroInteractions(readPrivate(mojo, "executor"));
		verify(readPrivate(mojo, "log", Log.class)).info(skipMessage(npmClient));
	}

	private void verifySkippedMojo(T mojo) {
		verifySkippedMojo(mojo, "npm");
	}


	private void execute_and_verify_successful_mojo_execution(T mojo, String pkg, Map<String, String> environmentVariables) throws Exception {
		mojo.execute();
		String expectedArgs = join(defaultArguments(true));
		verify_mojo_execution(
				mojo,
				pkg,
				expectedArgs,
				environmentVariables
		);
	}

	private void execute_and_verify_successful_mojo_execution(T mojo, String pkg) throws Exception {
		execute_and_verify_successful_mojo_execution(mojo, pkg, Collections.<String, String>emptyMap());
	}

	private void verify_mojo_execution(T mojo, String pkg, String expectedArgs) {
		verify_mojo_execution(
				mojo,
				pkg,
				expectedArgs,
				Collections.<String, String>emptyMap()
		);
	}

	private void verify_mojo_execution(T mojo, String pkg, String expectedArgs, Map<String, String> environmentVariables) {
		verify_success_output(mojo, pkg, expectedArgs);
		verify_command_execution(mojo, pkg, expectedArgs, environmentVariables);
	}

	private void verify_mojo_error_execution(T mojo, String pkg, String expectedArgs) {
		verify_error_output(mojo, pkg, expectedArgs);
		verify_command_execution(mojo, pkg, expectedArgs, Collections.<String, String>emptyMap());
	}

	private void verify_success_output(T mojo, String pkg, String expectedArgs) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Running: " + pkg + " " + expectedArgs);
		verify(logger, never()).error(anyString());
	}

	private void verify_error_output(T mojo, String pkg, String expectedArgs) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).error("Error during execution of: " + pkg + " " + expectedArgs);
		verify(logger).error("Exit status: 1");
	}

	private void verify_proxy_log_output(T mojo) {
		String expectedArgs = join(defaultArguments(true));

		Log logger = readPrivate(mojo, "log");

		verify(logger).info(
				"Running: npm " + expectedArgs +
						" --proxy http://mjeanroy:********@localhost:8080" +
						" --https-proxy http://mjeanroy:********@localhost:8080"
		);

		verify(logger).error(
				"Error during execution of: npm " + expectedArgs +
						" --proxy http://mjeanroy:********@localhost:8080" +
						" --https-proxy http://mjeanroy:********@localhost:8080"
		);
	}

	private void verify_command_execution_with_proxy(T mojo) {
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(readPrivate(mojo, "executor", CommandExecutor.class)).execute(
				any(File.class),
				cmdCaptor.capture(),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
				.contains("--proxy http://mjeanroy:********@localhost:8080")
				.contains("--https-proxy http://mjeanroy:********@localhost:8080");
	}

	private void verify_command_execution(T mojo, String pkg, String expectedArgs, Map<String, String> environment) {
		ArgumentCaptor<File> workingDirectoryCaptor = ArgumentCaptor.forClass(File.class);
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		ArgumentCaptor<NpmLogger> npmLoggerCaptor = ArgumentCaptor.forClass(NpmLogger.class);

		verify(readPrivate(mojo, "executor", CommandExecutor.class)).execute(
				workingDirectoryCaptor.capture(),
				cmdCaptor.capture(),
				npmLoggerCaptor.capture(),
				eq(environment)
		);

		File workingDirectory = workingDirectoryCaptor.getValue();
		Command cmd = cmdCaptor.getValue();
		NpmLogger npmLogger = npmLoggerCaptor.getValue();

		assertThat(workingDirectory).isEqualTo(readPrivate(mojo, "workingDirectory"));
		assertThat(npmLogger).isNotNull();
		assertThat(readPrivate(npmLogger, "log")).isEqualTo(readPrivate(mojo, "log"));
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo(pkg + " " + expectedArgs);
	}

	private void verify_command_execution_ignoring_proxies(T mojo) {
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(readPrivate(mojo, "executor", CommandExecutor.class)).execute(
				any(File.class),
				cmdCaptor.capture(),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
				.doesNotContain("--proxy http://root:password@nginx.local:80")
				.doesNotContain("--https-proxy http://root:password@nginx:80");
	}

	private void verify_command_skipped_has_not_been_logged(T mojo1, String cmd) {
		verify(readPrivate(mojo1, "log", Log.class), never()).info(String.format("Command %s already done, skipping.", cmd));
	}

	private void verify_executor_has_been_run_once(T mojo1) {
		verify(readPrivate(mojo1, "executor", CommandExecutor.class), times(1)).execute(
				any(File.class),
				any(Command.class),
				any(NpmLogger.class),
				ArgumentMatchers.<String, String>anyMap()
		);
	}

	private void verify_command_skipped_has_been_logged_once(T mojo1, String cmd) {
		verify(readPrivate(mojo1, "log", Log.class), times(1)).info(String.format("Command %s already done, skipping.", cmd));
	}

	private void givenFailExecutor(T mojo) {
		givenExecutor(mojo, failureResult());
	}

	private void givenSettingsWithDefaultProxies(T mojo) {
		givenSettings(mojo, newSettings(
				defaultHttpProxy(),
				defaultHttpsProxy()
		));
	}

	private void givenSettings(T mojo, Settings settings) {
		writePrivate(mojo, "settings", settings);
	}

	private void givenExecutor(T mojo, CommandResult result) {
		givenExecutor(readPrivate(mojo, "executor", CommandExecutor.class), result);
	}

	private CommandExecutor givenExecutor(CommandResult result) {
		return givenExecutor(mock(CommandExecutor.class), result);
	}

	private CommandExecutor givenExecutor(CommandExecutor executor, CommandResult result) {
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class), ArgumentMatchers.<String, String>anyMap())).thenReturn(result);
		return executor;
	}
}
