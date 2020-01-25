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

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Static Test File Utilities.
 */
public final class FileTestUtils {

	// Ensure non instantiation.
	private FileTestUtils() {
	}

	/**
	 * Get file, relatively to the root of the classpath.
	 *
	 * @param input File path.
	 * @return The file.
	 */
	public static File getFileFromClasspath(String input) {
		try {
			return Paths.get(FileTestUtils.class.getResource(input).toURI()).toFile();
		}
		catch (URISyntaxException ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Create new file instance by joining each parts.
	 *
	 * @param root The root file.
	 * @param path The sub-path.
	 * @param others Other (optional) sub-path.
	 * @return The file.
	 */
	public static File join(File root, String path, String... others) {
		File out = new File(root, path);
		for (String other : others) {
			out = new File(out, other);
		}

		return out;
	}

	/**
	 * Get absolute path of given file.
	 *
	 * @param path The path.
	 * @return The absolute path.
	 */
	public static String absolutePath(String path) {
		return new File(path).getAbsolutePath();
	}
}
