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
import com.github.mjeanroy.maven.plugins.node.model.PackageJson;
import com.github.mjeanroy.maven.plugins.node.model.ProxyConfig;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commons.io.Files.getNormalizeAbsolutePath;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.maven.plugins.node.commons.mvn.MvnUtils.findHttpActiveProfiles;
import static java.util.Collections.unmodifiableSet;

abstract class AbstractNpmScriptMojo extends AbstractNpmMojo {

	private static final String NPM_INSTALL = "install";
	private static final String NPM_TEST = "test";
	private static final String NPM_PUBLISH = "publish";
	private static final String NPM_START = "start";
	private static final String NPM_PRUNE = "prune";

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
			add(NPM_PRUNE);
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
	 * Should the `--maven` argument be added on npm/yarn commands.
	 * By default, this argument is automatically added to be able to know that maven triggered
	 * the command during the build.
	 */
	@Parameter(defaultValue = "true")
	private boolean addMavenArgument;

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
	 * Skip NPM script globally.
	 */
	@Parameter(defaultValue = "${npm.skip}")
	private boolean skip;

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
		this.color = false;
		this.addMavenArgument = true;
		this.failOnError = true;
		this.failOnMissingScript = true;
		this.ignoreProxies = true;
		this.executor = newExecutor();
	}

	@Override
	public void execute() throws MojoExecutionException {
		String scriptToRun = getScriptToRun(false);
		boolean isYarn = isUseYarn();
		Command cmd = isYarn ? yarn() : npm();

		if (needRunScript(scriptToRun)) {
			cmd.addArgument("run");
		}

		cmd.addArgument(scriptToRun);

		// Command already done ?
		if (hasBeenRunPreviously()) {
			getLog().info("Command " + cmd.toString() + " already done, skipping.");
			return;
		}

		// Should skip?
		if (skip || shouldSkip()) {
			getLog().info(getSkippedMessage());
			return;
		}

		File packageJsonFile = lookupPackageJson();
		PackageJson packageJson = parsePackageJson(packageJsonFile);
		if (needRunScript(scriptToRun) && !packageJson.hasScript(scriptToRun)) {
			handleMissingNpmScript(cmd, packageJsonFile);
			return;
		}

		if (!color) {
			cmd.addArgument("--no-color");
		}

		// Add maven flag
		// This will let any script known that execution is triggered by maven
		if (addMavenArgument) {
			cmd.addArgument("--maven");
		}

		// Should we add proxy ?
		if (!ignoreProxies) {
			List<ProxyConfig> activeProxies = findHttpActiveProfiles(settings.getProxies());
			for (ProxyConfig proxy : activeProxies) {
				cmd.addArgument(proxy.isSecure() ? "--https-proxy" : "--proxy");
				cmd.addArgument(proxy);
			}
		}

		doExecute(cmd);
	}

	/**
	 * Execute command.
	 *
	 * @param cmd The command to execute.
	 * @throws MojoExecutionException If something bad happened.
	 */
	private void doExecute(Command cmd) throws MojoExecutionException {
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
	 * Handle missing NPM script:
	 *
	 * <ul>
	 *   <li>Fail if {@link #failOnMissingScript} is {@code true}</li>
	 *   <li>Simply log a warning otherwise.</li>
	 * </ul>
	 *
	 * @param cmd The command to run.
	 * @param packageJsonFile The {@code package.json} file.
	 * @throws MojoExecutionException Thrown if {@link #failOnMissingScript} is {@code true}.
	 */
	private void handleMissingNpmScript(Command cmd, File packageJsonFile) throws MojoExecutionException {
		// This command is not a standard command, and it is not defined in package.json.
		// Fail as soon as possible.
		String message = "Cannot execute " + cmd.toString() + " command: it is not defined in package.json (please check file: " + packageJsonFile.getAbsolutePath() + ")";

		if (failOnMissingScript) {
			getLog().error(message + ".");
			throw new MojoExecutionException(message);
		}

		// Do not fail, but log warning
		getLog().warn(message + ", skipping.");
	}

	/**
	 * Get the script to run.
	 *
	 * @param silent Disable log output to warn about script deprecation.
	 * @return Script name to run.
	 */
	private String getScriptToRun(boolean silent) {
		if (this.script != null) {
			if (!silent) {
				getLog().warn("Parameter `script` has been deprecated to avoid conflict issues, please use `" + getScriptParameterName() + "` instead");
			}

			return this.script;
		}

		return notNull(getScript(), "Script command must not be null");
	}

	/**
	 * Check if given script command has already been run.
	 *
	 * @return {@code true} if script command has been run, {@code false} otherwise.
	 */
	private boolean hasBeenRunPreviously() {
		Map pluginContext = getPluginContext();
		if (pluginContext == null) {
			return false;
		}

		String taskId = currentTaskId();

		getLog().debug("Checking if task '" + taskId + "' has been already executed");

		return pluginContext.containsKey(taskId) && ((Boolean) pluginContext.get(taskId));
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

		String taskId = currentTaskId();

		getLog().debug("Storing execution of: '" + taskId + "'");

		pluginContext.put(taskId, status);
		setPluginContext(pluginContext);
	}

	private String currentTaskId() {
		String script = getScriptToRun(true);
		String project = getNormalizeAbsolutePath(getWorkingDirectory());
		return project + "::" + script;
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
	abstract boolean shouldSkip();

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
			handleFailure(cmd, result);
		} else {
			handleSuccess(cmd);
		}
	}

	/**
	 * Display a success log if execution succeed.
	 *
	 * @param cmd The command.
	 */
	private void handleSuccess(Command cmd) {
		Log log = getLog();
		if (log.isDebugEnabled()) {
			log.debug("Execution succeed: " + cmd.toString());
		}
	}

	/**
	 * Handle command execution failure:
	 *
	 * <ol>
	 *   <li>Log an error.</li>
	 *   <li>Fail if {@link #failOnError} is {@code true}.</li>
	 * </ol>
	 *
	 * @param cmd The command that failed.
	 * @param result The result status
	 * @throws MojoExecutionException If
	 */
	private void handleFailure(Command cmd, CommandResult result) throws MojoExecutionException {
		Log log = getLog();
		String rawCmd = cmd.toString();

		log.error("Error during execution of: " + rawCmd);
		log.error("Exit status: " + result.getStatus());

		// Throw exception if npm command does not succeed
		if (failOnError) {
			throw new MojoExecutionException("Error during: " + rawCmd);
		}
	}
}
