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

package com.github.mjeanroy.maven.plugins.node.commands;

/**
 * Store result of a command line execution.
 * Result contains a status:
 * - A success is a command exiting with zero.
 * - A failure is a command exiting with everything but zero.
 *
 * This class should not be instantiated explicitly, but should be obtaining with
 * the result of {@link com.github.mjeanroy.maven.plugins.node.commands.CommandExecutor#execute(java.io.File, Command, org.apache.maven.plugin.logging.Log)}.
 *
 * This class is immutable and, consequently, thread safe.
 */
public class CommandResult {

	/**
	 * Exit Status.
	 */
	private final int status;

	/**
	 * Create new result object.
	 *
	 * @param status Status value.
	 */
	CommandResult(int status) {
		this.status = status;
	}

	/**
	 * Get exit status.
	 *
	 * @return Exit status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Check if result is a success: this is a shortcut for checking if {@link #status} is equal
	 * to zero.
	 *
	 * @return True if result is a success, false otherwise.
	 */
	public boolean isSuccess() {
		return status == 0;
	}

	/**
	 * Check if result is a failure: this is a shortcut for checking if {@link #status} is not equal
	 * to zero.
	 *
	 * @return True if result is a failure, false otherwise.
	 */
	public boolean isFailure() {
		return status != 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof CommandResult) {
			CommandResult r = (CommandResult) o;
			return status == r.status;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return status;
	}

	@Override
	public String toString() {
		return String.format("Status: %s", status);
	}
}
