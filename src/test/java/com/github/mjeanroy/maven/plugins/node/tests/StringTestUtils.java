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

package com.github.mjeanroy.maven.plugins.node.tests;

import java.util.Collection;

/**
 * Static Test Utilities.
 */
public final class StringTestUtils {

	// Ensure non instantiation.
	private StringTestUtils() {
	}

	/**
	 * Join collection of strings into a single string, each item being separated by a space.
	 *
	 * @param collection The collection of strings.
	 * @return The final string.
	 */
	public static String join(Collection<String> collection) {
		return join(collection, " ");
	}

	/**
	 * Join collection of strings into a single string, each item being separated by given separator.
	 *
	 * @param collection The collection of strings.
	 * @param separator The separator to use.
	 * @return The final string.
	 */
	public static String join(Collection<String> collection, String separator) {
		StringBuilder sb = new StringBuilder();
		for (String arg : collection) {
			sb.append(arg).append(separator);
		}

		return sb.toString().trim();
	}
}