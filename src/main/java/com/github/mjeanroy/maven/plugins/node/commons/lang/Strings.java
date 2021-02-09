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

package com.github.mjeanroy.maven.plugins.node.commons.lang;

import java.util.Collection;

/**
 * Static String Utilities.
 */
public class Strings {

	// Ensure non instantiation.
	private Strings() {
	}

	/**
	 * Trim value in a null-safe way.
	 *
	 * @param value Value to trim.
	 * @return Trimmed value, maybe {@code null}.
	 */
	public static String trim(String value) {
		return value == null ? value : value.trim();
	}

	/**
	 * Join given inputs to a single output string.
	 *
	 * @param lines The inputs.
	 * @param separator The join separator.
	 * @return The output string.
	 */
	public static String join(Collection<String> lines, String separator) {
		if (lines.isEmpty()) {
			return "";
		}

		if (lines.size() == 1) {
			return lines.iterator().next();
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String line : lines) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}

			sb.append(line);
		}

		return sb.toString();
	}

	/**
	 * Turn a string to a capitalized string.
	 *
	 * @param text String to capitalized.
	 * @return Capitalized string.
	 */
	public static String capitalize(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	/**
	 * Turn a string to a non capitalized string.
	 *
	 * @param text String to un-capitalized.
	 * @return Non capitalized string.
	 */
	public static String uncapitalize(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}

	/**
	 * Put given placeholder at the left of the string, until it gets the required size.
	 *
	 * @param input Input string.
	 * @param length Required length.
	 * @param placeholder Placeholder.
	 * @return The output string.
	 */
	public static String leftPad(String input, int length, char placeholder) {
		int size = input == null ? 0 : input.length();
		if (size >= length) {
			return input;
		}

		StringBuilder sb = new StringBuilder(length);
		while (sb.length() < length - size) {
			sb.append(placeholder);
		}

		if (input != null) {
			sb.append(input);
		}

		return sb.toString();
	}
}
