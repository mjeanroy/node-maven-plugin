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
import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;
import com.github.mjeanroy.maven.plugins.node.commons.io.Files;
import com.github.mjeanroy.maven.plugins.node.commons.io.Ios;
import com.github.mjeanroy.maven.plugins.node.commons.lang.Strings;
import com.github.mjeanroy.maven.plugins.node.model.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.github.mjeanroy.maven.plugins.node.commands.CommandExecutors.newExecutor;
import static com.github.mjeanroy.maven.plugins.node.commons.io.Files.getNormalizeAbsolutePath;
import static com.github.mjeanroy.maven.plugins.node.commons.io.Ios.urlEncode;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.maven.plugins.node.commons.lang.Strings.trim;
import static com.github.mjeanroy.maven.plugins.node.commons.mvn.MvnUtils.findHttpActiveProfiles;
import static java.util.Arrays.asList;
import static java.util.Collections.*;

abstract class AbstractNpmScriptMojo extends AbstractNpmMojo {

	private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);

	private static final String NPM_INSTALL = "install";
	private static final String NPM_TEST = "test";
	private static final String NPM_PUBLISH = "publish";
	private static final String NPM_START = "start";
	private static final String NPM_PRUNE = "prune";
	private static final String NPM_CI = "ci";

	/**
	 * Store standard commands.
	 * Theses commands do not need to be prefixed by {@code "run"}
	 * argument.
	 */
	private static final Set<String> BASIC_COMMANDS;

	/**
	 * Specific standard commands for given npm client.
	 */
	private static final Map<String, Set<String>> CLIENT_BASIC_COMMANDS;

	/**
	 * The separator used to split file name and hash signature in persisted file.
	 */
	private static final String INPUT_STATE_SEPARATOR = "::";

	// Initialize commands
	static {
		BASIC_COMMANDS = unmodifiableSet(new HashSet<>(asList(
			NPM_INSTALL,
			NPM_TEST,
			NPM_PUBLISH,
			NPM_START,
			NPM_PRUNE
		)));

		CLIENT_BASIC_COMMANDS = Collections.singletonMap(
				"npm", singleton(NPM_CI)
		);
	}

	/**
	 * Check if given command need to be prefixed by {@code "run"} argument.
	 *
	 * @param command Command to check.
	 * @return {@code true} if command is a custom command and need to be prefixed by {@code "run"} argument, {@code false} otherwise.
	 */
	private static boolean needRunScript(String executable, String command) {
		if (BASIC_COMMANDS.contains(command)) {
			return false;
		}

		if (executable != null && !executable.isEmpty()) {
			String npmClient = executable.toLowerCase();
			if (CLIENT_BASIC_COMMANDS.containsKey(npmClient) && CLIENT_BASIC_COMMANDS.get(npmClient).contains(command)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Flag to check if {@code npm} command should use colorization.
	 * Default is {@code false}, since colorization is not natively supported with Maven.
	 */
	@Parameter(defaultValue = "true")
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
	@Parameter(defaultValue = "false")
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
	 * Set {@code clean} mojo to custom npm script.
	 *
	 * @deprecated
	 */
	@Parameter
	@Deprecated
	private String script;

	@Parameter
	private IncrementalBuildConfiguration incrementalBuild;

	@Parameter
	private LockStrategyConfiguration lockStrategies;

	/**
	 * Default Constructor.
	 */
	AbstractNpmScriptMojo() {
		super(newExecutor());
		this.color = true;
		this.addMavenArgument = true;
		this.failOnError = true;
		this.failOnMissingScript = true;
		this.ignoreProxies = true;
		this.incrementalBuild = new IncrementalBuildConfiguration();
		this.lockStrategies = new LockStrategyConfiguration();
	}

	@Override
	public final void execute() throws MojoExecutionException {
		Log log = getLog();

		String scriptToRun = getScriptToRun(false);
		Command cmd = npmClient();

		String[] parts = scriptToRun.split(" ");
		String cmdToRun = parts[0];
		boolean addRunScript = needRunScript(cmd.getName(), cmdToRun);

		if (addRunScript) {
			log.debug("Adding run prefix for custom script command");
			cmd.addArgument("run");
		}

		log.debug("Using '" + cmdToRun + "' command");
		cmd.addArgument(cmdToRun);

		// Add custom arguments
		for (int i = 1; i < parts.length; ++i) {
			String arg = trim(parts[i]);
			if (arg != null && !arg.isEmpty()) {
				cmd.addArgument(arg);
				log.debug("Adding custom argument: " + arg);
			}
		}

		// Should skip?
		if (shouldSkipGlobally() || shouldSkip()) {
			log.info(getSkippedMessage(cmd));
			return;
		}

		// Command already done during build?
		if (hasBeenRunPreviously()) {
			log.info("Command " + cmd.toString() + " already done, skipping.");
			return;
		}

		// Command already executed by a previous build without any changes?
		Map<String, String> previousState = readPreviousState();
		Map<String, String> newState = readCurrentState();

		if (!previousState.isEmpty() && Objects.equals(previousState, newState)) {
			log.info("Command " + cmd.toString() + " already done, no changes detected, skipping.");
			return;
		}
		else if (log.isDebugEnabled()) {
			printIncrementalBuildDiff(previousState, newState);
		}

		File packageJsonFile = lookupPackageJson();
		PackageJson packageJson = parsePackageJson(packageJsonFile);
		if (addRunScript && !packageJson.hasScript(cmdToRun)) {
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

		// Try to be smart here: some goal, such as install, needs to acquire an exclusive lock as in a workspace
		// project (with yarn, pnpm or npm >= 7), the install goal must never be run in parallel, otherwise it may triggers
		// unexpected results.
		// The goal that must not be run in parallel are:
		// - Install
		// - PreClean, as it runs npm install
		// - Bower, as it also install dependencies.
		// For all the other goal, it should be ok to be run in parallel (except if force in configuration),
		// as it should only alter file of the current maven module.
		getLog().debug("Getting lock strategy, according to configuration: " + lockStrategies);

		LockStrategy lockStrategy = firstNonNull(
				lockStrategies.getStrategy(getGoalName()),
				lockStrategy()
		);

		getLog().debug("Acquiring lock with strategy: " + lockStrategy);

		Lock acquiredLock = lockStrategy.getLock(lock);

		acquiredLock.lock();

		try {
			doExecute(cmd, newState);
		}
		finally {
			acquiredLock.unlock();
		}
	}

	/**
	 * Get the default lock strategy to use in this mojo.
	 *
	 * @return Lock Strategy, never {@code null}.
	 */
	abstract LockStrategy lockStrategy();

	/**
	 * Get the goal name.
	 *
	 * @return Goal name.
	 */
	abstract String getGoalName();

	/**
	 * Return script to execute.
	 *
	 * @return Script to execute.
	 */
	abstract String getScript();

	/**
	 * Check if mojo execution should be skipped.
	 *
	 * @return {@code true} if mojo execution should be skipped, {@code false} otherwise.
	 */
	abstract boolean shouldSkip();

	/**
	 * Get all default input files that will be used to compute current state during incremental build.
	 *
	 * @return Input entries.
	 */
	Collection<String> getDefaultIncrementalBuildIncludes() {
		return emptySet();
	}

	/**
	 * Get all default exclusions for to computing current state during incremental build.
	 *
	 * @return Input entries.
	 */
	Collection<String> getDefaultIncrementalBuildExcludes() {
		return emptySet();
	}

	/**
	 * Message logged when mojo execution is skipped.
	 *
	 * @param cmd The command to skip.
	 * @return Message.
	 */
	String getSkippedMessage(Command cmd) {
		return "Command '" + cmd.toString() + "' is skipped.";
	}

	/**
	 * Execute command.
	 *
	 * @param cmd The command to execute.
	 * @throws MojoExecutionException If something bad happened.
	 */
	private void doExecute(Command cmd, Map<String, String> state) throws MojoExecutionException {
		getLog().info("Running: " + cmd.toString());

		try {
			executeCommand(cmd);
			onRun(true);
			storeInputState(state);
		}
		catch (RuntimeException | MojoExecutionException ex) {
			onRun(false);
			throw ex;
		}
	}

	/**
	 * Return the script parameter to be able to display a useful log.
	 *
	 * @return Script parameter name.
	 */
	private String getScriptParameterName() {
		String klassName = getClass().getSimpleName();
		String name = Strings.uncapitalize(
				klassName.substring(0, klassName.length() - 4)
		);

		return name + "Script";
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
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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

	/**
	 * Get the current task identifier, basically the script to run in given working directory.
	 *
	 * @return Task identifier.
	 */
	private String currentTaskId() {
		String script = getScriptToRun(true);
		String project = getNormalizeAbsolutePath(getWorkingDirectory());
		return project + "::" + script;
	}

	/**
	 * Read mojo input states that has been computed during a previous build.
	 * If the state cannot be computed, an empty map will be returned.
	 *
	 * There can be a lot of reasons why last build state cannot be retrieved:
	 * <ul>
	 *   <li>Incremental build is disabled.</li>
	 *   <li>Mojo has never been executed.</li>
	 *   <li>Input state does not exist anymore on disk (if {@code mvn clean} has been runned)</li>
	 *   <li>Or for any other reasons.</li>
	 * </ul>
	 *
	 * @return The previous build state.
	 */
	private Map<String, String> readPreviousState() {
		Log log = getLog();
		log.debug("Reading previous input state");

		if (isIncrementalBuildDisabled()) {
			log.debug("Incremental build is disabled, skipping.");
			return emptyMap();
		}

		File stateFile = getInputStateFile();
		if (!stateFile.exists()) {
			log.debug("Input state file does not exist, skipping.");
			return emptyMap();
		}

		Charset charset = getCharset();
		List<String> lines = Files.readLines(stateFile, charset);
		if (lines.isEmpty()) {
			log.debug("Input state file is empty, skipping.");
			return emptyMap();
		}

		Map<String, String> state = new LinkedHashMap<>();
		for (String line : lines) {
			String[] parts = line.split(INPUT_STATE_SEPARATOR, 2);
			String path = parts[0];
			String hash = parts[1];

			log.debug("Found previous input '" + path + "' with signature: " + hash);

			state.put(path, hash);
		}

		return unmodifiableMap(state);
	}

	/**
	 * Read current mojo state, i.e:
	 *
	 * <ol>
	 *   <li>Scan input files.</li>
	 *   <li>Compute a signature for each file that have been detected.</li>
	 * </ol>
	 *
	 * @return Input states.
	 */
	private Map<String, String> readCurrentState() {
		Log log = getLog();
		log.debug("Reading current input state");

		if (isIncrementalBuildDisabled()) {
			log.debug("Incremental build is disabled, skipping.");
			return emptyMap();
		}

		Set<File> inputs = scanInputFiles();
		if (inputs.isEmpty()) {
			log.debug("No input files detected, skipping.");
			return emptyMap();
		}

		Map<String, String> state = new LinkedHashMap<>();
		for (File file : inputs) {
			if (file.exists()) {
				String path = Files.getNormalizeAbsolutePath(file);
				String hash = Ios.md5(file);

				log.debug("Storing input state of '" + path + "' with signature: " + hash);

				state.put(path, hash);
			}
		}

		return unmodifiableMap(state);
	}

	/**
	 * Store mojo input state on disk.
	 *
	 * @param state Current mojo state.
	 */
	private void storeInputState(Map<String, String> state) {
		if (isIncrementalBuildDisabled()) {
			return;
		}

		writeState(
				serializeState(state)
		);
	}

	/**
	 * Serialize input states (i.e md5 signature of all input files) to a line that will be written
	 * on disk and re-used in a next build.
	 *
	 * @param state Inputs state.
	 * @return Serialized state.
	 */
	private List<String> serializeState(Map<String, String> state) {
		Log log = getLog();
		List<String> lines = new ArrayList<>(state.size());

		for (Map.Entry<String, String> entry : state.entrySet()) {
			String path = entry.getKey();
			String hash = entry.getValue();

			log.debug("Serializing state: '" + path + "' with hash: " + hash);

			lines.add(path + INPUT_STATE_SEPARATOR + hash);
		}

		return lines;
	}

	/**
	 * Write mojo input state to given file that will be read in a next build to implement
	 * incremental build.
	 *
	 * @param lines Lines to write.
	 */
	private void writeState(List<String> lines) {
		Log log = getLog();
		File stateFile = getInputStateFile();
		Charset charset = getCharset();

		log.debug("Delete mojo state file: '" + stateFile + "'");
		Files.deleteFile(stateFile);

		log.debug("Storing mojo state to '" + stateFile + "' using charset: " + charset);
		if (!lines.isEmpty()) {
			log.debug("Writing state: " + lines);
			Files.writeLines(lines, stateFile, charset);
		}
	}

	/**
	 * Get charset to use to read and write file on disk.
	 *
	 * @return Charset to use.
	 */
	private Charset getCharset() {
		return StandardCharsets.UTF_8;
	}

	/**
	 * Get the file storing the mojo input states.
	 *
	 * @return The input state file.
	 */
	private File getInputStateFile() {
		String fName = getScriptToRun(true);
		String encodedName = urlEncode(fName);
		return Files.join(getWorkingDirectory(), "target", "node-maven-plugin", encodedName);
	}

	/**
	 * Scan all input files that will be used for computing state during incremental build.
	 *
	 * @return Input files to compute.
	 */
	private Set<File> scanInputFiles() {
		if (isIncrementalBuildDisabled()) {
			return emptySet();
		}

		return scanFiles();
	}

	/**
	 * Scan given input files and extract all existing files.
	 *
	 * <p>
	 *
	 * Given inputs can be defined as:
	 *
	 * <ul>
	 *   <li>An exact path (relative to the working directory), for example: {@code "/package.json"}</li>
	 *   <li>A pattern (relative to the working directory), for example: *.json</li>
	 * </ul>
	 *
	 * @return The set of files.
	 */
	private Set<File> scanFiles() {
		Log log = getLog();

		File baseDir = getWorkingDirectory();
		DirectoryScanner directoryScanner = new DirectoryScanner();
		directoryScanner.setBasedir(baseDir);
		directoryScanner.setIncludes(includes().toArray(new String[0]));
		directoryScanner.setExcludes(excludes().toArray(new String[0]));
		directoryScanner.scan();

		Set<File> inputFiles = new LinkedHashSet<>();
		for (String selectedFile : directoryScanner.getIncludedFiles()) {
			log.debug("Selecting input file: " + selectedFile);
			inputFiles.add(new File(baseDir, selectedFile));
		}

		return inputFiles;
	}

	/**
	 * Get set of files to be excluded in incremental build computation.
	 *
	 * @return Set of files to be included.
	 */
	private Set<String> excludes() {
		Set<String> excludes = new LinkedHashSet<>();

		// NPM/YARN dependencies
		excludes.add("**/node_modules/**/*");

		// Backend Source Files (main or test)
		if (incrementalBuild.isExcludeBackendSources()) {
			for (String lang : asList("java", "kotlin", "scala", "groovy")) {
				excludes.add("src/main/" + lang + "/**/*");
				excludes.add("src/test/" + lang + "/**/*");
			}
		}

		// Build output
		excludes.add("**/target/**/*");

		String goal = getGoalName();
		if (incrementalBuild.useDefaultExcludes(goal)) {
			excludes.addAll(getDefaultIncrementalBuildExcludes());
		}

		excludes.addAll(incrementalBuild.getExcludes(goal));
		return excludes;
	}

	/**
	 * Get set of files to be included in incremental build computation.
	 *
	 * @return Set of files to be included.
	 */
	private Set<String> includes() {
		Set<String> includes = new LinkedHashSet<>();

		String goal = getGoalName();
		if (incrementalBuild.useDefaultIncludes(goal)) {
			includes.addAll(getDefaultIncrementalBuildIncludes());
		}

		includes.addAll(incrementalBuild.getIncludes(goal));
		return includes;
	}

	/**
	 * Execute given command.
	 *
	 * @param cmd Command Line.
	 * @throws MojoExecutionException In case of errors.
	 */
	private void executeCommand(Command cmd) throws MojoExecutionException {
		CommandResult result = execute(cmd);
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

	/**
	 * Print each changes that triggered this task.
	 * This is useful for incremental build to understand why some changes have been detected for
	 * given task.
	 *
	 * @param previousState The previous state.
	 * @param newState The new state.
	 */
	private void printIncrementalBuildDiff(Map<String, String> previousState, Map<String, String> newState) {
		Log log = getLog();

		// Print a diff of what has changed for easier debugging
		log.debug("Checking what has changed since previous build...");

		for (Map.Entry<String, String> previousEntry : previousState.entrySet()) {
			String path = previousEntry.getKey();
			if (!newState.containsKey(path)) {
				log.debug("  - File '" + path + "' has been removed");
			} else if (!Objects.equals(previousEntry.getValue(), newState.get(path))) {
				log.debug("  - File '" + path + "' has changed");
			}
		}

		for (Map.Entry<String, String> newEntry : newState.entrySet()) {
			String path = newEntry.getKey();
			if (!previousState.containsKey(path)) {
				log.debug("  - File '" + path + "' has been added");
			}
		}
	}

	/**
	 * Check if incremental build is enabled for current mojo.
	 *
	 * @return {@code true} if incremental build, {@code false} otherwise.
	 */
	private boolean isIncrementalBuildDisabled() {
		return !incrementalBuild.isEnabled() || !incrementalBuild.isEnabled(getGoalName());
	}
}
