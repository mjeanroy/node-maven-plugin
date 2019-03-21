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
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.*;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commons.PreConditions.notNull;
import static com.github.mjeanroy.maven.plugins.node.commons.ProxyUtils.findHttpActiveProfiles;
import static java.util.Collections.unmodifiableSet;

abstract class AbstractNpmScriptMojo extends AbstractNpmMojo {

	private static final String NPM_INSTALL = "install";
	private static final String NPM_TEST = "test";
	private static final String NPM_PUBLISH = "publish";
	private static final String NPM_START = "start";

	/**
	 * Store standard {@code nom} commands.
	 * Theses commands do not need to be prefixed by {@code "run"}
	 * argument.
	 */
	private static final Set<String> BASIC_COMMANDS;

	// Initialize commands
	static {
		BASIC_COMMANDS = unmodifiableSet(new HashSet<String>() {{
			add(NPM_INSTALL);
			add(NPM_TEST);
			add(NPM_PUBLISH);
			add(NPM_START);
		}});
	}

	/**
	 * Check if given command need to be prefixed by {@code "run"}
	 * argument.
	 *
	 * @param command Command to check.
	 * @return {@code true} if command si a custom command and need to be prefixed by {@code "run"} argument, {@code false} otherwise.
	 */
	private static boolean needRunScript(String command) {
		return !BASIC_COMMANDS.contains(command);
	}

	/**
	 * Flag to check if {@code npm} command should use colorization.
	 * Default is {@code false}, since colorization is not natively supported with Maven.
	 */
	@Parameter(defaultValue = "false")
	private boolean color;

	/**
	 * Should the build fail on error ?
	 * By default, build will fail with if an error occurs, but this may
	 * be ignored and let the build continue.
	 */
	@Parameter(defaultValue = "true")
	private boolean failOnError;

	/**
	 * Should the build fail on missing script command?
	 * By default, build will fail with if a script command that is not defined in {@code package.json} file, but this may
	 * be ignored and let the build continue.
	 */
	@Parameter(defaultValue = "true")
	private boolean failOnMissingScript;

	/**
	 * Should proxies be ignored?
	 * Default is {@code true} since proxy may probably be defined in {@code .npmrc} file.
	 */
	@Parameter(defaultValue = "true")
	private boolean ignoreProxies;

	/**
	 * Maven Settings.
	 */
	@Parameter(defaultValue = "${settings}", readonly = true)
	private Settings settings;

	/**
	 * Set {@code clean} mojo to custom npm script.
	 *
	 * @deprecated
	 */
	@Parameter
	@Deprecated
	private String script;

	/**
	 * Executor used to run command line.
	 */
	private final CommandExecutor executor;

	/**
	 * Default Constructor.
	 */
	AbstractNpmScriptMojo() {
		super();
		this.executor = newExecutor();
	}

	@Override
	public void execute() throws MojoExecutionException {
		String scriptToRun = getScriptToRun();
		boolean isYarn = isUseYarn();
		Command cmd = isYarn ? yarn() : npm();

		if (needRunScript(scriptToRun)) {
			cmd.addArgument("run");
		}

		cmd.addArgument(scriptToRun);

		// Command already done ?
		if (hasBeenRun()) {
			// Skip execution.
			getLog().info("Command " + cmd.toString() + " already done, skipping.");
			return;
		}

		// Should skip ?
		if (isSkipped()) {
			getLog().info(getSkippedMessage());
			return;
		}

		File packageJsonFile = lookupPackageJson();
		PackageJson packageJson = parsePackageJson(packageJsonFile);

		if (needRunScript(scriptToRun) && !packageJson.hasScript(scriptToRun)) {
			// This command is not a standard command, and it is not defined in package.json.
			// Fail as soon as possible.
			String message = "Cannot execute " + cmd.toString() + " command: it is not defined in package.json (please check file: " + packageJsonFile.getAbsolutePath() + ")";
			if (failOnMissingScript) {
				getLog().error(message + ".");
				throw new MojoExecutionException(message);
			} else {
				// Do not fail, but log warning
				getLog().warn(message + ", skipping.");
				return;
			}
		}

		if (!color) {
			cmd.addArgument("--no-color");
		}

		// Add maven flag
		// This will let any script known that execution is triggered by maven
		cmd.addArgument("--maven");

		// Should we add proxy ?
		if (!ignoreProxies) {
			List<ProxyConfig> activeProxies = findHttpActiveProfiles(settings.getProxies());
			for (ProxyConfig proxy : activeProxies) {
				cmd.addArgument(proxy.isSecure() ? "--https-proxy" : "--proxy");
				cmd.addArgument(proxy);
			}
		}

		getLog().info("Running: " + cmd.toString());

		try {
			executeCommand(cmd);
			onRun(true);
		}
		catch (RuntimeException | MojoExecutionException ex) {
			onRun(false);
			throw ex;
		}
	}

	/**
	 * Get the script to run.
	 *
	 * @return Script name to run.
	 */
	private String getScriptToRun() {
		if (this.script != null) {
			getLog().warn("Parameter `script` has been deprecated to avoid conflict issues, please use `" + getScriptParameterName() + "` instead");
			return this.script;
		}

		return notNull(getScript(), "Script command must not be null");
	}

	/**
	 * Check if given script command has already been run.
	 *
	 * @return {@code true} if script command has been run, {@code false} otherwise.
	 */
	private boolean hasBeenRun() {
		Map pluginContext = getPluginContext();
		String script = getScript();
		return pluginContext != null && pluginContext.containsKey(script) && ((Boolean) pluginContext.get(script));
	}

	/**
	 * Executed after command execution.
	 *
	 * @param status If command execution has been executed.
	 */
	@SuppressWarnings("unchecked")
	private void onRun(boolean status) {
		Map pluginContext = getPluginContext();
		if (pluginContext == null) {
			pluginContext = new HashMap();
		}

		pluginContext.put(getScript(), status);
		setPluginContext(pluginContext);
	}

	/**
	 * Return script to execute.
	 *
	 * @return Script to execute.
	 */
	abstract String getScript();

	/**
	 * Return the script parameter to be able to display a useful log.
	 *
	 * @return Script parameter name.
	 */
	abstract String getScriptParameterName();

	/**
	 * Check if mojo execution should be skipped.
	 *
	 * @return {@code true} if mojo execution should be skipped, {@code false} otherwise.
	 */
	abstract boolean isSkipped();

	/**
	 * Message logged when mojo execution is skipped.
	 *
	 * @return Message.
	 */
	String getSkippedMessage() {
		return String.format("Npm %s is skipped.", getScript());
	}

	/**
	 * Execute given command.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private void executeCommand(Command cmd) throws MojoExecutionException {
		CommandResult result = executor.execute(getWorkingDirectory(), cmd, npmLogger());
		if (result.isFailure()) {
			// Always display error log
			getLog().error("Error during execution of: " + cmd.toString());
			getLog().error("Exit status: " + result.getStatus());

			// Throw exception if npm command does not succeed
			if (failOnError) {
				throw new MojoExecutionException("Error during: " + cmd.toString());
			}
		} else {
			// Display success log
			getLog().debug("Execution succeed: " + cmd.toString());
		}
	}
}
