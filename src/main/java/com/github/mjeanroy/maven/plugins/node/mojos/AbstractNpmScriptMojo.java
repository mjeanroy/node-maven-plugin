/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commands.Commands.npm;
import static com.github.mjeanroy.maven.plugins.node.commons.PreConditions.notNull;
import static com.github.mjeanroy.maven.plugins.node.commons.ProxyUtils.findHttpActiveProfiles;
import static java.util.Collections.unmodifiableSet;

public abstract class AbstractNpmScriptMojo extends AbstractNpmMojo {

	/**
	 * Store standard npm commands.
	 * Theses commands do not need to be prefixed by "run-script"
	 * argument.
	 */
	private static final Set<String> BASIC_COMMANDS;

	// Initialize commands
	static {
		Set<String> cmds = new HashSet<>();
		cmds.add("install");
		cmds.add("test");
		cmds.add("publish");
		BASIC_COMMANDS = unmodifiableSet(cmds);
	}

	/**
	 * Check if given command need to be prefixed by "run-script"
	 * argument.
	 *
	 * @param command Command to check.
	 * @return True if command si a custom command and need to be prefixed by "run-script" argument, false otherwise.
	 */
	private static boolean needRunScript(String command) {
		return !BASIC_COMMANDS.contains(command);
	}

	/**
	 * Flag to check if npm command should use colorization.
	 * Default is false, since colorization is not natively supported with Maven.
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
	 * Should the build fail on missing script command ?
	 * By default, build will fail with if an script command is not defined in package.json file, but this may
	 * be ignored and let the build continue.
	 */
	@Parameter(defaultValue = "true")
	private boolean failOnMissingScript;

	/**
	 * Maven Settings.
	 */
	@Parameter(defaultValue = "true")
	private boolean ignoreProxies;

	/**
	 * Maven Settings.
	 */
	@Parameter(defaultValue = "${settings}", readonly = true)
	private Settings settings;

	/**
	 * Executor used to run command line.
	 */
	private final CommandExecutor executor;

	/**
	 * Default Constructor.
	 */
	protected AbstractNpmScriptMojo() {
		super();
		this.executor = newExecutor();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String script = notNull(getScript(), "Npm Script command must not be null");
		boolean isCustom = needRunScript(script);
		String npmCmd = "npm" + (isCustom ? " run-script " : " ") + script;

		// Command already done ?
		if (hasBeenRun()) {
			// Skip execution.
			getLog().info("Command " + npmCmd + " already executed, skip.");
			return;
		}

		// Should skip ?
		if (isSkipped()) {
			getLog().info(getSkippedMessage());
			return;
		}

		PackageJson packageJson = getPackageJson();

		if (isCustom && !packageJson.hasScript(script)) {
			// This command is not a standard command, and it is not defined in package.json.
			// Fail as soon as possible.
			String message = "Cannot execute " + npmCmd + " command: it is not defined in package.json";
			getLog().warn(message);
			if (failOnMissingScript) {
				throw new MojoExecutionException(message);
			}
		}

		Command cmd = npm();

		// Append "run-script" if needed.
		if (isCustom) {
			cmd.addArgument("run-script");
		}

		cmd.addArgument(script);

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
				cmd.addArgument(proxy.toUri());
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
	 * Check if given script command has already been run.
	 *
	 * @return True if script command has been run, false otherwise.
	 */
	private boolean hasBeenRun() {
		Map pluginContext = getPluginContext();
		String script = getScript();
		return pluginContext != null &&
			pluginContext.containsKey(script) &&
			((Boolean) pluginContext.get(script));
	}

	/**
	 * Executed after command execution.
	 *
	 * @param status If command execution has been executed, false otherwise.
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
	protected abstract String getScript();

	/**
	 * Check if mojo execution should be skipped.
	 *
	 * @return True if mojo execution should be skipped, false otherwise.
	 */
	protected abstract boolean isSkipped();

	/**
	 * Message logged when mojo execution is skipped.
	 *
	 * @return Message.
	 */
	protected String getSkippedMessage() {
		return String.format("Npm %s is skipped.", getScript());
	}

	/**
	 * Execute given command.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private void executeCommand(Command cmd) throws MojoExecutionException {
		CommandResult result = executor.execute(getWorkingDirectory(), cmd, getLog());
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
