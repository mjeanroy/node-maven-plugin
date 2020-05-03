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

package com.github.mjeanroy.maven.plugins.node.commands;

import com.github.mjeanroy.maven.plugins.node.commons.lang.ToStringBuilder;

import java.io.File;
import java.util.Objects;

import static com.github.mjeanroy.maven.plugins.node.commons.lang.Objects.firstNonNull;

/**
 * Store result of a command line execution.
 *
 * <p>
 *
 * Result contains a status:
 * <ul>
 *   <li>A success is a command exiting with zero.</li>
 *   <li>A failure is a command exiting with everything but zero.</li>
 * </ul>
 *
 * This class should not be instantiated explicitly, but should be obtained with
 * the result of {@link CommandExecutor#execute(File, Command, OutputHandler, java.util.Map)}.
 *
 * <p>
 *
 * This class is immutable and, consequently, thread safe.
 */
public final class CommandResult {

	/**
	 * Exit Status.
	 */
	private final int status;

	/**
	 * The command output.
	 */
	private final String out;

	/**
	 * Create new result object.
	 *
	 * @param status Status value.
	 * @param out Command output.
	 */
	public CommandResult(int status, String out) {
		this.status = status;
		this.out = firstNonNull(out, "");
	}

	/**
	 * Get the exit status.
	 *
	 * @return Exit status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get {@link #out}
	 *
	 * @return {@link #out}
	 */
	public String getOut() {
		return out;
	}

	/**
	 * Check if result is a success: this is a shortcut for checking if {@link #status} is equal
	 * to zero.
	 *
	 * @return {@code true} if result is a success, {@code false} otherwise.
	 */
	public boolean isSuccess() {
		return status == 0;
	}

	/**
	 * Check if result is a failure: this is a shortcut for checking if {@link #status} is not equal
	 * to zero.
	 *
	 * @return {@code true} if result is a failure, {@code false} otherwise.
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
			return Objects.equals(status, r.status) && Objects.equals(out, r.out);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, out);
	}

	@Override
	public String toString() {
		return ToStringBuilder.builder(getClass())
				.append("status", status)
				.append("out", out)
				.build();
	}
}
