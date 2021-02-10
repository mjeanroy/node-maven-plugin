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

package com.github.mjeanroy.maven.plugins.node.loggers;

import com.github.mjeanroy.maven.plugins.node.commands.OutputHandler;

/**
 * Create a logger that process {@code npm} command output and redirect
 * everything to the standard output.
 */
public class SystemOutLogger extends AbstractNpmLogger implements OutputHandler {

	/**
	 * Create new NPM logger using an existing maven logger.
	 *
	 * @return The NPM logger.
	 */
	public static SystemOutLogger systemOutLogger() {
		return new SystemOutLogger();
	}

	/**
	 * Create the NPM logger.
	 */
	private SystemOutLogger() {
	}

	@Override
	void warn(String line) {
		sysOut(line);
	}

	@Override
	void error(String line) {
		sysOut(line);
	}

	@Override
	void info(String line) {
		sysOut(line);
	}

	private void sysOut(String line) {
		System.out.println(line == null ? "" : line);
	}
}
