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

package com.github.mjeanroy.maven.plugins.node.commons.io;

import com.github.mjeanroy.maven.plugins.node.exceptions.FileAccessException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Static File Utilities.
 */
public final class Files {

	private Files() {
	}

	/**
	 * Extract absolute path and normalize it, i.e: remove dot and double dot entries.
	 *
	 * @param file The file.
	 * @return The normalized file path.
	 */
	public static String getNormalizeAbsolutePath(File file) {
		if (file == null) {
			return null;
		}

		return normalizeFilePath(file.toPath());
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
	 * Normalize file path, i.e: remove dot and double dot entries.
	 *
	 * @param path The file path.
	 * @return The normalized file path.
	 */
	private static String normalizeFilePath(Path path) {
		return path.toAbsolutePath().normalize().toFile().getAbsolutePath();
	}

	/**
	 * Read and returns all lines of given input file using given charset.
	 * If input file does not exist, an empty list will be returned.
	 *
	 * @param out The file.
	 * @param charset The charset to use.
	 * @return File lines.
	 * @throws FileAccessException If file cannot be read on disk.
	 */
	public static List<String> readLines(File out, Charset charset) {
		if (!out.exists()) {
			return emptyList();
		}

		try {
			return unmodifiableList(java.nio.file.Files.readAllLines(out.toPath(), charset));
		} catch (IOException ex) {
			throw new FileAccessException(ex);
		}
	}

	/**
	 * Write given lines to given file using given charset. If the directory containing the
	 * file does not exist, it will be automatically created.
	 *
	 * @param lines Given lines.
	 * @param out The file.
	 * @param charset The charset to use.
	 * @throws FileAccessException If file cannot be written to disk.
	 */
	public static void writeLines(List<String> lines, File out, Charset charset) {
		if (!createFileDirectory(out)) {
			throw new FileAccessException("Unable to create directory: " + out.getParentFile().getAbsolutePath());
		}

		try {
			java.nio.file.Files.write(out.toPath(), lines, charset);
		} catch (IOException ex) {
			throw new FileAccessException(ex);
		}
	}

	/**
	 * Delete file, and fails if file cannot be deleted.
	 *
	 * @param file The file to delete.
	 * @throws FileAccessException If file cannot be deleted.
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (!file.delete()) {
				throw new FileAccessException("Unable to delete file: " + file);
			}
		}
	}

	/**
	 * Create directory of given file.
	 *
	 * @param file The file.
	 * @return If the directory already exists or has been created successfully.
	 */
	private static boolean createFileDirectory(File file) {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			return dir.mkdirs();
		}

		return true;
	}
}
