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

package com.github.mjeanroy.maven.plugins.node.commons.io;

import com.github.mjeanroy.maven.plugins.node.exceptions.FileAccessException;
import com.github.mjeanroy.maven.plugins.node.exceptions.Md5Exception;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static I/O Utilities.
 */
public final class Ios {

	// Ensure non instantiation.
	private Ios() {
	}

	/**
	 * Compute MD5 hash of given file.
	 *
	 * @param file The file.
	 * @return The MD5 hash.
	 */
	public static String md5(File file) {
		MessageDigest md5 = getMd5Digest();
		byte[] bytes = read(file);
		byte[] hash = md5.digest(bytes);
		return new BigInteger(1, hash).toString(16);
	}

	/**
	 * Compute MD5 hashes of given files.
	 *
	 * @param files Given input files.
	 * @return The hash entries.
	 */
	public static Map<String, String> md5(Collection<File> files) {
		Map<String, String> hashes = new LinkedHashMap<>();
		for (File file : files) {
			String path = Files.getNormalizeAbsolutePath(file);
			String hash = md5(file);
			hashes.put(path, hash);
		}

		return hashes;
	}

	/**
	 * Get the MD5 digest algorithm.
	 *
	 * @return MD5 Digest Instance.
	 */
	private static MessageDigest getMd5Digest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			throw new Md5Exception(ex);
		}
	}

	/**
	 * Read given file as a byte array.
	 *
	 * @param file The file.
	 * @return The file content.
	 */
	private static byte[] read(File file) {
		try {
			return java.nio.file.Files.readAllBytes(file.toPath());
		} catch (IOException ex) {
			throw new FileAccessException(ex);
		}
	}
}
