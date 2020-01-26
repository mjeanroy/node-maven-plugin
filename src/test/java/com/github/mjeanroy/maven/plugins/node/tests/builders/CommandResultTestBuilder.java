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

package com.github.mjeanroy.maven.plugins.node.tests.builders;

import com.github.mjeanroy.maven.plugins.node.commands.CommandResult;

/**
 * Fluent Builder for {@link CommandResult}
 */
public class CommandResultTestBuilder {

	/**
	 * Create {@link CommandResult} with a success status.
	 *
	 * @return The {@link CommandResult} instance.
	 */
	public static CommandResult successResult() {
		return new CommandResultTestBuilder().withSuccess().build();
	}

	/**
	 * Create {@link CommandResult} with a success status.
	 *
	 * @param out The command output.
	 * @return The {@link CommandResult} instance.
	 */
	public static CommandResult successResult(String out) {
		return new CommandResultTestBuilder().withSuccess().withOut(out).build();
	}

	/**
	 * Create {@link CommandResult} with a failure status.
	 *
	 * @return The {@link CommandResult} instance.
	 */
	public static CommandResult failureResult() {
		return new CommandResultTestBuilder().withFailure().build();
	}

	/**
	 * The result status.
	 */
	private boolean success;

	/**
	 * The command output.
	 */
	private String out;

	/**
	 * Create builder with default values.
	 */
	public CommandResultTestBuilder() {
		this.success = true;
		this.out = "";
	}

	/**
	 * Update result status to be a success.
	 *
	 * @return The builder.
	 */
	public CommandResultTestBuilder withSuccess() {
		this.success = true;
		return this;
	}

	/**
	 * Update result status to be a failure.
	 *
	 * @return The builder.
	 */
	public CommandResultTestBuilder withFailure() {
		this.success = false;
		return this;
	}

	/**
	 * Set {@link #out}
	 *
	 * @param out New {@link #out}
	 * @return The builder.
	 */
	public CommandResultTestBuilder withOut(String out) {
		this.out = out;
		return this;
	}

	/**
	 * Build {@link CommandResult} instance.
	 *
	 * @return The {@link CommandResult} instance.
	 */
	public CommandResult build() {
		int status = success ? 0 : 1;
		return new CommandResult(status, out);
	}
}
