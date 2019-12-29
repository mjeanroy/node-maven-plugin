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
import static com.github.mjeanroy.maven.plugins.node.tests.TestUtils.join;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.failureResult;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.CommandResultTestBuilder.successResult;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.ProxyTestBuilder.defaultHttpProxy;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.ProxyTestBuilder.defaultHttpsProxy;
import static com.github.mjeanroy.maven.plugins.node.tests.builders.SettingsTestBuilder.newSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractNpmScriptMojoTest<T extends AbstractNpmScriptMojo> extends AbstractNpmMojoTest<T> {

	private static final String NPM = "npm";
	private static final String YARN = "yarn";

	@Rule
	public ExpectedException thrown = none();

	@Test
	public void test_should_create_mojo() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		assertThat(mojo).isNotNull();
	}

	@Test
	public void test_should_create_mojo_with_configuration() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");
		assertThat(mojo).isNotNull();
		assertThat((Boolean) readPrivate(mojo, "color")).isTrue();
		assertThat((File) readPrivate(mojo, "workingDirectory")).isNotNull();
	}

	@Test
	public void it_should_execute_mojo_in_success() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");
		verify_mojo_success(mojo, NPM);
	}

	@Test
	public void it_should_execute_mojo_with_yarn_in_success() throws Exception {
		T mojo = lookupMojo("mojo-with-yarn");
		verify_mojo_success(mojo, YARN);
	}

	private void verify_mojo_success(T mojo, String pkg) throws Exception {
		mojo.execute();
		verifyMojoExecution(mojo, pkg, join(defaultArguments(true, true)));
	}

	@Test
	public void it_should_execute_mojo_in_success_without_colors() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		mojo.execute();
		verifyMojoExecution(mojo, NPM, join(defaultArguments(false, true)));
	}

	@Test
	public void it_should_execute_mojo_in_success_with_custom_script() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		overrideScript(mojo, "foobar");

		mojo.execute();

		verifyMojoExecution(mojo, NPM, "run foobar --maven");
	}

	@Test
	public void it_should_skip_mojo_execution() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");
		writePrivate(mojo, "skip", true);

		CommandExecutor executor = readPrivate(mojo, "executor");
		Log logger = readPrivate(mojo, "log");

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));
		verify(logger).info(skipMessage());
	}

	@Test
	public void it_should_skip_individual_mojo_execution() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");
		enableSkip(mojo);

		CommandExecutor executor = readPrivate(mojo, "executor");
		Log logger = readPrivate(mojo, "log");

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));
		verify(logger).info(skipMessage());
	}

	@Test
	public void it_should_skip_mojo_execution_if_it_has_been_executed() throws Exception {
		T mojo = lookupMojo("mojo-with-parameters");

		Map<Object, Object> pluginContext = new HashMap<>();
		pluginContext.put(script(), true);
		mojo.setPluginContext(pluginContext);

		CommandExecutor executor = readPrivate(mojo, "executor");
		Log logger = readPrivate(mojo, "log");

		mojo.execute();

		verify(executor, never()).execute(any(File.class), any(Command.class), any(NpmLogger.class));

		String cmd = NPM + (isStandardScript() ? "" : " run") + " " + script();
		verify(logger).info(String.format("Command %s already done, skipping.", cmd));
	}

	@Test
	public void it_should_execute_mojo_in_failure() throws Exception {
		T mojo = lookupMojo("mojo-fail-on-error-false");

		givenFailExecutor(mojo);

		mojo.execute();

		verifyMojoErrorExecution(mojo, NPM, join(defaultArguments(true, true)));
	}

	@Test
	public void it_should_execute_mojo_in_failure_and_throw_exception() throws Exception {
		thrown.expect(MojoExecutionException.class);

		T mojo = lookupMojo("mojo-fail-on-error-true");

		givenFailExecutor(mojo);

		mojo.execute();
	}

	@Test
	public void it_should_throw_exception_if_scripts_does_not_exist() throws Exception {
		if (!isStandardScript()) {
			thrown.expect(MojoExecutionException.class);
			thrown.expectMessage("Cannot execute npm run " + script() + " command: it is not defined in package.json");
		}

		T mojo = lookupMojo("mojo-without-scripts-fail");

		mojo.execute();
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

		CommandExecutor executor = readPrivate(mojo, "executor");
		verify(executor, verificationModeExecutor).execute(any(File.class), any(Command.class), any(NpmLogger.class));
	}

	@Test
	public void it_should_throw_exception_if_package_json_does_not_exist() throws Exception {
		thrown.expect(PackageJsonNotFoundException.class);
		thrown.expectMessage(endsWith("package.json does not exist"));

		lookupMojo("mojo-without-package-json").execute();
	}

	@Test
	public void it_should_add_proxy_configuration() throws Exception {
		T mojo = lookupMojo("mojo-with-proxy");
		givenSettingsWithDefaultProxies(mojo);
		givenFailExecutor(mojo);

		mojo.execute();

		verifyCommandExecutionWithProxy(mojo);
		verifyProxyLogOutput(mojo);
	}

	@Test
	public void it_should_ignore_proxy_configuration() throws Exception {
		T mojo = lookupEmptyMojo("mojo");
		givenSettingsWithDefaultProxies(mojo);

		writePrivate(mojo, "ignoreProxies", true);

		mojo.execute();

		verifyCommandExecutionIgnoringProxies(mojo);
	}

	@Test
	public void it_should_not_add_maven_argument_if_disabled() throws Exception {
		T mojo = lookupMojo("mojo-without-maven-argument");
		mojo.execute();
		verifyMojoExecution(mojo, NPM, join(defaultArguments(true, false)));
	}

	@Override
	T lookupMojo(String projectName) throws Exception {
		return configureMojo(
				super.lookupMojo(projectName)
		);
	}

	@Override
	T lookupEmptyMojo(String projectName) throws Exception {
		return configureMojo(
				super.lookupEmptyMojo(projectName)
		);
	}

	private T configureMojo(T mojo) {
		writePrivate(mojo, "executor", givenExecutor(successResult()));
		return mojo;
	}

	/**
	 * Override mojo script value.
	 *
	 * @param mojo The mojo.
	 * @param script The script value to set.
	 */
	abstract void overrideScript(T mojo, String script);

	/**
	 * Override mojo script value.
	 *
	 * @param mojo The mojo.
	 */
	abstract void enableSkip(T mojo);

	/**
	 * The expected skipped message.
	 *
	 * @return Skipped message.
	 */
	String skipMessage() {
		return String.format("Npm %s is skipped.", script());
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

	private List<String> defaultArguments(boolean withColors, boolean withMaven) {
		List<String> arguments = new ArrayList<>();

		String mojoName = script();

		if (!isStandardScript()) {
			arguments.add("run");
		}

		arguments.add(mojoName);

		if (!withColors) {
			arguments.add("--no-color");
		}

		// Do not forget maven flag
		if (withMaven) {
			arguments.add("--maven");
		}

		return arguments;
	}

	/**
	 * Verify mojo execution.
	 *
	 * @param mojo The mojo to verify.
	 * @param pkg The script runner (i.e npm or yarn).
	 * @param expectedArgs The expected arguments passed to the command executor.
	 */
	private void verifyMojoExecution(T mojo, String pkg, String expectedArgs) {
		verifySuccessOutput(mojo, pkg, expectedArgs);
		verifyCommandExecution(mojo, pkg, expectedArgs);
	}

	/**
	 * Verify mojo execution.
	 *
	 * @param mojo The mojo to verify.
	 * @param pkg The script runner (i.e npm or yarn).
	 * @param expectedArgs The expected arguments passed to the command executor.
	 */
	private void verifyMojoErrorExecution(T mojo, String pkg, String expectedArgs) {
		verifyErrorOutput(mojo, expectedArgs);
		verifyCommandExecution(mojo, pkg, expectedArgs);
	}

	/**
	 * Verify log outputs for a "basic" successful script.
	 *
	 * @param mojo The mojo with the logger to verify.
	 * @param pkg The script runner (i.e npm or yarn).
	 * @param expectedArgs The expected arguments passed to the script runner.
	 */
	private void verifySuccessOutput(T mojo, String pkg, String expectedArgs) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).info("Running: " + pkg + " " + expectedArgs);
		verify(logger, never()).error(anyString());
	}

	/**
	 * Verify log outputs for an errored script.
	 *
	 * @param mojo The mojo with the logger to verify.
	 * @param expectedArgs The expected arguments passed to the script runner.
	 */
	private void verifyErrorOutput(T mojo, String expectedArgs) {
		Log logger = readPrivate(mojo, "log");
		verify(logger).error("Error during execution of: npm " + expectedArgs);
		verify(logger).error("Exit status: 1");
	}


	/**
	 * Verify that proxy configuration is displayed in command output without any password.
	 *
	 * @param mojo The mojo with the logger to verify.
	 */
	private void verifyProxyLogOutput(T mojo) {
		String expectedArgs = join(defaultArguments(true, true));

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

	/**
	 * Verify that the npm/yarn command has been executed with maven proxy configuration.
	 *
	 * @param mojo The mojo with the command executor to verify.s
	 */
	private void verifyCommandExecutionWithProxy(T mojo) {
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(executor).execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class));

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
				.contains("--proxy http://mjeanroy:********@localhost:8080")
				.contains("--https-proxy http://mjeanroy:********@localhost:8080");
	}

	/**
	 * Verify command that has been run.
	 *
	 * @param mojo The mojo with the command executor to verify.
	 * @param pkg The script runner (i.e npm or yarn).
	 * @param expectedArgs The expected arguments passed to the command executor.
	 */
	private void verifyCommandExecution(T mojo, String pkg, String expectedArgs) {
		CommandExecutor executor = readPrivate(mojo, "executor");

		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(executor).execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class));

		Command cmd = cmdCaptor.getValue();
		assertThat(cmd).isNotNull();
		assertThat(cmd.toString()).isEqualTo(pkg + " " + expectedArgs);
	}

	/**
	 * Verify that npm/yarn command has been executed, ignoring maven proxy
	 * configuration.
	 *
	 * @param mojo The mojo with the command executor to verify.
	 */
	private void verifyCommandExecutionIgnoringProxies(T mojo) {
		CommandExecutor executor = readPrivate(mojo, "executor");
		ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);
		verify(executor).execute(any(File.class), cmdCaptor.capture(), any(NpmLogger.class));

		Command command = cmdCaptor.getValue();
		assertThat(command.toString())
				.doesNotContain("--proxy http://root:password@nginx.local:80")
				.doesNotContain("--https-proxy http://root:password@nginx:80");
	}

	/**
	 * Create and override mojo executor with an mock instance that returns a failure result for every command
	 * it will run.
	 *
	 * @param mojo The mojo.
	 * @return The mock executor created by this method.
	 */
	private CommandExecutor givenFailExecutor(T mojo) {
		return givenExecutor(mojo, failureResult());
	}

	/**
	 * Set maven settings with default HTTP & HTTPS proxy on given Mojo.
	 *
	 * @param mojo The mojo.
	 */
	private void givenSettingsWithDefaultProxies(T mojo) {
		givenSettings(mojo, newSettings(
				defaultHttpProxy(),
				defaultHttpsProxy()
		));
	}

	/**
	 * Set maven settings on given Mojo.
	 *
	 * @param mojo The mojo.
	 * @param settings Maven Settings.
	 */
	private void givenSettings(T mojo, Settings settings) {
		writePrivate(mojo, "settings", settings);
	}

	/**
	 * Create and override mojo executor with an mock instance that returns given result for every command
	 * it will run.
	 *
	 * @param mojo The mojo.
	 * @param result The command result returned by this executor.
	 * @return The mock executor created by this method.
	 */
	private CommandExecutor givenExecutor(T mojo, CommandResult result) {
		CommandExecutor executor = readPrivate(mojo, "executor");
		return givenExecutor(executor, result);
	}

	/**
	 * Create new mojo executor with an mock instance that returns given result for every command
	 * it will run.
	 *
	 * @param result The command result returned by this executor.
	 * @return The mock executor created by this method.
	 */
	private CommandExecutor givenExecutor(CommandResult result) {
		CommandExecutor executor = mock(CommandExecutor.class);
		return givenExecutor(executor, result);
	}

	/**
	 * Customizer mojo executor and returns given result for every command
	 * it will run.
	 *
	 * @param executor The executor.
	 * @param result The command result returned by this executor.
	 * @return The mock executor created by this method.
	 */
	private CommandExecutor givenExecutor(CommandExecutor executor, CommandResult result) {
		when(executor.execute(any(File.class), any(Command.class), any(NpmLogger.class))).thenReturn(result);
		return executor;
	}
}
