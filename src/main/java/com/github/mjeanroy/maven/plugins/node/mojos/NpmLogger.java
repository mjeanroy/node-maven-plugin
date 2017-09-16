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

import com.github.mjeanroy.maven.plugins.node.commands.OutputHandler;
import org.apache.maven.plugin.logging.Log;

/**
 * Create a logger that process {@code npm} command output and redirect
 * the line to a maven logger with the appropriate log level.
 *
 * <h3>How is the log level detected?</h3>
 *
 * The log level is detected using {@code npm} output:
 *
 * <ul>
 *   <li>If the output starts with {@code "npm ERR! "}, then the level {@code ERROR} will be used.</li>
 *   <li>It the output starts with {@code "npm WARN "}, then the level {@code WARN} will be used.</li>
 *   <li>Otherwise, the level {@code INFO} is used by default.</li>
 * </ul>
 */
class NpmLogger implements OutputHandler {

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
	 * Create new NPM logger using an existing maven logger.
	 *
	 * @param logger Maven logger.
	 * @return The NPM logger.
	 */
	static NpmLogger npmLogger(Log logger) {
		return new NpmLogger(logger);
	}

	/**
	 * Maven logger.
	 */
	private final Log log;

	/**
	 * Create the NPM logger.
	 *
	 * @param log The maven logger.
	 */
	private NpmLogger(Log log) {
		this.log = log;
	}

	@Override
	public void process(String line) {
		if (line.startsWith(NPM_WARN_PREFIX)) {
			log.warn(line);
		} else if (line.startsWith(NPM_ERROR_PREFIX)) {
			log.error(line);
		} else {
			log.info(line);
		}
	}
}
