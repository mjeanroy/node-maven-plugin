package com.github.mjeanroy.maven.plugins.node.loggers;

import com.github.mjeanroy.maven.plugins.node.commands.OutputHandler;

abstract class AbstractNpmLogger implements OutputHandler {

	/**
	 * The {@code npm} warn prefix used when warning are displayed by
	 * npm script.
	 */
	private static final String NPM_WARN_PREFIX = "npm WARN ";

	/**
	 * The {@code npm} error prefix used when errors are displayed by
	 * npm script.
	 */
	private static final String NPM_ERROR_PREFIX = "npm ERR! ";

	/**
	 * The {@code npm} warn prefix used when warning are displayed by
	 * npm script.
	 */
	private static final String YARN_WARN_PREFIX = "warning ";

	/**
	 * The {@code yarn} error prefix used when errors are displayed by
	 * yarn script.
	 */
	private static final String YARN_ERROR_PREFIX = "error ";

	/**
	 * The {@code webpack} prefix to display a warning.
	 * For example:
	 * <ul>
	 *   <li>{@code "WARNING in webpack performance recommendations:"}</li>
	 *   <li>{@code "WARNING in entrypoint size limit:"}</li>
	 * </ul>
	 */
	public static final String WARNING_PREFIX = "WARNING ";

	/**
	 * A deprecation warning displayed when npm detects a deprecated dependency.
	 */
	public static final String DEPRECATION_WARNING = "DeprecationWarning: ";

	/**
	 * Create the logger.
	 */
	AbstractNpmLogger() {
	}

	@Override
	public final void process(String line) {
		if (shouldWarn(line)) {
			warn(line);
		} else if (line.startsWith(NPM_ERROR_PREFIX) || line.startsWith(YARN_ERROR_PREFIX)) {
			error(line);
		} else {
			info(line);
		}
	}

	abstract void warn(String line);

	abstract void error(String line);

	abstract void info(String line);

	private static boolean shouldWarn(String line) {
		if (line.startsWith(NPM_WARN_PREFIX) || line.startsWith(YARN_WARN_PREFIX)) {
			return true;
		}

		if (line.startsWith(WARNING_PREFIX)) {
			return true;
		}

		return line.contains(DEPRECATION_WARNING);
	}
}
