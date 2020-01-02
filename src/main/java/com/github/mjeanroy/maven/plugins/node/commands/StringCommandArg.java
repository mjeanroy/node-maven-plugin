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

/**
 * Simple string argument.
 */
class StringCommandArg implements CommandArg {

	/**
	 * Create new string command argument.
	 *
	 * @param arg Original argument.
	 * @return Command argument.
	 */
	static CommandArg arg(String arg) {
		return new StringCommandArg(arg);
	}

	/**
	 * Original argument.
	 */
	private final String arg;

	/**
	 * Build String Argument.
	 *
	 * @param arg String argument.
	 */
	private StringCommandArg(String arg) {
		this.arg = arg;
	}

	@Override
	public String toArgument() {
		return arg;
	}

	@Override
	public String toString() {
		return arg;
	}
}
